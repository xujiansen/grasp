package lib.grasp.util;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

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
public class PermissionRxUtil {

    /**
     * 测试权限
     */
    public static boolean checkDangerousPermission(FragmentActivity activity, String permission) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        return permissions.isGranted(permission);
    }

    /**
     * 申请权限(单个)
     * <br/>1、返回true：申请成功 ；返回false：申请失败
     * <br/>2、同意后，之后再申请此权限则不再弹出提示框
     */
    @SuppressWarnings("all")
    public static void requireDangerousPermission(FragmentActivity activity, String permissionStr, boolean isKeepRequire) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        permissions.request(permissionStr)          // request
                .subscribe(aBoolean -> {
                    if(aBoolean)        return;     // 给了
                    if(!isKeepRequire)  return;     // 没给但不强求
                    requireDangerousPermission(activity, permissionStr, true);
                });
    }

    /**
     * 申请权限(多个)
     * <br/>1、只要有一个禁止，则返回false；全部同意，则返回true。
     * <br/>2、某个权限同意后，之后再申请此权限则不再弹出提示框，其他的会继续弹出
     * <br/>3、申请多个权限，会有多个弹窗
     */
    @SuppressWarnings("all")
    public static void requireDangerousPermission(FragmentActivity activity, List<String> permissionsList, boolean isKeepRequire) {
        String[] array = permissionsList.toArray(new String[0]);
        requireDangerousPermission(activity, array, isKeepRequire);
    }

    /**
     * 申请权限(多个)
     * <br/>1、只要有一个禁止，则返回false；全部同意，则返回true。
     * <br/>2、某个权限同意后，之后再申请此权限则不再弹出提示框，其他的会继续弹出
     * <br/>3、申请多个权限，会有多个弹窗
     */
    @SuppressWarnings("all")
    public static void requireDangerousPermission(FragmentActivity activity, String[] permissionArray, boolean isKeepRequire) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        permissions.request(permissionArray)            // request
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean)        return;     // 给了
                        if(!isKeepRequire)  return;     // 没给但不强求
                        requireDangerousPermission(activity, permissionArray, true);
                    }
                });
    }



    /**
     * 申请权限(单个)
     * <br/>1、返回true：申请成功 ；返回false：申请失败
     * <br/>2、同意后，之后再申请此权限则不再弹出提示框
     */
    @SuppressWarnings("all")
    public static void requireDangerousPermissionForDetail(FragmentActivity activity, String permissionStr) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        permissions.requestEach(permissionStr)              // requestEach
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.name.equalsIgnoreCase(permissionStr)) {
                            if (permission.granted) {
                                //同意
                            } else if (permission.shouldShowRequestPermissionRationale){
                                //禁止
                            }else {
                                //禁止，且“以后不再询问”，以后申请权限，不会继续弹出提示
                            }
                        }
                    }
                });
    }

    /**
     * 申请权限(多个)
     * <br/>1、只要有一个禁止，则返回false；全部同意，则返回true。
     * <br/>2、某个权限同意后，之后再申请此权限则不再弹出提示框，其他的会继续弹出
     * <br/>3、申请多个权限，会有多个弹窗
     */
    @SuppressWarnings("all")
    public static void requireDangerousPermissionForDetail(FragmentActivity activity, String[] permissionArray) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        permissions.requestEach(permissionArray)            // requestEach
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.name.equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            if (permission.granted) {
                                // 同意
                            } else if (permission.shouldShowRequestPermissionRationale){
                                // 禁止
                            }else {
                                // 禁止，且选择“以后不再询问”，以后申请权限，不会继续弹出提示
                            }
                        }
                        if (permission.name.equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            if (permission.granted) {
                                // 同意
                            }else if (permission.shouldShowRequestPermissionRationale){
                                // 禁止
                            } else {
                                // 禁止，且选择“以后不再询问”，以后申请权限，不会继续弹出提示
                            }
                        }
                        if (permission.name.equalsIgnoreCase(Manifest.permission.READ_CALENDAR)) {
                            if (permission.granted) {
                                // 同意
                            }else if (permission.shouldShowRequestPermissionRationale){
                                // 禁止
                            } else {
                                // 禁止，且选择“以后不再询问”，以后申请权限，不会继续弹出提示
                            }
                        }
                    }
                });
    }

    /**
     * 申请多个权限，获取合并后的详细信息
     * @param activity
     */
    @SuppressWarnings("all")
    public void checkPermissionRequestEachCombined(FragmentActivity activity, String[] permissionArray) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        permissions.requestEachCombined(permissionArray)    // requestEachCombined
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 全部同意后调用
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 只要有一个选择：禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
                        } else {
                            // 只要有一个选择：禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
                        }
                    }
                });
    }

    private void gotoPermissionSetting(Context context) {
        String sdk      = Build.VERSION.SDK;        // SDK号
        String model    = Build.MODEL;              // 手机型号
        String release  = Build.VERSION.RELEASE;    // android系统版本号
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
