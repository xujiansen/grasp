package com.rooten;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import android.telephony.TelephonyManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.rooten.base.UserData;
import com.rooten.help.ActivityMgr;
import com.rooten.help.LocalBroadMgr;
import com.rooten.help.NotificationHelper;
import com.rooten.help.apploop.util.LoopHelper;
import com.rooten.help.filehttp.FileDownloadMgr;
import com.rooten.help.filehttp.FileUploadMgr;
import lib.grasp.util.PathUtil;
import lib.grasp.util.PermissionUtil;

public class BaApp extends Application {
    private Handler mHandler = new Handler();                // 主线程执行  　
    protected UserData mUserData;                // 用户数据
    private Map<String, Object> mIntentParams = new HashMap<>();
    private ActivityMgr mActivityMgr = null; // Activity管理辅助类
    private NotificationHelper mNotiHelper = null; // Notification辅助类
    private LocalBroadMgr mLocalBroadMgr = null; // 本地广播管理
    private FileUploadMgr mFileUploadMgr = null; // 文件上传管理
    private FileDownloadMgr mFileDownloadMgr = null; // 文件下载管理

    private volatile boolean mIsFirstLogin = false;  // 是否是第一次登陆

    private RequestQueue mRequestQueue;

    public ExecutorService AppThreadPool = Executors.newFixedThreadPool(3);

    /** 系统轮训帮助类 */
    public LoopHelper mLoopHelper = new LoopHelper();

    public String mLoadId;

    @Override
    public void onCreate() {
        super.onCreate();

//        LeakCanary.install(this);   // 内存泄漏监听-ps:在程序启动之后会卡顿一下

        initAppPath();          // 初始化应用路径
        initAppParams();        // 初始化带参辅助类
        initIconify();          // 初始化 Iconify
//        initIMSetDefault();     // 设置默认值，如果是第一次登陆

        // 注册TimeTick广播

        // 注册全局异常的监听
//        AppHandleException.register(this);

        // 如果本地没有保存登陆历史，则是第一次登陆
        mIsFirstLogin = AppParamsMgr.getUserInfo(this) == null;
    }

    private void initAppPath() {
        PathUtil.initPath(this);
    }

    private void initAppParams() {
        mLocalBroadMgr = new LocalBroadMgr(this);          // 本地广播管理
        mActivityMgr = new ActivityMgr();                // Activity管理辅助类
        mNotiHelper = new NotificationHelper(this);
        mFileUploadMgr = new FileUploadMgr();        // 文件上传管理器
        mFileDownloadMgr = new FileDownloadMgr();        // 文件上传管理器

        mLoadId = getFileDownloadMgr().registerCategory("下载图片", 1);
        startFileHttp();
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

    private void initIMSetDefault() {
        // 设置默认的声音提醒
        AppParamsMgr.setDefaultSwitchVoice(this);

        // 设置默认的震动提醒
        AppParamsMgr.setDefaultSwitchVibrate(this);

        // 设置默认分辨率
        AppParamsMgr.setDefaultVideoResolution(this);

        // 设置默认的定位状态
        AppParamsMgr.setDefaultLocateState(this);
    }

    public void startFileHttp() {
        mFileUploadMgr.startUpload();    // 开启文件上传
        mFileDownloadMgr.startDownload();    // 开启文件上传
    }

    public void stopFileHttp() {
        mFileUploadMgr.stopUpload();    // 停止文件上传
        mFileDownloadMgr.stopDownload();    // 停止文件上传
    }

    synchronized public String getIMEI() {
        if(!PermissionUtil.checkDangerousPermission(this, Manifest.permission.READ_PHONE_STATE)) return "";
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public String getAppVersionName() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public int getAppVersionCode() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public void runOnUiThread(Runnable r) {
        if (r == null) return;
        mHandler.post(r);
    }

    public void runOnUiThread(Runnable r, long delay) {
        if (r == null) return;
        mHandler.postDelayed(r, delay);
    }

    public void removeCallbacks(Runnable r) {
        if (r == null) return;
        mHandler.removeCallbacks(r);
    }

    public void putArg(String name, Object value) {
        if (name == null || name.length() == 0) return;
        mIntentParams.put(name, value);
    }

    public Object getArg(String name) {
        if (name == null || name.length() == 0) return null;
        return mIntentParams.get(name);
    }

    public Object removeArg(String name) {
        if (name == null || name.length() == 0) return null;
        return mIntentParams.remove(name);
    }


    static public String getDeviceName() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    static public String getDeviceType() {
        return Build.BRAND;
    }

    static public String getOSName() {
        return "Android";
    }

    public UserData getUserData() {
        return mUserData;
    }

    public void setUserData(UserData mUserData) {
        this.mUserData = mUserData;
    }

    public LocalBroadMgr getLocalBroadMgr() {
        return mLocalBroadMgr;
    }

    public NotificationHelper getNotiHelper() {
        return mNotiHelper;
    }

    public ActivityMgr getActivityMgr() {
        return mActivityMgr;
    }

    public FileUploadMgr getFileUploadMgr() {
        return mFileUploadMgr;
    }

    public FileDownloadMgr getFileDownloadMgr() {
        return mFileDownloadMgr;
    }

    public LoopHelper getLoopHelper() {
        return mLoopHelper;
    }

    public void setLoopHelper(LoopHelper mLoopHelper) {
        this.mLoopHelper = mLoopHelper;
    }


    /**
     * 重新启动App
     */
    public static void reLaunchApp(Context context) {
    }

    /**
     * 完全退出App，如果不把当前所未关闭的Activity关闭，在杀死进程之后，会重新启动栈顶的Activity
     */
    public void destroyApp(boolean isKillProcess) {
        // 销毁Activity管理类
        mActivityMgr.onDestroy();

        // 杀死本进程
        if (isKillProcess) android.os.Process.killProcess(Process.myPid());
    }
}


