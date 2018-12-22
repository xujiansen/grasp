package cn.com.rooten.help.permission.setting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import cn.com.rooten.help.AppHelper;
import cn.com.rooten.help.permission.AppPermissionApply;

/**
 * 华为权限设置
 */

public class HuaWeiSetting extends BaseSetting {
    // 华为手机管家
    private static final String HUAWEI_SYS_MANAGER_PCKNAME = "com.huawei.systemmanager";

    // 华为设置锁屏后运行权限的Activity
    private static final String ProtectActivity = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    // 华为设置权限主界面
    private static final String MainActivity = "com.huawei.permissionmanager.ui.RecyclerViewTestMainActivity";

    // 华为设置悬浮框权限--3.0
    private static final String SysAlertWindow_3 = "com.huawei.notificationmanager.ui.NotificationManagmentActivity";

    // 华为设置悬浮框权限--其余的
    private static final String SysAlertWindow = "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity";

    // 华为设置电池使用模式--3.0
    private static final String PowerManagerActivity_3 = "com.huawei.systemmanager.power.HwPowerManagerActivity";

    // 华为设置电池使用模式--其余的
    private static final String PowerManagerActivity = "com.huawei.systemmanager.power.ui.HwPowerManagerActivity";

    /**
     * 能否设置受保护的权限
     */
    public static boolean canSetProtectPermission(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, ProtectActivity));
        return AppHelper.isActivityIntentAvailable(context, intent);
    }

    /**
     * 跳转去设置受保护的权限
     */
    public static void gotoSetProtectPermission(Activity activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, ProtectActivity));
        startActivity(activity, intent);
    }

    /**
     * 能否设置其余的权限
     */
    public static boolean canSetOtherPermission(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, MainActivity));
        return AppHelper.isActivityIntentAvailable(context, intent);
    }

    /**
     * 跳转设置其余的权限
     */
    public static void gotoSetOtherPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppPermissionApply.gotoAppDetailSettings(activity);
        } else {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, MainActivity));
            startActivity(activity, intent);
        }
    }

    /**
     * 能否设置悬浮框的权限
     */
    public static boolean canSetSysAlertWindowPermission(Context context) {
        // 4.4之后再设置悬浮框权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return false;

        double emuiVer = AppHelper.getEmuiVersion();
        String activityName = emuiVer < 4.0 ? SysAlertWindow_3 : SysAlertWindow;

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, activityName));
        return AppHelper.isActivityIntentAvailable(context, intent);
    }

    /**
     * 跳转设置悬浮框的权限
     */
    public static void gotoSetSysAlertWindowPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppPermissionApply.requestAlertWindowPermission(activity);
        } else {
            double emuiVer = AppHelper.getEmuiVersion();
            String activityName = emuiVer < 4.0 ? SysAlertWindow_3 : SysAlertWindow;

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, activityName));
            startActivity(activity, intent);
        }
    }

    public static boolean canSetPowerModePermission(Context context) {
        // 4.4之后再设置电池使用模式
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return false;

        double emuiVer = AppHelper.getEmuiVersion();
        String activityName = emuiVer < 4.0 ? PowerManagerActivity_3 : PowerManagerActivity;

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, activityName));
        return AppHelper.isActivityIntentAvailable(context, intent);
    }

    public static void gotoSetPowerModePermission(Activity activity) {
        double emuiVer = AppHelper.getEmuiVersion();
        String activityName = emuiVer < 4.0 ? PowerManagerActivity_3 : PowerManagerActivity;

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_SYS_MANAGER_PCKNAME, activityName));
        startActivity(activity, intent);
    }
}
