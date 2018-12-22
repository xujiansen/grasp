package lib.grasp.entity.biz_entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色
 */
public class Role {
    public static final String ARG_NAME         = Role.class.getSimpleName();
    public static final String ARG_LIST_NAME    = Role.class.getSimpleName() + "LIST";
    public static final String ARG_ROLE_ID      = Role.class.getSimpleName() + "ID";

    /** 保洁员 */
    public static final String ROLE_CLEANKEEPER     = "1";

    /** 物业负责人 */
    public static final String ROLE_PROPERTYCHARGER = "2";

    /** 硬件检修技术员 */
    public static final String ROLE_HARDWAREPEOPLE  = "3";

    /** 贩卖配送员 */
    public static final String ROLE_DISTRIBUTOR     = "4";

    /** 广告管理员 */
    public static final String ROLE_ADVMANAGER      = "5";

    /** 标识管理员 */
    public static final String ROLE_LOGOMANAGER     = "6";


    @SerializedName("roleId")
    /** 角色ID */
    public String roleId;

    @SerializedName("roleName")
    /** 角色名称 */
    public String roleName;

    /** 角色图片资源 */
    public int picRes = -1;

    public Role(String roleId, String name, int picRes) {
        this.roleId = roleId;
        this.roleName = name;
        this.picRes = picRes;
    }

    public static ArrayList<Role> getTestDatas() {
        ArrayList<Role> list = new ArrayList<>();
        list.add(new Role(ROLE_CLEANKEEPER, "保洁员", -1));
        list.add(new Role(ROLE_PROPERTYCHARGER, "物业负责人", -1));
        list.add(new Role(ROLE_HARDWAREPEOPLE, "硬件检修技术员", -1));
        list.add(new Role(ROLE_DISTRIBUTOR, "贩卖配送员", -1));
        list.add(new Role(ROLE_ADVMANAGER, "广告管理员", -1));
        list.add(new Role(ROLE_LOGOMANAGER, "标识管理员", -1));
        return list;
    }


    /** 获取角色全称 */
    public static String getRoleText(String roleId){
        switch (roleId){
            case ROLE_CLEANKEEPER:{
                return "保洁员";
            }
            case ROLE_PROPERTYCHARGER:{
                return "物业负责人";
            }
            case ROLE_HARDWAREPEOPLE:{
                return "硬件检修技术员";
            }
            case ROLE_DISTRIBUTOR:{
                return "贩卖配送员";
            }
            case ROLE_ADVMANAGER:{
                return "广告管理员";
            }
            case ROLE_LOGOMANAGER:{
                return "标识管理员";
            }
            default:{
                return "管理人员";
            }
        }
    }

    /** 获取角色简称 */
    public String getRoleSimpleText(){
        switch (roleId){
            case ROLE_CLEANKEEPER:{
                return "洁";
            }
            case ROLE_PROPERTYCHARGER:{
                return "业";
            }
            case ROLE_HARDWAREPEOPLE:{
                return "修";
            }
            case ROLE_DISTRIBUTOR:{
                return "送";
            }
            case ROLE_ADVMANAGER:{
                return "广";
            }
            case ROLE_LOGOMANAGER:{
                return "标";
            }
            default:{
                return "嘎";
            }
        }
    }

    /** 是否只有一个角色 */
    public static boolean isOnlyOneRole(List<Role> list){
        return list != null && list.size() == 1;
    }

}
