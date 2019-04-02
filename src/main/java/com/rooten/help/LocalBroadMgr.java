package com.rooten.help;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

import com.rooten.BaApp;

/**
 * 本地广播广播工具类
 */
public class LocalBroadMgr {
    public static final String PACKAGE_NAME = "PACKAGE_NAME";    // 广播携带参数

    public static final String ACTION_APP_ONLINE = "com.rooten.action.im.app_online";                     // app上线广播
    public static final String ACTION_APP_OFFLINE = "com.rooten.action.im.app_offline";                    // app离线广播
    public static final String ACTION_APP_RELOGIN_STATE = "com.rooten.action.im.reLogin_state";                  // 重连状态
    public static final String ACTION_APP_RELOGIN_FAILURE = "com.rooten.action.im.reLogin_failure";                // 重连状态
    public static final String ACTION_APP_RELOGIN_SUCCESS = "com.rooten.action.im.reLogin_success";                // 重连状态
    public static final String ACTION_UPDATE_USER_SIGN = "com.rooten.action.im.update_user_sign";               // 更新用户签名
    public static final String ACTION_UPDATE_USER_NAME = "com.rooten.action.im.update_user_name";               // 更新用户备注
    public static final String ACTION_UPDATE_USER_GROUP = "com.rooten.action.im.update_user_group";              // 更新用户分组
    public static final String ACTION_USER_PRESENCE_STATE = "com.rooten.action.im.user_presence_state";            // 用户出席状态
    public static final String ACTION_UPDATE_APP_MSGLB_LIST = "com.rooten.action.im.update_app_msglb_list";          // 更新消息列表界面
    public static final String ACTION_UPDATE_ALL_MSG_NUM = "com.rooten.action.im.update_all_msg_num";             // 更新主页面消息列表tab图标上显示的所有未读的数量
    public static final String ACTION_UPDATE_SINGLE_MSG_NUM = "com.rooten.action.im.update_single_msg_num";          // 更新消息列表单条消息的未读数量
    public static final String ACTION_UPDATE_RELOAD_ROSTER_DATA = "com.rooten.action.im.update_reload_roster_data";      // 重新查询联系人列表人员的数据
    public static final String ACTION_UPDATE_ROSTER_USER_DATA = "com.rooten.action.im.update_roster_user_data";        // 更新联系人列表人员的数据
    public static final String ACTION_UPDATE_ROSTER_GROUP_DATA = "com.rooten.action.im.update_roster_group_data";       // 更新联系人列表分组的数据
    public static final String ACTION_REFRESH_CHAT_ROOM_DATA = "com.rooten.action.im.refresh_chat_room_data";         // 重新刷新群组的数据
    public static final String ACTION_BROAD_USER_OFFLINE_MSG_TO = "com.rooten.action.im.broad_user_offline_msg_to_";     // 广播用户离线消息-发送给当前正在聊天的对象（即当前聊天界面打开时候聊天的对象）
    public static final String ACTION_BROAD_ROOM_OFFLINE_MSG_TO = "com.rooten.action.im.broad_room_offline_msg_to_";     // 广播群组离线消息-发送给当前正在聊天的对象（即当前聊天界面打开时候聊天的群组）
    public static final String ACTION_BROAD_ROOM_NOTI_MSG_TO = "com.rooten.action.im.broad_room_noti_msg_to_";        // 广播群组通知消息-发送给当前正在聊天的对象（即当前聊天界面打开时候聊天的群组）
    public static final String ACTION_BROAD_FILE_UPLOAD_PROGRESS = "com.rooten.action.im.broad_file_upload_progress";     // 广播文件上传进度
    public static final String ACTION_BROAD_FILE_DOWNLOAD_PROGRESS = "com.rooten.action.im.broad_file_download_progress";   // 广播文件下载进度
    public static final String ACTION_BROAD_MESSAGE_SEND_STATE = "com.rooten.action.im.broad_message_send_state";       // 广播消息发送状态
    public static final String ACTION_BROAD_TOPIC_IMAGE_PROGRESS = "com.rooten.action.im.broad_topic_image_progress";     // 广播话题圈缩略图图片下载进度
    public static final String ACTION_MULTI_CALL_REJECT = "com.rooten.action.im.multi_call_reject";              // 拒绝接听
    public static final String ACTION_MULTI_CALL_BUSY = "com.rooten.action.im.multi_call_busy";                // 对方正忙
    public static final String ACTION_MULTI_CALL_HANDLE = "com.rooten.action.im.multi_call_handle";              // 本账号的其他终端已经处理
    public static final String ACTION_MULTI_CALL_CLOSE = "com.rooten.action.im.multi_call_close";               // 呼叫方或者被叫方已经关闭
    public static final String ACTION_UPDATE_APP_UPGRADE = "com.rooten.action.im.update_app_upgrade";             // 通知版本更新
    public static final String ACTION_UPDATE_MAIL_ORG = "com.rooten.action.im.update_mail_org";                // 刷新通讯录机构
    public static final String ACTION_UPDATE_MAIL_USER = "com.rooten.action.im.update_mail_user";               // 刷新通讯录人员
    public static final String ACTION_UPDATE_MAIL_SORT = "com.rooten.action.im.update_mail_sort";               // 刷新通讯录排序
    public static final String ACTION_UPDATE_MAIL_POSITION = "com.rooten.action.im.update_mail_position";           // 刷新通讯录职位
    public static final String ACTION_UPDATE_MARKET_APP_PROGRESS = "com.rooten.action.im.update_market_app_progress";     // 应用市场app下载进度
    public static final String ACTION_UPDATE_PYQ_MESSAGE = "com.rooten.action.im.update_pyq_message";             // 朋友圈新的信息通知-包括新的帖子和回复
    public static final String ACTION_NEW_C2C_PUSH = "yangzhou.gaqu.c2c.push";             // 手表给手机发送新信息

