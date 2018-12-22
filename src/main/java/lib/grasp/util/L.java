package lib.grasp.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.com.rooten.util.Utilities;

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
    public static void logOnly(String tag, Object msg) {
        if (!IS_SHOW_LOG_AND_PRINT) return;
        showLogCompletion(tag, msg.toString(), MAX_LENGTH);
    }

    /**
     * 打印
     */
    public static void logOnly(Class clazz, String tag, Object msg) {
        if (!IS_SHOW_LOG_AND_PRINT) return;
        show(clazz.getName(), tag + "," + msg.toString());
    }

    /**
     * 打印
     */
    public static void logOnly(Object clazz, String tag, Object msg) {
        if (!IS_SHOW_LOG_AND_PRINT) return;
        show(clazz.getClass().getName(), tag + "," + msg.toString());
    }


    /**
     * 打印, 写入文件
     */
    public static void logAndWrite(String tag, Object msg) {
        logOnly(tag, msg);
        if (IS_WRITE_TO_FILE) writeLogToFile("", tag, msg.toString());
    }

    /**
     * 打印, 写入文件
     */
    public static void logAndWrite(Class clazz, String tag, Object msg) {
        logOnly(clazz, tag, msg);
        if (IS_WRITE_TO_FILE) writeLogToFile("", clazz.getName(), tag + ":" + msg.toString());
    }

    /**
     * 打印
     */
    public static void logAndWrite(Object object, String tag, Object msg) {
        logOnly(object, tag, msg);
        if (IS_WRITE_TO_FILE) writeLogToFile("", object.toString(), tag + ":" + msg.toString());
    }

    /**
     * 删除指定日期前的日志文件
     */
    public static void doDelFile() {
        String needDelFile = mLogFile.format(getDateBefore());
        File file = new File(LOG_PATH_SDCARD_DIR, needDelFile + LOGFILEName);
        if (file.exists()) file.delete();
    }

    /**
     * 打开日志文件并写入日志
     **/
    private static void writeLogToFile(String mLogType, String tag, String text) {
        Date nowTime = new Date();
        String needWriteFile = mLogFile.format(nowTime);
        String needWriteMessage = mLogSdf.format(nowTime) + " " + mLogType + " " + tag + " " + text;
        File file = new File(LOG_PATH_SDCARD_DIR, needWriteFile + LOGFILEName);
        Utilities.ensurePathExists(file.getParent());
        if (!Utilities.fileExists(file.getAbsolutePath())) {
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
    public static void showLogCompletion(String tag, String log, int showCount) {
        if (log.length() > showCount) return;

        if (log.length() > showCount) {
            String show = log.substring(0, showCount);

            show(tag, show);

            if ((log.length() - showCount) > showCount) {//剩下的文本还是大于规定长度
                String partLog = log.substring(showCount, log.length());
                showLogCompletion(tag, partLog, showCount);
            } else {
                String surplusLog = log.substring(showCount, log.length());
                show(tag, surplusLog);
            }
        } else {
            show(tag, log);
        }

//        for(int i = 0; i < log.length(); i=+showCount){
//            int end = i+showCount;
//            if(end > log.length()) end = log.length();
//            String part = log.substring(i, end);
//            show(tag, part);
//        }
    }

    private static void show(String tag, String log) {
        System.out.println(tag + ":" + log);
//        Log.i(tag, log);
    }
}