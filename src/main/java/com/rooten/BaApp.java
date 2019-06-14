package com.rooten;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Process;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.rooten.base.UserData;
import com.rooten.help.ActivityMgr;
import com.rooten.help.LocalBroadMgr;
import com.rooten.help.NotificationHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lib.grasp.util.PathUtil;

public class BaApp extends Application {
    private Handler             mHandler        = new Handler();                // 主线程执行  　
    private UserData            mUserData;                // 用户数据
    private Map<String, Object> mIntentParams   = new HashMap<>();
    private ActivityMgr         mActivityMgr    = null; // Activity管理辅助类
    private NotificationHelper  mNotiHelper     = null; // Notification辅助类

    private LocalBroadMgr       mLocalBroadMgr = null; // 本地广播管理

    private RequestQueue        mRequestQueue;

    public  ExecutorService     mAppThreadPool = Executors.newFixedThreadPool(3);


    @Override
    public void onCreate() {
        super.onCreate();

        init();                 // 初始化辅助类
        initAppPath();          // 初始化应用路径
        initIconify();          // 初始化 Iconify
    }

    private void init() {
        mLocalBroadMgr      = new LocalBroadMgr(this);          // 本地广播管理
        mActivityMgr        = new ActivityMgr();                // Activity管理辅助类
        mNotiHelper         = new NotificationHelper(this);
    }

    private void initAppPath() {
        PathUtil.initPath(this);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    private void initIconify() {
        Iconify.with(new FontAwesomeModule())
                .with(new MaterialModule())
                .with(new MaterialCommunityModule());
    }


    /** 获取设备名称 */
    static public String getDeviceName() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    /** 获取设备品牌 */
    static public String getDeviceType() {
        return Build.BRAND;
    }

    /** 获取操作系统 */
    static public String getOSName() {
        return "Android";
    }

    /** 获取APP版本名称 */
    public String getAppVersionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /** 获取APP版本Code */
    public int getAppVersionCode() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /** 添加主线程任务 */
    public void runOnUiThread(Runnable r) {
        if (r == null) return;
        mHandler.post(r);
    }

    /** 添加主线程(延时)任务 */
    public void runOnUiThread(Runnable r, long delay) {
        if (r == null) return;
        mHandler.postDelayed(r, delay);
    }

    /** 添加全局参数 */
    public void putArg(String name, Object value) {
        if (name == null || name.length() == 0) return;
        mIntentParams.put(name, value);
    }

    /** 获取全局参数 */
    public Object getArg(String name) {
        if (name == null || name.length() == 0) return null;
        return mIntentParams.get(name);
    }

    /** 删除全局参数 */
    public Object removeArg(String name) {
        if (name == null || name.length() == 0) return null;
        return mIntentParams.remove(name);
    }

    /** 获取当前用户 */
    public UserData getUserData() {
        return mUserData;
    }

    /** 设置当前用户 */
    public void setUserData(UserData mUserData) {
        this.mUserData = mUserData;
    }

    /** 获取本地广告代理者 */
    public LocalBroadMgr getLocalBroadMgr() {
        return mLocalBroadMgr;
    }

    /** 获取Noti代理者 */
    public NotificationHelper getNotiHelper() {
        return mNotiHelper;
    }

    /** 获取Activity管理者 */
    public ActivityMgr getActivityMgr() {
        return mActivityMgr;
    }

    /** 完全退出App，如果不把当前所未关闭的Activity关闭，在杀死进程之后，会重新启动栈顶的Activity */
    public void destroyApp(boolean isKillProcess) {
        mActivityMgr.onDestroy();       // 销毁Activity管理类
        if (isKillProcess) android.os.Process.killProcess(Process.myPid()); // 杀死本进程
    }

}


