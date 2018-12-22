package lib.grasp.widget.recyclerview.group.entity;

import java.util.ArrayList;

/**
 * 可展开收起的组数据的实体类 它比GroupEntity只是多了一个boolean类型的isExpand，用来表示展开和收起的状态。
 */
public class SampleExpandableGroupEntity implements ExpandableGroupInte{

    private String header;
    private String footer;
    private ArrayList<SampleChildEntity> children;
    private boolean isExpand;

    public SampleExpandableGroupEntity(String header, String footer, boolean isExpand, ArrayList<SampleChildEntity> children) {
        this.header = header;
        this.footer = footer;
        this.isExpand = isExpand;
        this.children = children;
    }

    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String getFooter() {
        return footer;
    }

    @Override
    public void setFooter(String footer) {
        this.footer = footer;
    }

    @Override
    public boolean isExpand() {
        return isExpand;
    }

    @Override
    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    @Override
    public ArrayList<SampleChildEntity> getChildren() {
        return children;
    }

    @Override
    public void setChildren(ArrayList<SampleChildEntity> children) {
        this.children = children;
    }
}
