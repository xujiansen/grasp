package com.rooten.help.permission;


import android.Manifest;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 * 所有危险的需要申请授权权限
 */

@SuppressWarnings("NewApi")
public class AppDangerousPermission {
    public static final Map<String, String> PermissionMap = new HashMap<>();

    static {
        // 读写日期
        String group = Manifest.permission_group.CALENDAR;
        PermissionMap.put(Manifest.permission.READ_CALENDAR, group);
        PermissionMap.put(Manifest.permission.WRITE_CALENDAR, group);

        // 摄像头
        group = Manifest.permission_group.CAMERA;
        PermissionMap.put(Manifest.permission.CAMERA, group);

        // 联系人
        group = Manifest.permission_group.CONTACTS;
        PermissionMap.put(Manifest.permission.READ_CONTACTS, group);
        PermissionMap.put(Manifest.permission.WRITE_CONTACTS, group);
        PermissionMap.put(Manifest.permission.GET_ACCOUNTS, group);

        // 定位
        group = Manifest.permission_group.LOCATION;
        PermissionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, group);
        PermissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, group);

        // 麦克风
        group = Manifest.permission_group.MICROPHONE;
        PermissionMap.put(Manifest.permission.RECORD_AUDIO, group);

        // 来电
        group = Manifest.permission_group.PHONE;
        PermissionMap.put(Manifest.permission.READ_PHONE_STATE, group);
        PermissionMap.put(Manifest.permission.CALL_PHONE, group);
        PermissionMap.put(Manifest.permission.READ_CALL_LOG, group);
        PermissionMap.put(Manifest.permission.WRITE_CALL_LOG, group);
        PermissionMap.put(Manifest.permission.WRITE_VOICEMAIL, group);
        PermissionMap.put(Manifest.permission.USE_SIP, group);
        PermissionMap.put(Manifest.permission.PROCESS_OUTGOING_CALLS, group);

        // 感应设备
        group = Manifest.permission_group.SENSORS;
        PermissionMap.put(Manifest.permission.BODY_SENSORS, group);

        // 短信
        group = Manifest.permission_group.SMS;
        PermissionMap.put(Manifest.permission.SEND_SMS, group);
        PermissionMap.put(Manifest.permission.RECEIVE_SMS, group);
        PermissionMap.put(Manifest.permission.READ_SMS, group);
        PermissionMap.put(Manifest.permission.RECEIVE_WAP_PUSH, group);
        PermissionMap.put(Manifest.permission.RECEIVE_MMS, group);

        // 设备存储
        group = Manifest.permission_group.STORAGE;
        PermissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, group);
        PermissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, group);
    }

    public static String loadDescription(String permissionGroup) {
        switch (permissionGroup) {
            case Manifest.permission_group.CALENDAR:
                return "读写系统日期";
            case Manifest.permission_group.CAMERA:
                return "获取摄像头";
            case Manifest.permission_group.CONTACTS:
                return "获取手机联系人";
            case Manifest.permission_group.LOCATION:
                return "进行定位";
            case Manifest.permission_group.MICROPHONE:
                return "使用麦克风录音";
            case Manifest.permission_group.PHONE:
                return "手机来电相关操作";
            case Manifest.permission_group.SENSORS:
                return "手机感应";
            case Manifest.permission_group.SMS:
                return "短信相关操作";
            case Manifest.permission_group.STORAGE:
                return "手机存储相关操作";
        }
        return "";
    }
}
