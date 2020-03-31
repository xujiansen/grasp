package com.rooten.help;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.rooten.BaApp;
import com.rooten.util.Util;

import lib.grasp.util.AppUtil;
import lib.grasp.util.L;
import lib.grasp.util.PathUtil;
import lib.grasp.util.StreamUtil;
import lib.grasp.util.TimeDateUtil;

/**
 * 全局异常捕获
 */
public class AppHandleException implements Thread.UncaughtExceptionHandler {
    private BaApp mApp;

    /** 单例 */
    private static AppHandleException defaultInstance;

    public static AppHandleException getDefault() {
        if (defaultInstance == null) {
            synchronized (AppHandleException.class) {
                if (defaultInstance == null) {
                    defaultInstance = new AppHandleException();
                    defaultInstance.mApp = BaApp.getApp();
                }
            }
        }
        return defaultInstance;
    }

    /** 注册全局监听 */
    public static void doRegister() {
        if (!Util.isMainThread()) return;
        Thread.setDefaultUncaughtExceptionHandler(AppHandleException.getDefault());
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 清除所有的notification
        NotificationHelper.getDefault().cancelAll();
        NotificationHelper.getDefault().cancelYyspNoti();

        // 保存错误日志
        StringWriter writer = new StringWriter();
        PrintWriter print = new PrintWriter(writer);
        ex.printStackTrace(print);
        writeError(writer.toString());

//        mApp.startAppOnService();

        // 销毁本程序
        AppUtil.destroyApp(true);
    }

    private void writeError(String error) {
//        if (!Constant.APP_DEBUG) return;

        String errorParent = PathUtil.getLogErrorPath();
        String filename = TimeDateUtil.getDateTimeEx(new Date()) + ".txt";

        FileOutputStream out = null;
        try {
            File file = new File(errorParent, filename);
            out = new FileOutputStream(file, true);

            JSONObject obj = new JSONObject();
            obj.put("time", TimeDateUtil.getDateTimeMillisEs(new Date()));
            obj.put("errInfo", error);
            out.write(obj.toJSONString().getBytes("utf-8"));
        } catch (Exception e) {
            L.log("writeError::Exception" + e.toString());
        } finally {
            StreamUtil.closeOutputStream(out);
        }
    }
}
