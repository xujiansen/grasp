package cn.com.rooten.help.apploop;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.rooten.AppParamsMgr;
import cn.com.rooten.BaApp;
import cn.com.rooten.util.PowerUtil;
import lib.grasp.util.L;

/**
 * App是否存在服务
 */
public class AppOnService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "AppOnService--onStartCommand");

        TaskUtil.startAppPoll(this); // 设置闹钟

        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "AppOnService--isAppError：" + AppParamsMgr.isAppError(this));

        if (AppParamsMgr.isAppError(this)) {
            startReLaunchTimer(this);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        boolean isQuit = AppParamsMgr.isQuit(this);

        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "AppOnService--onDestroy" + isQuit);

        if (!isQuit) {
            Intent intent = new Intent(this, AppOnService.class);
            startService(intent);
        } else {
            TaskUtil.stopAppPoll(this);

            // 如果是正常退出服务,手动杀掉本进程,防止仍然接收TimeTick广播
            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /***********************************************App异常重连计时器************************************************/
    /**
     * 1.为什么不直接进行重连(或者为什么要通过广播去重连)？
     * 原因：该进程是服务进程，如果在该进程内直接进行重连的操作，那么Socket资源就直接在该进程中；但是我们的程序肯定要在另外一个进程中
     * 冲突；而且如果在该进程中直接重连，无法进入主页面；也就是主程序进程和该服务进程不能同一个。
     * 2.为什么要开计时器延时进行重连？
     * 原因：App异常之后会回调AppHandleException类中的uncaughtException()之后在执行到mApp.destroyApp();这一步时候原先的App进程
     * 才会被真正杀掉；但是在这个步骤之前会首先进行重连，而如果直接在异常的App进程被杀死前就进行重连，异常的App进程会马上被杀死，
     * 导致无法重连。
     */

    private PowerUtil mPowerUtil;
    private Timer mReLaunchTimer;
    private TimerTask mReLaunchTask;

    private void startReLaunchTimer(final Context context) {
        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "AppOnService--startReLaunchTimer");

        // 先初始化cpu管理类
        if (mPowerUtil == null) {
            mPowerUtil = new PowerUtil(context);
        }

        // 计时器已经开始工作
        if (mReLaunchTimer != null) return;

        L.logAndWrite(AppLoopImpl.class.getSimpleName(), "AppOnService--startReLaunchTimer--22");

        // 启动延时时间，保持cpu超时时间
        final int RE_LAUNCH_TIME = 5000;
        final int ACQUIRE_CPU_TIME_OUT = 5500; // 尽量比上面的值大一点，防止已经超时了，timer还没有执行
        mPowerUtil.acquireCpu(ACQUIRE_CPU_TIME_OUT);

        mReLaunchTask = new TimerTask() {
            @Override
            public void run() {
                L.logAndWrite(AppLoopImpl.class.getSimpleName(), "AppOnService--run");

                // 停止计时器，启动App
                stopReLaunchTimer();
                BaApp.reLaunchApp(context);
            }
        };

        // 开启计时器
        mReLaunchTimer = new Timer();
        mReLaunchTimer.schedule(mReLaunchTask, RE_LAUNCH_TIME);
    }

    private void stopReLaunchTimer() {
        if (mReLaunchTask != null) {
            mReLaunchTask.cancel();
            mReLaunchTask = null;
        }

        if (mReLaunchTimer != null) {
            mReLaunchTimer.cancel();
            mReLaunchTimer = null;
        }

        // 释放cpu
        if (mPowerUtil == null) return;
        mPowerUtil.releaseCpu();
        mPowerUtil = null;
    }
}
