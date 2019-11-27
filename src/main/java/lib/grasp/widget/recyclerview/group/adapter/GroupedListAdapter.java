package lib.grasp.widget.recyclerview.group.adapter;

import android.content.Context;

import java.util.ArrayList;

import lib.grasp.R;
import lib.grasp.widget.recyclerview.group.adapter.base.GroupedRecyclerViewAdapter;
import lib.grasp.widget.recyclerview.group.entity.GroupInte;
import lib.grasp.widget.recyclerview.group.entity.SampleChildEntity;
import lib.grasp.widget.recyclerview.group.holder.BaseViewHolder;

/**
 * 这是普通的分组Adapter 每一个组都有头部、尾部和子项。
 */
public class GroupedListAdapter extends GroupedRecyclerViewAdapter {

    private ArrayList<GroupInte> mGroups;

    public GroupedListAdapter(Context context, ArrayList<GroupInte> groups) {
        super(context);
        mGroups = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroups == null ? 0 : mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<SampleChildEntity> children = mGroups.get(groupPosition).getChildren();
        return children == null ? 0 : children.size();
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return true;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.adapter_footer;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.adapter_child;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupInte entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_header, "-" + entity.getHeader() + "-");
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupInte entity = mGroups.get(groupPosition);
        holder.setText(R.id.tv_footer, "-" + entity.getFooter() + "-");
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        SampleChildEntity entity = mGroups.get(groupPosition).getChildren().get(childPosition);
        holder.setText(R.id.tv_child, entity.getChild());
    }
}
