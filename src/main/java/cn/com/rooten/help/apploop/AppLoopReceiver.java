package cn.com.rooten.help.apploop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import cn.com.rooten.AppParamsMgr;
import cn.com.rooten.BaApp;
import cn.com.rooten.Constant;
import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;
import cn.com.rooten.util.Utilities;

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

        boolean isQuit = AppParamsMgr.isQuit(context);
        if (isQuit) return false;

        // 具体实现类
        AppLoopImpl.onLooper(app, msg.what);
        return true;
    }
}
