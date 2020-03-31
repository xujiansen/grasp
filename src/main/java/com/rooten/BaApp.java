package com.rooten;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.rooten.base.UserData;
import com.rooten.help.ActivityMgr;
import com.rooten.help.LocalBroadMgr;
import com.rooten.help.NotificationHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lib.grasp.util.L;
import lib.grasp.util.PathUtil;
import lib.grasp.util.SPUtil;

public class BaApp extends Application {

    /**
     * 主线程执行
     */
    private Handler mHandler = new Handler();

    /**
     * 临时数据
     */
    private Map<String, Object> mTempParams   = new HashMap<>();

    /**
     * 用户数据
     */
    protected UserData mUserData = new UserData();

    /**
     * 应用级上下文
     */
    protected static BaApp APP;

    /**
     * 应用级上下文
     * <br/>
     * 这个方法强烈建议子类重写
     */
    public static BaApp getApp() {
        return APP;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaApp.APP = this;
        initBiz();
    }

    protected void initBiz(){
        initAppPath();
        initIconify();
        initLogUtil();
    }

    /** 初始化应用路径 */
    private void initAppPath() {
        PathUtil.initPath();
    }

    /** 初始化 Iconify */
    private void initIconify() {
        Iconify.with(new FontAwesomeModule())
                .with(new MaterialModule())
                .with(new MaterialCommunityModule());
    }

    /** 初始化日志工具类 */
    private void initLogUtil() {
        L.init();
    }

    /** 添加任务(立刻执行) */
    public void runOnUiThread(Runnable r) {
        if (r == null) return;
        mHandler.post(r);
    }

    /** 添加任务(延时执行) */
    public void runOnUiThread(Runnable r, long delay) {
        if (r == null) return;
        mHandler.postDelayed(r, delay);
    }

    /** 添加全局参数 */
    public void putArg(String name, Object value) {
        if (name == null || name.length() == 0) return;
        mTempParams.put(name, value);
    }

    /** 获取全局参数 */
    public Object getArg(String name) {
        if (name == null || name.length() == 0) return null;
        return mTempParams.get(name);
    }

    /** 删除全局参数并返回 */
    public Object removeArg(String name) {
        if (name == null || name.length() == 0) return null;
        return mTempParams.remove(name);
    }

    /** 获取当前用户 */
    public UserData getUserData() {
        return mUserData;
    }

    /** 设置当前用户 */
    public void setUserData(UserData mUserData) {
        this.mUserData = mUserData;
    }
}


