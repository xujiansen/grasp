package lib.grasp.widget.recyclerview.group.entity;

import java.util.ArrayList;

/**
 * Created by GaQu_Dev on 2018/11/19.
 */
public interface GroupInte {

    String getHeader();

    void setHeader(String header);

    String getFooter();

    void setFooter(String footer);

    ArrayList<SampleChildEntity> getChildren();

    void setChildren(ArrayList<SampleChildEntity> children);
}
