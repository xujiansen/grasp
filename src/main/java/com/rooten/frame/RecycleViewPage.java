package com.rooten.frame;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

import lib.grasp.R;
import com.rooten.ctrl.widget.SimpleItemDecoration;
import com.rooten.ctrl.widget.SwipeRefreshLayout;
import com.rooten.util.Utilities;

public class RecycleViewPage extends BasePage implements SwipeRefreshLayout.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    protected View mRootView;

    protected SwipeRefreshLayout mRefreshLayout;
    protected RecyclerView mRecyclerView;

    public RecycleViewPage(Context context) {
        super(context);
        createView();
    }

    private void createView() {
        mRootView = View.inflate(mContext, R.layout.swip_recycle_page, null);

        initRefreshLayout();
        initRecyclerView();
    }

    private void initRefreshLayout() {
        mRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swip_recycle_page_refresh);
        mRefreshLayout.setTopColor(android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
        mRefreshLayout.setBottomColor(android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
        mRefreshLayout.setMode(SwipeRefreshLayout.Mode.DISABLED);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadListener(this);
    }

    public void setRefreshMode(SwipeRefreshLayout.Mode mode) {
        mRefreshLayout.setMode(mode);
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.swip_recycle_page_recycle);

        showLinearView();       // 使用线性的布局方式
        addItemDecoration();    // 添加间隔线

        // 设置Item的内容改变时的动画是否支持
        RecyclerView.ItemAnimator itemAnimator = mRecyclerView.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
    }

    public void addItemDecoration() {
        if (mRecyclerView == null) return;

        RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
        if (!(lm instanceof LinearLayoutManager)) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) lm;
        Drawable d = mContext.getResources().getDrawable(R.drawable.ic_divider_list);
        SimpleItemDecoration divider = new SimpleItemDecoration(d, layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
    }

    public void stopRefresh() {
        mRefreshLayout.setRefreshing(false);
    }

    public void stopLoad() {
        mRefreshLayout.setLoading(false);
    }


    /**
     * 网格状视图
     */
    public void showGridView() {
        GridLayoutManager gridManager = new GridLayoutManager(mContext, 3);
        gridManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridManager);
    }

    /**
     * 水平视图
     */
    public void showLinearView() {
        LinearLayoutManager layoutManage = new LinearLayoutManager(mContext);
        layoutManage.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManage);
    }

    @Override
    public View getView() {
        return mRootView;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoad() {

    }

    public abstract static class BaseHolder extends RecyclerView.ViewHolder {
        public BaseHolder(View itemView) {
            super(itemView);
        }

        public abstract void loadData(int position);
    }

    public abstract static class BaseAdapter extends RecyclerView.Adapter<BaseHolder> {
        @Override
        public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BaseHolder holder = createViewHolder();

            int w = RecyclerView.LayoutParams.WRAP_CONTENT;
            int m = RecyclerView.LayoutParams.MATCH_PARENT;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m, w);
            holder.itemView.setLayoutParams(params);

            return holder;
        }

        protected void setPad(BaseHolder holder, int position) {
            boolean isLowLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;

            int padTopAndBottom = isLowLollipop ? 6 : 9;
            int padLeftAndRight = isLowLollipop ? 9 : 9;

            Context context = holder.itemView.getContext();
            padTopAndBottom = Utilities.getValueByDpi(context, padTopAndBottom);
            padLeftAndRight = Utilities.getValueByDpi(context, padLeftAndRight);

            int dataSize = getItemCount();
            boolean isLast = position == dataSize - 1;
            int padBottom = isLast ? padTopAndBottom : 0;
            holder.itemView.setPadding(padLeftAndRight, padTopAndBottom, padLeftAndRight, padBottom);
        }

        @Override
        public void onBindViewHolder(BaseHolder holder, int position) {
            holder.loadData(position);
        }

        protected abstract BaseHolder createViewHolder();
    }

    private ArrayList getItemDecorations() {
        try {
            Field mItemDecorationsField = mRecyclerView.getClass().getDeclaredField("mItemDecorations");
            mItemDecorationsField.setAccessible(true);
            Object obj = mItemDecorationsField.get(mRecyclerView);
            if (!(obj instanceof ArrayList)) return new ArrayList();

            return (ArrayList) obj;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public void clearLine() {
        ArrayList list = getItemDecorations();
        list.clear();
    }
}
