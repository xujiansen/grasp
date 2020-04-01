package com.rooten.help.apploop.timetick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.AppHandler;
import com.rooten.interf.IHandler;
import com.rooten.help.apploop.AppLoopImpl;

/**
 * TimeTick广播自己的接收器
 */
public class TimeTickReceiver extends BroadcastReceiver implements IHandler {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!action.equals(TimeTickUtil.ACTION_TIME_TICK)) return;

        Message msg = handle.obtainMessage();
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

        // App轮询
        AppLoopImpl.onLooper(app, Constant.ID_POLL);

        // 心跳
//        if (!app.isOnline()) return false;
        AppLoopImpl.onLooper(app, Constant.ID_HEARTBEAT);

        return false;
    }
}
