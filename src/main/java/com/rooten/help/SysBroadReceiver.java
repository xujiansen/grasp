package com.rooten.help;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Message;

import com.rooten.BaApp;
import com.rooten.frame.AppHandler;
import com.rooten.frame.IHandler;
import com.rooten.util.Util;

import lib.grasp.util.NetUtil;

/**
 * 系统广播
 */
public class SysBroadReceiver extends BroadcastReceiver implements IHandler {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        Bundle data = new Bundle();
        data.putString("action", intent.getAction());

        Message msg = handle.obtainMessage();
        msg.setData(data);
        msg.obj = context;
        handle.sendMessage(msg);
    }

    private AppHandler handle = new AppHandler(this);

    @Override
    public boolean handleMessage(Message msg) {
        Context context = (Context) msg.obj;
        if (context == null) return false;

        BaApp app = (BaApp) context.getApplicationContext();
        if (app == null) return false;

        Bundle data = msg.getData();
        switch (Util.getString(data, "action")) {
            case ConnectivityManager.CONNECTIVITY_ACTION: {
                // 如果当前网络状态是未连接的直接返回
                if (!NetUtil.isNetConnected(context)) break;

                // 在程序启动时候会收到该广播，但是程序是在正常启动的所以要判断
                break;
            }

            case Intent.ACTION_SCREEN_OFF: {
                System.gc(); // 当屏幕灭屏时候主动gc降低手机内存
                break;
            }

            case Intent.ACTION_BOOT_COMPLETED: {
                break;
            }
        }

        return false;
    }
}
