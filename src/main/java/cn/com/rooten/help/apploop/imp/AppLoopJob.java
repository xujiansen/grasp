package cn.com.rooten.help.apploop.imp;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;

import cn.com.rooten.BaApp;
import cn.com.rooten.Constant;
import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;
import cn.com.rooten.util.Util;

import static lib.grasp.util.ApkUtil.getExplicitIntent;

@SuppressWarnings("NewApi")
public class AppLoopJob extends JobService implements IHandler {
    private BaApp mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (BaApp) getApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Message msg = handler.obtainMessage();
        msg.what = params.getJobId();
        msg.obj = params;

        handler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private AppHandler handler = new AppHandler(this);

    @Override
    public boolean handleMessage(Message msg) {
        JobParameters params = (JobParameters) msg.obj;
        if (params == null) return false;
        try{
            jobFinished(params, false);                         // 结束job
        }
        catch (Exception e){}

        Intent mIntent = new Intent();
        mIntent.setAction(Constant.ARG_ACTION_REMOTE_SERVICE);
        mIntent.setPackage(getPackageName());
        Intent eintent = new Intent(getExplicitIntent(this, mIntent));
        startService(eintent);
        return true;
    }
}
