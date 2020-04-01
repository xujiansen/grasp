package com.rooten.help;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.rooten.BaApp;
import com.rooten.AppHandler;
import com.rooten.interf.IHandler;

import lib.grasp.util.EventBusUtil;

/**
 * 广播接收分发类
 * <br/>
 * 主要是转到主线程上面
 * @deprecated 建议使用 {@link EventBusUtil}
 */
public class BroadReceiverImpl extends BroadcastReceiver implements IHandler {

    private onBroadReceiverListener mReceive;

    /**
     * 广播接收分发类
     * @deprecated 建议使用 {@link EventBusUtil}
     */
    public BroadReceiverImpl(onBroadReceiverListener l) {
        mReceive = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Context cxt = context.getApplicationContext();
        if (!(cxt instanceof BaApp)) return;

        Message msg = handle.obtainMessage();
        msg.obj = intent;
        handle.sendMessage(msg);
    }





    private AppHandler handle = new AppHandler(this);

    @Override
    public boolean handleMessage(Message msg) {
        if (!(msg.obj instanceof Intent)) return false;

        Intent intent = (Intent) msg.obj;
        String action = intent.getAction();

        if (mReceive == null) return false;
        mReceive.onReceive(action, intent);
        return true;
    }







    public interface onBroadReceiverListener {
        void onReceive(String action, Intent intent);
    }
}
