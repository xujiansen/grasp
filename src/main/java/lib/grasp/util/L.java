package lib.grasp.util;

import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 带日志文件输入的，又可控开关的日志调试
 */
public class L {
    private static final String LOG_FILE_PATH = Environment.getExternalStorageDirectory() + "/LOG/";

    private static boolean IS_SHOW_LOG_AND_PRINT = true;                 // 是否控制台打印
    private static boolean IS_WRITE_TO_FILE = true;                 // 日志写入文件开关
    private static String LOG_PATH_SDCARD_DIR = LOG_FILE_PATH;        // 日志文件在sdcard中的路径
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 30;                   // sd卡中日志文件的最多保存天数
    private static String LOGFILEName = ".txt";               // 本类输出的日志文件名称
    private static char LOG_TYPE = 'v';                  // 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
    private static int MAX_LENGTH = 3900;                 // log每行最多字数

    private static SimpleDateFormat mLogFile = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());           // 日志文件名称前缀
    private static SimpleDateFormat mLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());  // 日志内容的时间格式

    /**
     * 打印
     */
    public static void log(Object msg) {
        Logger.d(msg);
    }


    /**
     * 打印
     */
    @Deprecated
    public static void log(Class classs, String title, Object msg) {
        Logger.d(title + ": " +  msg);
    }

    /**
     * 打印
     */
    @Deprecated
    public static void logOnly(Object msg) {
        if (!IS_SHOW_LOG_AND_PRINT) return;
        showLogCompletion(msg.toString(), MAX_LENGTH);
    }

    /**
     * 打印
     */
    @Deprecated
    public static void logAndWrite(Object msg) {
        logOnly(msg);
        if (IS_WRITE_TO_FILE) writeLogToFile(defaultTag(), msg.toString());
    }

    /**
     * 删除指定日期前的日志文件
     */
    @Deprecated
    public static void doDelFile() {
        String needDelFile = mLogFile.format(getDateBefore());
        File file = new File(LOG_PATH_SDCARD_DIR, needDelFile + LOGFILEName);
        if (file.exists()) file.delete();
    }

    /**
     * 打开日志文件并写入日志
     **/
    private static void writeLogToFile(String tag, String text) {
        Date nowTime = new Date();
        String needWriteFile = mLogFile.format(nowTime);
        String needWriteMessage = mLogSdf.format(nowTime) + " " + tag + " " + text;
        File file = new File(LOG_PATH_SDCARD_DIR, needWriteFile + LOGFILEName);
        FileUtil.ensurePathExists(file.getParent());
        if (!FileUtil.fileExists(file.getAbsolutePath())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }

    /**
     * 分段打印出较长log文本
     *
     * @param log       原log文本
     * @param showCount 规定每段显示的长度（最好不要超过eclipse限制长度）
     */
    @Deprecated
    public static void showLogCompletion(String log, int showCount) {
        if(log.length() < showCount){
            show(log);
            return;
        }

        String show = log.substring(0, showCount);
        show(show);

        if ((log.length() - showCount) > showCount) {                   //剩下的文本还是大于规定长度
            String partLog = log.substring(showCount);
            showLogCompletion(partLog, showCount);
        } else {
            String surplusLog = log.substring(showCount);
            show(surplusLog);
        }
    }

    private static void show(String log) {
        System.out.println("单Log:" + log);
//        Log.i(tag, log);
    }

    private static String defaultTag() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.out.println("　Log:");
        for(int i = stackTrace.length - 1; i >= 4; i--){    // log栈
            StackTraceElement e = stackTrace[i];
            System.out.println("　Log:【" + e.getClassName() + "】 - 【" + e.getMethodName() + "】 - 【" + e.getLineNumber() + "行】");
        }
        StackTraceElement log = stackTrace[1];
        String tag = null;
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement e = stackTrace[i];
            if (!e.getClassName().equals(log.getClassName())) {
                tag = e.getClassName() + "." + e.getMethodName();
                break;
            }
        }
        if (tag == null) {
            tag = log.getClassName() + "." + log.getMethodName();
        }
        return tag;
    }
}