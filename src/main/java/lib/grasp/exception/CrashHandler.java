package lib.grasp.exception;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import com.rooten.BaApp;
import com.rooten.util.Util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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

    /** 单例 */
    private static CrashHandler mInstance;

    /** 注册全局异常监听 */
    public static void init(){
        if (!Util.isMainThread()) return;
        Thread.setDefaultUncaughtExceptionHandler(getInstance());
    }

    private static CrashHandler getInstance() {
        if(mInstance == null){
            synchronized (CrashHandler.class){
                if(mInstance == null) mInstance = new CrashHandler();
            }
        }
        return mInstance;
    }

    private Map<String, String> mInfo = new HashMap<>(); // 日志相关字段
    private DateFormat mDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if(isHandleException(e)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    /** 是否已经自己手动处理 */
    private boolean isHandleException(Throwable e){
        if(e == null) return false;
//        if(e == null) return true;
        collectErrInfo();
        saveErrInfo(e);
        return true;
    }

    private void collectErrInfo(){
        PackageManager pm = BaApp.getApp().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(BaApp.getApp().getPackageName(), PackageManager.GET_ACTIVITIES);
            if(pi == null) return;

            String versionName = TextUtils.isEmpty(pi.versionName) ? "未知版本" : pi.versionName;
            String versionCode = Long.toString(pi.getLongVersionCode());
            mInfo.put("版本名", versionName);
            mInfo.put("版本号", versionCode);
            mInfo.put("品牌", Build.BRAND);
            mInfo.put("型号", Build.MODEL);
            mInfo.put("CPU版本", Build.CPU_ABI);

//            Field[] fields = Build.class.getFields();
//            if(fields == null || fields.length <= 0) return;
//            for(Field field : fields){
//                field.setAccessible(true);
//                mInfo.put(field.getName(), field.get(null).toString());
//            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
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
        String errorParent = PathUtil.getLogErrorPath();
        String errorLogPath = errorParent + timeNowFormatted + ".log";
        FileUtil.appendStrToFile(BaApp.getApp(), errorLogPath, sb.toString());
    }
}
