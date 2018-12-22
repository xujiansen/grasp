package cn.com.rooten.help.apploop;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Message;

import cn.com.rooten.AppParamsMgr;
import cn.com.rooten.BaApp;
import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;

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

        boolean isQuit = AppParamsMgr.isQuit(this);
        if (isQuit) return false;

        // 具体实现类
        AppLoopImpl.onLooper(mApp, msg.what);

        // 结束job
        jobFinished(params, false);
        return true;
    }
}
