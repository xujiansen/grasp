package com.rooten.help;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import com.rooten.BaApp;

/**
 * 本地广播广播工具类
 */
public class LocalBroadMgr {
    public static final String PACKAGE_NAME = "PACKAGE_NAME";

    public static final String ACTION_BROAD_FILE_DOWNLOAD_PROGRESS  = "com.rooten.action.im.broad_file_download_progress";   // 广播文件下载进度

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

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver == null || mLocalBroad == null) return;
        mLocalBroad.registerReceiver(receiver, filter);
    }

    public void unRegisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || mLocalBroad == null) return;
        mLocalBroad.unregisterReceiver(receiver);
    }
}
