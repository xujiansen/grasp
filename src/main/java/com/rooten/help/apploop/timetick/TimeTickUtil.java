package com.rooten.help.apploop.timetick;

import android.content.Intent;
import android.content.IntentFilter;

import com.rooten.BaApp;
import com.rooten.help.BroadReceiverImpl;

/**
 * 注册timeTick帮助类,这边是系统的接收器
 * 之所以要把TImeTick放在Application初始化,并且在接收到TimeTick广播之后还要自己发送一个ACTION_TIME_TICK的广播:
 * 1.现在测试下来当开了远程服务时候,Application是初始化多次的,所以在Application注册的话会注册多个接收器
 * 2.之所以哟在Application注册,是同时监听程序是否还在;
 * 3.因为涉及到两个进程都注册了TimeTick所以,在TimeTick接收并执行相应操作时候,不能在远程进程中执行;就广播到主进程执行。
 */
public class TimeTickUtil implements BroadReceiverImpl.onBroadReceiverListener {
    // 自己广播的TimeTick的action
    public static final String ACTION_TIME_TICK = "com.rooten.action.TIME_TICK";

    private BaApp mApp;

    // timeTick广播
    private BroadReceiverImpl mTimeTickBroad;

    public TimeTickUtil(BaApp app) {
        mApp = app;
    }

    /**
     * 注册的广播即使不杀掉
     */
    public void registerTimeTickReceiver() {
        mTimeTickBroad = new BroadReceiverImpl(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        mApp.registerReceiver(mTimeTickBroad, filter);
    }

    public void unRegisterTimeTickReceiver() {
        mApp.unregisterReceiver(mTimeTickBroad);
    }

    @Override
    public void onReceive(String action, Intent intent) {
        Intent timeTickBroad = new Intent(ACTION_TIME_TICK);
        mApp.sendBroadcast(timeTickBroad);
    }
}
