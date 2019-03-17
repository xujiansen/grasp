package lib.grasp.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

/**
 * 权限工具类
 * <p>
 * 权限组名	权限名称
 * <p>
 * -CALENDAR（日历）-
 * READ_CALENDAR
 * WRITE_CALENDAR
 * <p>
 * -CAMERA（相机）-
 * CAMERA
 * <p>
 * -CONTACTS（联系人）-
 * READ_CONTACTS
 * WRITE_CONTACTS
 * GET_ACCOUNTS
 * <p>
 * -LOCATION（位置）-
 * ACCESS_FINE_LOCATION
 * ACCESS_COARSE_LOCATION
 * <p>
 * -MICROPHONE（麦克风）-
 * RECORD_AUDIO
 * <p>
 * -PHONE（手机）-
 * READ_PHONE_STATE
 * CALL_PHONE
 * ERAD_CALL_LOG
 * WRITE_CALL_LOG
 * ADD_VOICEMAIL
 * USE_SIP
 * PROCESS_OUTGOING_CALLS
 * <p>
 * -SENSORS（传感器）-
 * BODY_SENSORS
 * <p>
 * -SMS（短信）-
 * SEND_SMS
 * RECEIVE_SMS
 * READ_SMS
 * RECEIVE_WAP_PUSH
 * RECEIVE_MMS
 * <p>
 * -STORAGE（存储卡）-
 * READ_EXTERNAL_STORAGE
 * WRITE_EXTERNAL_STORAGE
 * <p>
 * 这张表可以作为一个参照表，
 * 每当使用一个权限的时，可以先到这张表中来查看一下，
 * 如果在属于这张表中的权限，那么就需要进行运行时权限处理，
 * 如果不在这张表中，那么只需要在AndroidManifest.xml文件中添加一下权限声明就可以了。
 * <p>
 * 另外需要注意，
 * 表格中每个危险权限都属于一个权限组，我们在进行运行时权限处理时使用的是权限名，
 * 但是用户一旦同意授权了，那么该权限所对应的权限组中所有其他的权限也会同时被授权。
 */
public class PermissionUtil {
    /*
     *  1. 摄像头
     *  2. 录音
     *  3. 定位
     *  4. 读写外存
     *
     *  5. 网络
     *  6. 联系人
     *  7. 收发读取信息
     *  8. 传感器
     */

    /**
     * 测试权限
     * 5.0待测
     */
    public static boolean checkDangerousPermission(Context context, String permission) {
        boolean isGranted = false;

        if (Build.VERSION.SDK_INT >= 23) {    // 6.0及以上
            isGranted = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED; // 6.0(23|360手机), 7.1(25|小米), 8.1(27|小米)
//            boolean isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;       // 7.1(25|小米), 8.1(27|小米)
        } else {
            isGranted = true;
        }

        if (!(context instanceof Activity)) return isGranted;

        if (!isGranted) { // 没有授予权限
            // 用户上次拒绝时是否没有选中"不再提醒"
            boolean isCanRequireAgain = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission); // 7.1(25|小米), 8.1(27|小米), 360手机权限管理失败(一直false)
            if (!isCanRequireAgain) { // 不能再弹出申请框了
                TOAST.showShort(context, "本应用未能获取权限:" + permission + ", 请手动设置");
            } else {
                requireDangerousPermission((Activity) context, permission);
            }
        }
        return isGranted;
    }


    /**
     * 申请权限
     */
    public static void requireDangerousPermission(Activity context, String permission) {
        ActivityCompat.requestPermissions(context, new String[]{permission}, 123);
    }

    /**
     * 申请权限
     */
    public static void requireDangerousPermission(Activity context, String[] permission) {
        ActivityCompat.requestPermissions(context, permission, 123);
    }

    private void gotoPermissionSetting(Context context) {
        String sdk = android.os.Build.VERSION.SDK; // SDK号

        String model = android.os.Build.MODEL; // 手机型号

        String release = android.os.Build.VERSION.RELEASE; // android系统版本号
        String brand = Build.BRAND;//手机厂商
        if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
            gotoMiuiPermission(context);//小米
        } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
            gotoMeizuPermission(context);
        } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
            gotoHuaweiPermission(context);
        } else {
            context.startActivity(getAppDetailSettingIntent(context));
        }

    }

    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission(Context context) {
        if (context == null) return;
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                context.startActivity(getAppDetailSettingIntent(context));
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission(Context context) {
        if (context == null) return;
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission(Context context) {
        if (context == null) return;
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent(Context context) {
        if (context == null) return null;
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }

}
