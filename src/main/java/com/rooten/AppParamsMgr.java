package com.rooten;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import lib.grasp.util.L;
import lib.grasp.util.StreamUtil;

/**
 * App参数管理
 */
public class AppParamsMgr {
    public static final String USER_FILE = "user.txt";       // 保存用户名，密码的文件名
    public static final String USER_ID_KEY = "user";           // 保存用户名，用户key
    public static final String USER_PWD_KEY = "pwd";            // 保存用户名，密码key
    public static final String USER_INFO = "info";           // 用户个人信息
    public static final String LOCAL_NET_KEY = "netType";        // 保存用户名，网络类型key
    public static final String APP_QUIT_KEY = "isQuit";         // 当前是否退出
    public static final String APP_ERROR = "appError";       // App异常

    public static final String APP_SWITCH_VOICE = "switch_voice";       // 声音提醒
    public static final String APP_SWITCH_VIBRATE = "switch_vibrate";     // 震动提醒
    public static final String APP_SWITCH_LOCATE = "switch_locate";      // 定位状态
    public static final String VIDEO_RESOLUTION = "video_resolution";   // 视频分辨率

    public static final String CALL_RING_ID = "call_ring_id";       // 选择的来电铃声的id
    public static final String MESSAGE_NOTI_ID = "message_noti_id";    // 聊天消息提醒
    public static final String SYS_MSG_NOTI_ID = "sys_msg_noti_id";    // 系统消息提醒
    public static final String CALL_RING_PATH = "call_ring_path";     // 选择的来电铃声的音频路径
    public static final String MESSAGE_NOTI_PATH = "message_noti_path";  // 聊天消息提醒的音频路径
    public static final String SYS_MSG_NOTI_PATH = "sys_msg_noti_path";  // 系统消息提醒的音频路径

    private static final String STRING_LOG = "string_log.txt";     // 保存文件名，字符串
    private static final String BOOLEAN_LOG = "boolean_log.txt";    // 保存文件名，boolean型

    /** 用户登录记录 */
    public static final String ARG_PERSON_INFO 	= "ARG_PERSON_INFO";

    public static void saveUserInfo(Context context, String uid, String pwd, String info) {
        FileOutputStream out = null;
        try {
            out = context.openFileOutput(USER_FILE, Context.MODE_MULTI_PROCESS);
            JSONObject obj = new JSONObject();
            obj.put(USER_ID_KEY, uid);
            obj.put(USER_PWD_KEY, pwd);
            obj.put(USER_INFO, info);
            String data = obj.toJSONString();
            out.write(data.getBytes("utf-8"));
        } catch (Exception e) {
            L.logOnly("saveUserInfo::Exception" + e.toString());
        } finally {
            StreamUtil.closeOutputStream(out);
        }
    }

    public static JSONObject getUserInfo(Context context) {
        FileInputStream in = null;
        try {
            in = context.openFileInput(USER_FILE);
            byte[] data = new byte[1024];
            int len = -1;
            StringBuilder buf = new StringBuilder();
            while ((len = in.read(data, 0, 1024)) != -1) {
                String s = new String(data, 0, len);
                buf.append(s);
            }
            return (JSONObject) JSONValue.parse(buf.toString());
        } catch (Exception e) {
            L.logOnly("getUserInfo::Exception" + e.toString());
        } finally {
            StreamUtil.closeInputStream(in);
        }
        return null;
    }

    public static void saveBooleanValue(Context context, String name, boolean value) {
        if (TextUtils.isEmpty(name)) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(name, value);
        edit.apply();
    }

    public static boolean getBooleanValue(Context context, String name) {
        if (TextUtils.isEmpty(name)) return false;

        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(name, false);
    }

    public static void saveStringValue(Context context, String name, String value) {
        if (TextUtils.isEmpty(name)) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences(STRING_LOG, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(name, value);
        edit.apply();
    }

    public static String getStringValue(Context context, String name) {
        if (TextUtils.isEmpty(name)) return "";

        SharedPreferences sharedPreferences = context.getSharedPreferences(STRING_LOG, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(name, "");
    }

    public static void saveQuitNote(Context context, boolean isQuit) {
        saveBooleanValue(context, APP_QUIT_KEY, isQuit);
    }

    public static boolean isQuit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        return !sharedPreferences.contains(APP_QUIT_KEY) || sharedPreferences.getBoolean(APP_QUIT_KEY, false);
    }

    public static void setAppError(Context context) {
        saveBooleanValue(context, APP_ERROR, true);
    }

    public static void clearAppError(Context context) {
        saveBooleanValue(context, APP_ERROR, false);
    }

    public static boolean isAppError(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.contains(APP_ERROR) && sharedPreferences.getBoolean(APP_ERROR, false);
    }

    // 设置默认声音提醒
    public static void setDefaultSwitchVoice(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        if (sharedPreferences.contains(APP_SWITCH_VOICE)) return;

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(APP_SWITCH_VOICE, true);
        edit.apply();
    }

    // 设置震动提醒的默认值
    public static void setDefaultSwitchVibrate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        if (sharedPreferences.contains(APP_SWITCH_VIBRATE)) return;

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(APP_SWITCH_VIBRATE, false);
        edit.apply();
    }

    // 设置分辨率的默认值
    public static void setDefaultVideoResolution(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STRING_LOG, Context.MODE_MULTI_PROCESS);
        if (sharedPreferences.contains(VIDEO_RESOLUTION)) return;

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.apply();
    }

    // 设置系统消息提示的默认值
    public static void setDefaultLocateState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BOOLEAN_LOG, Context.MODE_MULTI_PROCESS);
        if (sharedPreferences.contains(APP_SWITCH_LOCATE)) return;

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(APP_SWITCH_LOCATE, false);
        edit.apply();
    }

    public static void saveYyspTaskId(Context context, int taskId) {
        saveStringValue(context, "yysp_key", String.valueOf(taskId));
    }

}
