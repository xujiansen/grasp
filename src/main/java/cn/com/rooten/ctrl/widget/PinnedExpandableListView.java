package cn.com.rooten.ctrl.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import lib.grasp.R;


public class PinnedExpandableListView extends FrameLayout implements AbsListView.OnScrollListener, View.OnClickListener {
    private ExpandableListView mExpandableListView;
    private LinearLayout mPinnedRootView;
    private GroupViewHolder mPinnedGroupHolder;
    private boolean mPinnedEnable = true;

    public PinnedExpandableListView(Context context) {
        super(context);
        init();
    }

    public PinnedExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinnedExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        int wrap_content = LayoutParams.WRAP_CONTENT;
        int match_parent = LayoutParams.MATCH_PARENT;

        mExpandableListView = (ExpandableListView) LayoutInflater.from(getContext()).inflate(R.layout.expandable_list_divider, null);
        mPinnedRootView = new LinearLayout(getContext());

        addView(mExpandableListView, match_parent, match_parent);
        addView(mPinnedRootView, match_parent, wrap_content);

        // 初始化mPinnedView
        mPinnedRootView.setOrientation(LinearLayout.VERTICAL);
        mPinnedRootView.setVisibility(View.GONE);

        // 不需要系统默认的图标
        mExpandableListView.setGroupIndicator(null);

        // 设置滚动事件
        mExpandableListView.setOnScrollListener(this);