    /**
     * 新建文件夹
     */
    public static final String ACTION_ADD_FOLDER = "com.rooten.action.im.ADD_FOLDER";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * 上传文件
     */
    public static final String ACTION_UPLOAD = "com.rooten.action.im.NEW_ACTION_UPLOAD";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * 上传任务
     */
    public static final String ACTION_NEW_UPLOAD_TASK = "com.rooten.action.im.NEW_UPLOAD_TASK";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * 下载任务
     */
    public static final String ACTION_NEW_DOWNLOAD_TASK = "com.rooten.action.im.NEW_DOWNLOAD_TASK";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * token过期
     */
    public static final String ACTION_SESSION_OUTDATE = "com.rooten.action.im.SESSION_OUTDATE";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * 退出
     */
    public static final String ACTION_LOGOUT = "com.rooten.action.im.LOGOUT";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * 个人容量更新
     */
    public static final String ACTION_CAPA_UPDATE = "com.rooten.action.im.CAPA_UPDATE";             // 朋友圈新的信息通知-包括新的帖子和回复
    /**
     * 按shareID查找分享文件
     */
    public static final String ACTION_SEARCH_SHARE = "com.rooten.action.im.SEARCH_SHARE";             // 朋友圈新的信息通知-包括新的帖子和回复

    private BaApp mApp;
    private String mPckName;
    private LocalBroadcastManager mLocalBroad;

    public LocalBroadMgr(BaApp app) {
        mApp = app;
        mPckName = app.getPackageName();
        mLocalBroad = LocalBroadcastManager.getInstance(app);
    }

    // 发送普通广播
    public void broadAction(String action) {
        Intent intent = new Intent(action);
        intent.setPackage(mApp.getPackageName());
        intent.putExtra(PACKAGE_NAME, mPckName);
        mLocalBroad.sendBroadcast(intent);
    }

    // 发送带参数广播
    public void broadActionWithData(String action, String data) {
        Intent intent = new Intent(action);
        intent.setPackage(mApp.getPackageName());
        intent.putExtra(PACKAGE_NAME, mPckName);
        intent.putExtra("data", data);
        mLocalBroad.sendBroadcast(intent);
    }

    // 发送带参数广播
    public void broadActionWithData(String action, Bundle data) {
        Intent intent = new Intent(action);
        intent.setPackage(mApp.getPackageName());
        intent.putExtra(PACKAGE_NAME, mPckName);
        if (data != null) intent.putExtras(data);
        mLocalBroad.sendBroadcast(intent);
    }

