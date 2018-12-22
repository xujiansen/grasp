package lib.grasp.widget.recyclerview.group.entity;

import java.util.ArrayList;

/**
 * 组数据的实体类
 */
public class SampleEntity implements GroupInte {

    private String header;
    private String footer;
    private ArrayList<SampleChildEntity> children;

    public SampleEntity(String header, String footer, ArrayList<SampleChildEntity> children) {
        this.header = header;
        this.footer = footer;
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
    public ArrayList<SampleChildEntity> getChildren() {
        return children;
    }

    @Override
    public void setChildren(ArrayList<SampleChildEntity> children) {
        this.children = children;
    }
}
