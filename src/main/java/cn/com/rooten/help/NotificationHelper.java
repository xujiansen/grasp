package cn.com.rooten.help;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import cn.com.rooten.BaApp;
import cn.com.rooten.util.Utilities;
import lib.grasp.R;

public class NotificationHelper {
    private final int NOTIFY_FIRSRT = 0;
    private final int NOTIFY_APP = NOTIFY_FIRSRT + 1;
    private final int NOTIFY_GPS = NOTIFY_FIRSRT + 2;
    private final int NOTIFY_XTXX = NOTIFY_FIRSRT + 3;
    private final int NOTIFY_IMXX = NOTIFY_FIRSRT + 4;
    private final int NOTIFY_YYSP = NOTIFY_FIRSRT + 5;

    private BaApp mApp;
    private NotificationManager mNotiManager;

    public NotificationHelper(BaApp app) {
        mApp = app;
        mNotiManager = (NotificationManager) mApp.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void cancelAll() {
        mNotiManager.cancel(NOTIFY_APP);
        mNotiManager.cancel(NOTIFY_IMXX);
    }

    public void cancelYyspNoti() {
        mNotiManager.cancel(NOTIFY_YYSP);
    }

    public void addAppNotification() {
        String title = "您还没有登录";
//        if (!Utilities.isEmpty(mApp.getUserData().name)) {
//            title = mApp.getUserData().name + " 你好";
//        }

        String clsName = mApp.getPackageManager().getLaunchIntentForPackage(mApp.getPackageName())
                .getComponent()
                .getClassName();

        Intent contentIntent = new Intent();
        contentIntent.setComponent(new ComponentName(mApp, clsName));
        contentIntent.setAction(Intent.ACTION_MAIN);
        contentIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent intent = PendingIntent.getActivity(mApp, 0, contentIntent, 0);
        newNotification(title, intent, NOTIFY_APP);
    }

    public void addReLaunchNotification() {
//        Intent contentIntent = new Intent();
//        contentIntent.setComponent(new ComponentName(mApp, AppHome.class));
//        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent intent = PendingIntent.getActivity(mApp, 0, contentIntent, 0);
//
//        String title = mApp.getUserData().getName() + " 你好";
//        newNotification(title, intent, NOTIFY_APP);
    }

    private void newNotification(String title, PendingIntent intent, int notiId) {
        CharSequence appName = mApp.getResources().getText(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mApp);
        builder.setContentTitle(title)
                .setContentText(appName + "正在后台运行").setTicker(null)
                .setContentIntent(intent).setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT).setOngoing(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_launcher);
        } else {
            builder.setSmallIcon(R.drawable.ic_launcher_tran);
        }

        mNotiManager.notify(notiId, builder.build());
    }

    public NotificationManager getNotificationMgr() {
        return mNotiManager;
    }
}
