package com.rooten.help.permission.setting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rooten.help.AppHelper;
import com.rooten.help.permission.AppPermissionApply;

/**
 * 三星权限设置
 */

public class SamSungSetting extends BaseSetting {
    // Android 系统设置
    private static final String SAMSUNG_SYS_SETTING_PCKNAME = "com.android.settings";

    // Android 7.0之前跳转到安全设置界面
    private static final String SecuritySettingsActivity = "com.android.settings.Settings$SecuritySettingsActivity";

    // 三星智能管理器(中国)-7.0之后
    private static final String SAMSUNG_SMART_MANAGER_PCKNAME = "com.samsung.android.sm_cn";

    // 三星智能管理器(中国)-7.0之后-- 跳转到自运行设置界面
    private static final String AutoRunActivity = "com.samsung.android.sm.ui.ram.AutoRunActivity";

    // 三星智能管理器(中国)-7.0之后-- 跳转到跳转到电池使用界面
    private static final String BatteryActivity = "com.samsung.android.sm.ui.battery.BatteryActivity";

    /**
     * 能否设置悬浮框权限
     */
    public static boolean canSetSysAlertWindowPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 跳转去设置悬浮框权限
     */
    public static void gotoSetSysAlertWindowPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        AppPermissionApply.requestAlertWindowPermission(activity);
    }

    /**
     * 能否设置自运行权限
     */
    public static boolean canSetAutoRunPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 跳转去设置自运行权限
     */
    public static void gotoSetAutoRunPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SAMSUNG_SMART_MANAGER_PCKNAME, AutoRunActivity));
        startActivity(activity, intent);
    }

    /**
     * 能否设置电池优化情况
     */
    public static boolean canSetBatteryPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 跳转去设置电池优化情况
     */
    public static void gotoSetBatteryPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SAMSUNG_SMART_MANAGER_PCKNAME, BatteryActivity));
        startActivity(activity, intent);
    }

    /**
     * 能否设置权限
     */
    public static boolean canSetPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return true;

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SAMSUNG_SYS_SETTING_PCKNAME, SecuritySettingsActivity));
        return AppHelper.isActivityIntentAvailable(context, intent);
    }

    /**
     * 跳转去设置权限
     */
    public static void gotoSetPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AppPermissionApply.gotoAppDetailSettings(activity);
            return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(SAMSUNG_SYS_SETTING_PCKNAME, SecuritySettingsActivity));
        startActivity(activity, intent);
    }
}
