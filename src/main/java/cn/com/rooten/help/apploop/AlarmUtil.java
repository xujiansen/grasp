package cn.com.rooten.help.apploop;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class AlarmUtil {
    public static void setBroadcastAlarm(Context context, Class<? extends BroadcastReceiver> cls, int id, int minute) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("id", id);

        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);
        setAlarm(context, sender, minute);
    }

    public static void cancelBroadcastAlarm(Context context, Class<? extends BroadcastReceiver> cls, int id) {
        Intent intent = new Intent(context, cls);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    public static void setServiceAlarm(Context context, Class<? extends BroadcastReceiver> cls, int id, int minute) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("id", id);

        PendingIntent sender = PendingIntent.getService(context, id, intent, 0);
        setAlarm(context, sender, minute);
    }

    public static void cancelServiceAlarm(Context context, Class<? extends BroadcastReceiver> cls, int id) {
        Intent intent = new Intent(context, cls);
        PendingIntent sender = PendingIntent.getService(context, id, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    private static void setAlarm(Context context, PendingIntent intent, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, minute);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intent);
        }
    }
}
