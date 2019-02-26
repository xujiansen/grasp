package lib.grasp.widget.diaglog;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 选择
 */
public class RadioOneEntity {
    public String id;
    public String name;

    private Object tag;

    public RadioOneEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RadioOneEntity(String id, String name, Object tag) {
        this.id = id;
        this.name = name;
        this.tag = tag;
    }

    public static List<RadioOneEntity> getTestDatas(){
        ArrayList<RadioOneEntity> list = new ArrayList<>();
        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项1"));
        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项2"));
        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项3"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项4"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项5"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项6"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项7"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项8"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项9"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项10"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项11"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项12"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项13"));
//        list.add(new RadioOneEntity(UUID.randomUUID().toString(), "选项14"));
        return list;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