    // 发送带参数广播
    public void broadActionWithListData(String action, ArrayList<String> data) {
        Intent intent = new Intent(action);
        intent.setPackage(mApp.getPackageName());
        intent.putExtra(PACKAGE_NAME, mPckName);
        intent.putStringArrayListExtra("data", data);
        mLocalBroad.sendBroadcast(intent);
    }

    // 发送带参数广播
    public void broadActionWithListCharData(String action, ArrayList<CharSequence> data) {
        Intent intent = new Intent(action);
        intent.setPackage(mApp.getPackageName());
        intent.putExtra(PACKAGE_NAME, mPckName);
        intent.putCharSequenceArrayListExtra("data", data);
        mLocalBroad.sendBroadcast(intent);
    }

    // 发送带参数广播
    public void broadActionWithListCharData(String action, ArrayList<CharSequence> data, String arg1) {
        Intent intent = new Intent(action);
        intent.setPackage(mApp.getPackageName());
        intent.putExtra(PACKAGE_NAME, mPckName);
        intent.putExtra("targetPath", arg1);
        intent.putCharSequenceArrayListExtra("data", data);
        mLocalBroad.sendBroadcast(intent);
    }

    // 更新消息列表
    public void broadMsglbUpdate(String op) {
        broadMsglbUpdate(op, "", "");
    }

    // 更新消息列表
    public void broadMsglbUpdate(String op, String type, String key) {
        Bundle data = new Bundle();
        data.putString("op", op);
        data.putString("type", type);
        data.putString("key", key);
        String action = LocalBroadMgr.ACTION_UPDATE_APP_MSGLB_LIST;
        broadActionWithData(action, data);
    }

    // 更新联系人列表用户
    public void broadRosterUserUpdate(String op, String fid) {
        Bundle data = new Bundle();
        data.putString("op", op);
        data.putString("fid", fid);
        String action = LocalBroadMgr.ACTION_UPDATE_ROSTER_USER_DATA;
        broadActionWithData(action, data);
    }

    // 更新联系人列表分组
    public void broadRosterGroupUpdate(String op, int gid) {
        Bundle data = new Bundle();
        data.putString("op", op);
        data.putInt("gid", gid);
        String action = LocalBroadMgr.ACTION_UPDATE_ROSTER_GROUP_DATA;
        broadActionWithData(action, data);
    }


    /**
     * 广播文件下载进度
     * 1.上传文件的全路径
     */
    public void broadFileDownloadProgress(String saveFile, String msgId, int progress) {
        Bundle data = new Bundle();
        data.putString("msgId", msgId);
        data.putString("saveFile", saveFile);
        data.putInt("progress", progress);

        broadActionWithData(ACTION_BROAD_FILE_DOWNLOAD_PROGRESS, data);
    }

    /**
     * 广播消息发送状态
     */
    public void broadMessageSendState(String tag) {
        String action = ACTION_BROAD_MESSAGE_SEND_STATE;
        broadActionWithData(action, tag);
    }

    /**
     * 用于话题圈列表向详细页面更新进度
     */
    public void broadTopicImageProgress(String downFileName, int progress) {
        Bundle data = new Bundle();
        data.putString("downFileName", downFileName);
        data.putInt("progress", progress);

        broadActionWithData(ACTION_BROAD_TOPIC_IMAGE_PROGRESS, data);
    }

    /**
     * 用于应用市场下载app更新进度
     */
    public void broadMarketAppProgress(String reqId, long curSize, long allSize) {
        Bundle data = new Bundle();
        data.putString("reqId", reqId);
        data.putLong("curSize", curSize);
        data.putLong("allSize", allSize);

        broadActionWithData(ACTION_UPDATE_MARKET_APP_PROGRESS, data);
    }

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver == null || mLocalBroad == null) return;
        mLocalBroad.registerReceiver(receiver, filter);
    }

    public void unRegisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || mLocalBroad == null) return;
        mLocalBroad.unregisterReceiver(receiver);
    }
}
