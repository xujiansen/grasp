package cn.com.rooten.help.apploop;

import android.content.Context;
import android.os.Build;

import cn.com.rooten.Constant;

public class TaskUtil {
    public static void startHeartbeat(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.setBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_HEARTBEAT, Constant.TIME_HEARTBEAT);
        } else {
            int time_heartbeat = Constant.TIME_HEARTBEAT * 60 * 1000;
            JobUtil.scheduleLatencyJob(context, AppLoopJob.class, Constant.ID_HEARTBEAT, time_heartbeat);
        }
    }

    public static void stopHeartbeat(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.cancelBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_HEARTBEAT);
        } else {
            JobUtil.cancelJob(context, Constant.ID_HEARTBEAT);
        }
    }

    public static void startAppUpgrade(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.setBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_APPUPGRADE, Constant.TIME_APPUPGRADE);
        } else {
            int time_appUpgrade = Constant.TIME_APPUPGRADE * 60 * 1000;
            JobUtil.scheduleLatencyJob(context, AppLoopJob.class, Constant.ID_APPUPGRADE, time_appUpgrade);
        }
    }

    public static void stopAppUpgrade(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.cancelBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_APPUPGRADE);
        } else {
            JobUtil.cancelJob(context, Constant.ID_APPUPGRADE);
        }
    }

    public static void startAppReLogin(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.setBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_RELOGIN, Constant.TIME_RELOGIN);
        } else {
            int time_reLogin = Constant.TIME_RELOGIN * 60 * 1000;
            JobUtil.scheduleLatencyJob(context, AppLoopJob.class, Constant.ID_RELOGIN, time_reLogin);
        }
    }

    public static void stopAppReLogin(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.cancelBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_RELOGIN);
        } else {
            JobUtil.cancelJob(context, Constant.ID_RELOGIN);
        }
    }

    public static void startAppPoll(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.setBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_POLL, Constant.TIME_POLL);
        } else {
            int time_poll = Constant.TIME_POLL * 60 * 1000;
            JobUtil.scheduleLatencyJob(context, AppLoopJob.class, Constant.ID_POLL, time_poll);
        }
    }

    public static void stopAppPoll(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.cancelBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_POLL);
        } else {
            JobUtil.cancelJob(context, Constant.ID_POLL);
        }
    }

    public static void stopAllTask(Context context) {
        TaskUtil.stopHeartbeat(context);
        TaskUtil.stopAppReLogin(context);
        TaskUtil.stopAppUpgrade(context);
        TaskUtil.stopAppPoll(context);
        JobUtil.cancelAllJob(context);
    }
}
