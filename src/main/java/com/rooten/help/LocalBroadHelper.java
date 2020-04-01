package com.rooten.help;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import com.rooten.BaApp;

import lib.grasp.util.EventBusUtil;

/**
 * 本地广播广播工具类
 * @deprecated 建议使用 {@link EventBusUtil}
 */
public class LocalBroadHelper {
    public static final String PACKAGE_NAME = "PACKAGE_NAME";

    /** 广播文件下载进度 */
    public static final String ACTION_BROAD_FILE_DOWNLOAD_PROGRESS  = "com.rooten.action.im.broad_file_download_progress";

    protected BaApp mApp;
    private String mPckName;
    private LocalBroadcastManager mLocalBroad;

    /** 单例 */
    private static volatile LocalBroadHelper defaultInstance;

    /**
     * @deprecated 建议使用 {@link EventBusUtil}
     */
    @Deprecated
    public static LocalBroadHelper getDefault() {
        if (defaultInstance == null) {
            synchronized (LocalBroadHelper.class) {
                if (defaultInstance == null) {
                    defaultInstance = new LocalBroadHelper();
                }
            }
        }
        return defaultInstance;
    }

    private LocalBroadHelper() {
        mApp = BaApp.getApp();
        mPckName = mApp.getPackageName();
        mLocalBroad = LocalBroadcastManager.getInstance(mApp);
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

    /** 注册(应用内广播监听) */
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver == null || mLocalBroad == null) return;
        mLocalBroad.registerReceiver(receiver, filter);
    }

    /** 解注册(应用内广播监听) */
    public void unRegisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || mLocalBroad == null) return;
        mLocalBroad.unregisterReceiver(receiver);
    }
}