        // 滚动条
        mExpandableListView.setVerticalScrollBarEnabled(false);
    }

    public void toggleGroup(int groupPosition) {
        boolean isExpanded = mExpandableListView.isGroupExpanded(groupPosition);
        if (isExpanded) mExpandableListView.collapseGroup(groupPosition);
        else mExpandableListView.expandGroup(groupPosition, false);
    }

    public void setPinnedEnable(boolean enable) {
        mPinnedEnable = enable;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (!mPinnedEnable) return;

        ExpandableListAdapter baseAdapter = mExpandableListView.getExpandableListAdapter();
        if (baseAdapter == null || !(baseAdapter instanceof Adapter)) return;

        Adapter adapter = (Adapter) baseAdapter;

        // 创建并添加Pinned视图
        addPinnedView(adapter);

        // 初始化当前Pinned的View
        View curPinnedItemView = mPinnedGroupHolder.itemView;

        final long packedPosition = mExpandableListView.getExpandableListPosition(firstVisibleItem);
        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

        int groupCount = adapter.getGroupCount();
        int childCount = adapter.getChildrenCount(groupPosition);

        // 判断是否第一个是分组项
        boolean isGroupInFirst = groupPosition != -1 && childPosition == -1;

        if (!isGroupInFirst) {
            boolean isLastGroupPosition = groupPosition == groupCount - 1;
            if (isLastGroupPosition) return;

            int nextGroupPosition = groupPosition + 1;
            long nextGroupPackedPosition = ExpandableListView.getPackedPositionForGroup(nextGroupPosition);
            int nextGroupFlatPosition = mExpandableListView.getFlatListPosition(nextGroupPackedPosition);
            int nextGroupChildIndex = nextGroupFlatPosition - firstVisibleItem;
            if (nextGroupFlatPosition == -1 || nextGroupChildIndex < 0) return;

            // 这边的getChildAt-getChildCount都只是当前屏幕上显示的子布局
            View nextGroupItemView = view.getChildAt(nextGroupChildIndex);
            if (nextGroupItemView == null) return;

            int pinnedBottom = curPinnedItemView.getBottom();   // 悬浮的视图的底部
            int nextGroupTop = nextGroupItemView.getTop();      // 当前屏幕上显示的最先一个分组的顶部
            if (nextGroupTop < pinnedBottom)                    // 当下一个分组的顶部小于悬浮视图的底部
            {
                // 显示Pinned
                int tranY = pinnedBottom - nextGroupTop;
                curPinnedItemView.setTag(-1);
                mPinnedRootView.scrollTo(0, tranY);
                boolean flag = Math.abs(tranY) > 0 && Math.abs(tranY) < pinnedBottom;
                mPinnedRootView.setVisibility(flag ? VISIBLE : GONE);

                if (mPinnedRootView.getVisibility() != VISIBLE) return;
                curPinnedItemView.setTag(groupPosition);
                boolean isExpanded = mExpandableListView.isGroupExpanded(groupPosition);
                mPinnedGroupHolder.loadData(groupPosition, isExpanded);
                return;
            }
        }

        if (!mExpandableListView.isGroupExpanded(groupPosition) || childCount <= 0) {
            curPinnedItemView.setTag(-1);
            mPinnedRootView.setVisibility(GONE);
            return;
        }

        mPinnedRootView.scrollTo(0, 0);
        mPinnedRootView.setVisibility(VISIBLE);
        curPinnedItemView.setTag(groupPosition);
        mPinnedGroupHolder.loadData(groupPosition, true);
    }

    /**
     * 是否添加Pinned的视图
     */
    private boolean hasAddPinnedView() {
        return mPinnedRootView.getChildCount() == 2;
    }

    private void addPinnedView(Adapter adapter) {
        if (adapter == null || hasAddPinnedView()) return;

        // 先移除所有子视图
        mPinnedRootView.removeAllViews();

        // 创建Pinned的视图
        mPinnedGroupHolder = adapter.createGroupViewHolder();
        mPinnedGroupHolder.itemView.setOnClickListener(this);

        final View pinnedContent = mPinnedGroupHolder.itemView;
        final ImageView pinnedLine = new ImageView(getContext());

        int w = LinearLayout.LayoutParams.MATCH_PARENT;
        int m = LinearLayout.LayoutParams.WRAP_CONTENT;
        mPinnedRootView.addView(pinnedContent, w, m);
        mPinnedRootView.addView(pinnedLine, w, 1);

        // 初始化参数
        pinnedLine.setScaleType(ImageView.ScaleType.FIT_XY);
        pinnedLine.setImageDrawable(getLineDrawable());
    }

    @Override
    public void onClick(View v) {
        Object tagObj = v.getTag();
        if (!(tagObj instanceof Integer)) return;

        int groupPosition = (Integer) tagObj;
        if (groupPosition < 0) return;

        // 收缩分组数据
        mExpandableListView.collapseGroup(groupPosition);
    }

    public void setAdapter(Adapter adapter) {
        if (adapter == null) return;
        mExpandableListView.setAdapter(adapter);
    }

    private Drawable getLineDrawable() {
        final int[] ATTRS = new int[]{android.R.attr.listDivider};
        final TypedArray a = getContext().obtainStyledAttributes(ATTRS);
        Drawable d = a.getDrawable(0);
        a.recycle();
        return d;
    }

    public abstract static class Adapter extends BaseExpandableListAdapter {
        private LruCache<View, GroupViewHolder> mGroupHolderMap = new LruCache<>(20);
        private LruCache<View, ChildViewHolder> mChildHolderMap = new LruCache<>(20);

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * 子视图是否可点击
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        public GroupViewHolder findGroupHolderByView(View itemView) {
            return mGroupHolderMap.get(itemView);
        }

        public ChildViewHolder findChildHolderByView(View itemView) {
            return mChildHolderMap.get(itemView);
        }

        @Override
        final public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                GroupViewHolder groupHolder = createGroupViewHolder();
                convertView = groupHolder.itemView;
                mGroupHolderMap.put(convertView, groupHolder);
            }

            GroupViewHolder holder = mGroupHolderMap.get(convertView);
            if (holder == null) return convertView;
            holder.loadData(groupPosition, isExpanded);
            return convertView;
        }

        @Override
        final public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ChildViewHolder childHolder = createChildViewHolder();
                convertView = childHolder.itemView;
                mChildHolderMap.put(convertView, childHolder);
            }

            ChildViewHolder holder = mChildHolderMap.get(convertView);
            if (holder == null) return convertView;
            holder.loadData(groupPosition, childPosition, isLastChild);
            return convertView;
        }

        /**
         * 创建分组视图
         */
        public abstract GroupViewHolder createGroupViewHolder();

        /**
         * 创建子项内容视图
         */
        public abstract ChildViewHolder createChildViewHolder();
    }

    public static class GroupViewHolder {
        public View itemView = null;

        public GroupViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public void loadData(int groupPosition, boolean isExpanded) {

        }
    }

    public static class ChildViewHolder {
        public View itemView = null;

        public ChildViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public void loadData(int groupPosition, int childPosition, boolean isLastChild) {

        }
    }
}
