package com.rooten.help.apploop.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmUtil {
    /** 延时发广播 */
    public static void setBroadcastAlarm(Context context, Class<? extends BroadcastReceiver> cls, int requestCode, int milliSecond) {
        Intent intent = new Intent(context, cls);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        setAlarm(context, pi, milliSecond);
    }

    /** 取消发广播 */
    public static void cancelBroadcastAlarm(Context context, Class<? extends BroadcastReceiver> cls, int requestCode) {
        Intent intent = new Intent(context, cls);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    /** 延时启服务 */
    public static void setServiceAlarm(Context context, Class<? extends BroadcastReceiver> cls, int requestCode, int milliSecond) {
        Intent intent = new Intent(context, cls);

        PendingIntent sender = PendingIntent.getService(context, requestCode, intent, 0);
        setAlarm(context, sender, milliSecond);
    }

    /** 停止服务 */
    public static void cancelServiceAlarm(Context context, Class<? extends BroadcastReceiver> cls, int requestCode) {
        Intent intent = new Intent(context, cls);
        PendingIntent sender = PendingIntent.getService(context, requestCode, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    /** 设置定时任务 */
    private static void setAlarm(Context context, PendingIntent intent, int milliSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.add(Calendar.MILLISECOND, milliSecond);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        }
    }
}
