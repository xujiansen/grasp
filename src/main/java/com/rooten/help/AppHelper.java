package com.rooten.help;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.rooten.BaApp;

import lib.grasp.R;
import lib.grasp.util.StreamUtil;

import static android.content.Context.ACTIVITY_SERVICE;

public class AppHelper {
    private BaApp mApp;

    public AppHelper(BaApp app) {
        mApp = app;
    }

    synchronized public static boolean isGsmPhone(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (TelephonyManager.PHONE_TYPE_GSM == tm.getPhoneType());
    }

    public static void setStatusBarBgInKitKat(Activity activity) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBg = activity.getResources().getColor(R.color.colorPrimaryDark);
            activity.getWindow().getDecorView().setBackgroundColor(statusBg);
        }
    }

    public boolean isTopActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return isTopActivityBeforeLollipop();
        } else {

            return isTopActivityAfterLollipop();
        }
    }

    private boolean isTopActivityBeforeLollipop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return false;

        ActivityManager activityManager = (ActivityManager) mApp.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
        if (appTask.size() > 0) {
            if (mApp.getPackageName().equals(appTask.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTopActivityAfterLollipop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;

        try {
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();

            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, curDay - 1);
            long startTime = calendar.getTimeInMillis();

            UsageStatsManager usageStatsManager = (UsageStatsManager) mApp.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
            if (stats == null || stats.isEmpty()) return false;

            // SortedMap--按key由小到大的存储的map
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
            for (UsageStats usageStats : stats) {
                sortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }

            String topPackageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
            return topPackageName.equals(mApp.getPackageName());
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 判断Activity的launchMode是否是singleTask模式
     */
    public static boolean isSingleTask(Activity activity) {
        try {
            PackageManager pck = activity.getPackageManager();
            ComponentName cn = activity.getComponentName();
            ActivityInfo activityInfo = pck.getActivityInfo(cn, PackageManager.MATCH_DEFAULT_ONLY);
            return activityInfo.launchMode == ActivityInfo.LAUNCH_SINGLE_TASK;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 进程是否存在
     */
    public static boolean hasProcess(Context context, String pckName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appTask = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo task : appTask) {
                String storePckName = task.processName;
                if (storePckName.equalsIgnoreCase(pckName)) return true;
            }
        } else {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            long delay = 24 * 60 * 60 * 1000;
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - delay, time);
            if (stats == null || stats.isEmpty()) return false;

            // SortedMap--按key由小到大的存储的map
            for (UsageStats usageStats : stats) {
                String storePckName = usageStats.getPackageName();
                if (storePckName.equalsIgnoreCase(pckName)) return true;
            }
        }
        return false;
    }

    public static String getNetworkOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (TelephonyManager.PHONE_TYPE_CDMA == tm.getPhoneType()) return "46003";
        return tm.getNetworkOperator();
    }

    public static String[] getCellInfo(Context context) {
        String[] arr = new String[2];
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        CellLocation cellLocation = manager.getCellLocation();

        /**通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
        if (cellLocation instanceof GsmCellLocation) {
            GsmCellLocation gsmLocation = (GsmCellLocation) cellLocation;
            int lac = gsmLocation.getLac();
            int cid = gsmLocation.getCid();

            arr[0] = String.valueOf(lac);
            arr[1] = String.valueOf(cid);
        } else if (cellLocation instanceof CdmaCellLocation) /**通过CdmaCellLocation获取中国电信 LAC 和cellID */ {
            CdmaCellLocation cdmaLocation = (CdmaCellLocation) cellLocation;
            int lac = cdmaLocation.getNetworkId();
            int cid = cdmaLocation.getBaseStationId();

            arr[0] = String.valueOf(lac);
            arr[1] = String.valueOf(cid);
        }
        return arr;
    }

    /**
     * 获取华为操作系统EMUI的版本
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            String[] arr = version.split("\\.");
            if (arr.length == 0) return 0;

            String newVer = arr[0] + ".";
            for (int i = 1; i < arr.length; i++) {
                newVer += arr[i];
            }
            return Double.parseDouble(newVer);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return "";
        } finally {
            StreamUtil.closeBufferReader(input);
        }
        return line;
    }

    /**
     * 判断Intent是否有效, 跳转Activity
     */
    public static boolean isActivityIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
