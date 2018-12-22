package lib.grasp.widget.diaglog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by JS_grasp on 2018/12/19.
 */
public class CheckMultiEntity {
    public String id;
    public String name;
    /** 是否选中 */
    public boolean isCheck;

    public CheckMultiEntity(String id, String name, boolean isCheck) {
        this.id = id;
        this.name = name;
        this.isCheck = isCheck;
    }

    public static List<CheckMultiEntity> getTestDatas(){
        ArrayList<CheckMultiEntity> list = new ArrayList<>();
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项1", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项2", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项3", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项4", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项5", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项6", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项7", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项8", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项9", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项10", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项11", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项12", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项13", false));
        list.add(new CheckMultiEntity(UUID.randomUUID().toString(), "选项14", false));
        return list;
    }
}
