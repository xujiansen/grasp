package com.rooten.help.apploop.imp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.frame.AppHandler;
import com.rooten.frame.IHandler;

import static lib.grasp.util.AppUtil.getExplicitIntent;

public class AppLoopReceiver extends BroadcastReceiver implements IHandler {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        Message msg = handler.obtainMessage();
        msg.what = intent.getIntExtra("id", -1);
        msg.obj = context;

        handler.sendMessage(msg);
    }

    private AppHandler handler = new AppHandler(this);

    @Override
    public boolean handleMessage(Message msg) {
        Context context = (Context) msg.obj;
        if (context == null) return false;

        BaApp app = (BaApp) context.getApplicationContext();
        if (app == null) return false;

        Intent mIntent = new Intent();
        mIntent.setAction(Constant.ARG_ACTION_REMOTE_SERVICE);
        mIntent.setPackage(context.getPackageName());
        Intent eintent = new Intent(getExplicitIntent(context, mIntent));
        context.startService(eintent);
        return true;
    }
}
