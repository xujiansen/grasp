package com.rooten.help.apploop.timetick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.rooten.AppParamsMgr;
import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.frame.AppHandler;
import com.rooten.frame.IHandler;
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

        // 如果当前程序已经退出也直接返回
        if (AppParamsMgr.isQuit(context)) return false;

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
