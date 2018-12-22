package lib.grasp.widget.recyclerview.group.model;

import java.util.ArrayList;

import lib.grasp.widget.recyclerview.group.entity.ExpandableGroupInte;
import lib.grasp.widget.recyclerview.group.entity.GroupInte;
import lib.grasp.widget.recyclerview.group.entity.SampleChildEntity;
import lib.grasp.widget.recyclerview.group.entity.SampleEntity;
import lib.grasp.widget.recyclerview.group.entity.SampleExpandableGroupEntity;

/**
 * Depiction:
 * Author: teach
 * Date: 2017/3/20 15:51
 */
public class GroupModel {

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<GroupInte> getGroups1(int groupCount, int childrenCount) {
        ArrayList<GroupInte> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<SampleChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new SampleChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new SampleEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", children));
        }
        return groups;
    }

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<GroupInte> getGroups(int groupCount, int childrenCount) {
        ArrayList<GroupInte> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<SampleChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new SampleChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new SampleEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", children));
        }
        return groups;
    }

    /**
     * 获取可展开收起的组列表数据(默认展开)
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     * @return
     */
    public static ArrayList<ExpandableGroupInte> getExpandableGroups(int groupCount, int childrenCount) {
        ArrayList<ExpandableGroupInte> groups = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<SampleChildEntity> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new SampleChildEntity("第" + (i + 1) + "组第" + (j + 1) + "项"));
            }
            groups.add(new SampleExpandableGroupEntity("第" + (i + 1) + "组头部",
                    "第" + (i + 1) + "组尾部", true, children));
        }
        return groups;
    }

}
