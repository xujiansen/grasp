package lib.grasp.exception;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lib.grasp.util.FileUtil;
import lib.grasp.util.PathUtil;

/**
 * Created by JS_grasp on 2018/12/23.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mCtx;
    private static CrashHandler mInstance;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> mInfo = new HashMap<>(); // 日志相关字段
    private DateFormat mDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private CrashHandler(){}

    /** 注册全局异常监听 */
    public static void init(Context ctx){
        mInstance = getInstance();
        mInstance.mCtx = ctx;
        mInstance.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mInstance.mDefaultHandler);
    }

    private static CrashHandler getInstance() {
        if(mInstance == null){
            synchronized (CrashHandler.class){
                if(mInstance == null) mInstance = new CrashHandler();
            }
        }
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // 1. 收集
        // 2. 保存
        // 2. 上传

        if(isHandleException(e)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }
        if(mDefaultHandler == null) return;


    }

    /** 是否已经自己手动处理 */
    private boolean isHandleException(Throwable e){
        if(e == null) return false;
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mCtx, "UnCaughtException", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();

        collectErrInfo();
        saveErrInfo(e);
        return false;
    }

    private void collectErrInfo(){
        PackageManager pm = mCtx.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(mCtx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if(pi == null) return;

            String versionName = TextUtils.isEmpty(pi.versionName) ? "未知版本" : pi.versionName;
            String versionCode = Long.toString(pi.getLongVersionCode());
            mInfo.put("VersionName", versionName);
            mInfo.put("VersionCode", versionCode);

            Field[] fields = Build.class.getFields();
            if(fields == null || fields.length <= 0) return;
            for(Field field : fields){
                field.setAccessible(true);
                mInfo.put(field.getName(), field.get(null).toString());
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void saveErrInfo(Throwable e){
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, String> entry : mInfo.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable cause = e.getCause();
        while (cause != null){
            cause.printStackTrace(printWriter);
            cause = e.getCause();
        }

        printWriter.close();

        String result = writer.toString();
        sb.append(result);

        String timeNowFormatted = mDf.format(new Date());

        String errorParent = PathUtil.getErrorPath();
        String errorLogPath = errorParent + timeNowFormatted + ".log";
        FileUtil.appendStrToFile(mCtx, errorLogPath, sb.toString());
    }
}
