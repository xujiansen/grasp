package com.rooten.help;

import net.minidev.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import com.rooten.AppParamsMgr;
import com.rooten.BaApp;
import com.rooten.util.Util;
import com.rooten.util.Utilities;
import lib.grasp.util.L;
import lib.grasp.util.PathUtil;

/**
 * 全局异常捕获
 */
public class AppHandleException implements Thread.UncaughtExceptionHandler {
    private BaApp mApp;

    private static AppHandleException mInstance;

    private AppHandleException() {
    }

    public static void register(BaApp app) {
        if (!Util.isMainThread() || mInstance != null) return;

        mInstance = new AppHandleException();
        mInstance.mApp = app;
        Thread.setDefaultUncaughtExceptionHandler(mInstance);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 清除所有的notification
        mApp.getNotiHelper().cancelAll();
        mApp.getNotiHelper().cancelYyspNoti();

        // 保存错误日志
        StringWriter writer = new StringWriter();
        PrintWriter print = new PrintWriter(writer);
        ex.printStackTrace(print);
        writeError(writer.toString());

        // 置异常标记，在另外的进程中进行重连，本进程已经异常不能进行更多操作
        AppParamsMgr.setAppError(mApp);
//        mApp.startAppOnService();

        // 销毁本程序
        mApp.destroyApp(true);
    }

    private void writeError(String error) {
//        if (!Constant.APP_DEBUG) return;

        String errorParent = PathUtil.PATH_LOG_ERROR;
        String filename = Utilities.getDateTimeEx(new Date()) + ".txt";

        FileOutputStream out = null;
        try {
            File file = new File(errorParent, filename);
            out = new FileOutputStream(file, true);

            JSONObject obj = new JSONObject();
            obj.put("time", Utilities.getDateTimeMillisEs(new Date()));
            obj.put("errInfo", error);
            out.write(obj.toJSONString().getBytes("utf-8"));
        } catch (Exception e) {
            L.logOnly(AppHandleException.class, "writeError::Exception", e.toString());
        } finally {
            Utilities.closeOutputStream(out);
        }
    }
}
