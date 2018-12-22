package lib.grasp.entity.biz_entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫码返回仓体
 */
public class Cabin {
    public static final String ARG_NAME         = Cabin.class.getSimpleName();
    public static final String ARG_LIST_NAME    = Cabin.class.getSimpleName() + "LIST";



    @SerializedName("cangId")
    /** 舱体编号 */
    public String cabinId;

    @SerializedName("cangName")
    /** 舱体名称 */
    public String cabinName;

    @SerializedName("supRoleEntityList")
    /** 用户对于该舱体所拥有的角色 */
    public List<Role> roleList;

    public Cabin(String cabinId, String cabinName, List<Role> roleList) {
        this.cabinId = cabinId;
        this.cabinName = cabinName;
        this.roleList = roleList;
    }

    /** 测试数据 */
    public static List<Cabin> getTestDatas() {

        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role(Role.ROLE_CLEANKEEPER, "保洁员", -1));
        roleList.add(new Role(Role.ROLE_PROPERTYCHARGER, "物业负责人", -1));
        roleList.add(new Role(Role.ROLE_HARDWAREPEOPLE, "硬件检修技术员", -1));

        List<Cabin> list = new ArrayList<>();
        list.add(new Cabin("0001", "舱位0001", roleList));
        list.add(new Cabin("0002", "舱位0002", roleList));
        list.add(new Cabin("0003", "舱位0003", roleList));
        list.add(new Cabin("0004", "舱位0004", roleList));
        list.add(new Cabin("0005", "舱位0005", roleList));
        list.add(new Cabin("0006", "舱位0006", roleList));
        return list;
    }

    /** 是否只有一个仓位 */
    public static boolean isOnlyOneCabin(List<Cabin> list){
        return list != null && list.size() == 1;
    }

    /** 按CabinId在列表查找指定 */
    public static Cabin getCabinFromList(List<Cabin> list, String cabinId){
        if(list == null || list.size() == 0 || TextUtils.isEmpty(cabinId)) return null;
        for(Cabin temp : list){
            if(TextUtils.equals(temp.cabinId, cabinId)) return temp;
        }
        return null;
    }
}
