package cn.com.rooten.help.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.rooten.frame.IActivityResult;
import cn.com.rooten.frame.IResultListener;
import lib.grasp.R;
import lib.grasp.util.L;

/**
 * android 6.0以上权限申请
 */

public class AppPermissionApply {
    private final int PERMISSION_REQ_CODE = 9;

    // 单例实例
    private static AppPermissionApply mInstance;

    // 回调接口
    private onAppPermissionApplyListener mListener;

    public static AppPermissionApply getInstance() {
        synchronized (AppPermissionApply.class) {
            if (mInstance == null) {
                mInstance = new AppPermissionApply();
            }
            return mInstance;
        }
    }

    public void checkPermission(Activity activity, onAppPermissionApplyListener l) {
        mListener = l;

        // 如果是6.0以下的直接返回
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mListener != null) mListener.endResult(true);
            return;
        }

        List<String> list = getAppPermission(activity);
        if (list.size() == 0) {
            if (mListener != null) mListener.endResult(true);
            return;
        }

        // 需要申请授权的权限
        List<String> groupPermissions = new ArrayList<>();
        for (String permission : list) {
            // 如果没有该权限并且该权限是危险的需要申请的
            if (!hasPermission(activity, permission) && isDangerousPermission(permission)) {
                String groupPermission = AppDangerousPermission.PermissionMap.get(permission);
                if (groupPermission == null) continue;
                groupPermissions.add(groupPermission);
            }
        }

        // 没有需要申请的权限
        if (groupPermissions.size() == 0) {
            if (mListener != null) mListener.endResult(true);
            return;
        }

        // 将未同意的权限通知申请授权
        String[] permissions = groupPermissions.toArray(new String[]{});
        requestPermission(activity, permissions);
    }

    public boolean isAllPermissionGranted(Activity activity) {
        // 如果是6.0以下的直接返回
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> list = getAppPermission(activity);
        if (list.size() == 0) return true;

        // 需要申请授权的权限
        List<String> groupPermissions = new ArrayList<>();
        for (String permission : list) {
            // 如果没有该权限并且该权限是危险的需要申请的
            if (!hasPermission(activity, permission) && isDangerousPermission(permission)) {
                String groupPermission = AppDangerousPermission.PermissionMap.get(permission);
                if (groupPermission == null) continue;
                groupPermissions.add(groupPermission);
            }
        }

        // 没有需要申请的权限
        return groupPermissions.size() == 0;
    }

    /**
     * ps，6.0申请权限，只有以下的权限需要申请其余的还是以前注册的形式就可以了
     */
    private boolean isDangerousPermission(String permission) {
        return AppDangerousPermission.PermissionMap.containsKey(permission);
    }

    /** 获取mainfest里面的权限 */
    private List<String> getAppPermission(Context context) {
        List<String> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = pi.requestedPermissions;
            if (permissions == null || permissions.length == 0) return list;

            for (String str : permissions) {
                try {
                    PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
                    list.add(permissionInfo.name);
                } catch (Exception e) {
                    L.logOnly(AppPermissionApply.class, "getAppPermission::Exception", e.toString());
                }
            }
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    public static PermissionInfo getPermissionInfo(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = pi.requestedPermissions;
            if (permissions == null || permissions.length == 0) return null;

            for (String str : permissions) {
                try {
                    PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
                    if (!permissionInfo.name.equals(permission)) continue;
                    return permissionInfo;
                } catch (Exception e) {
                    L.logOnly(AppPermissionApply.class, "getAppPermission::Exception", e.toString());
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasPermission(Context context, String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQ_CODE);
    }

    /**
     * 回调返回
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            if (requestCode != PERMISSION_REQ_CODE || mListener == null) return;

            int permissionsLen = permissions.length;
            int grantResultLen = grantResults.length;

            boolean isAllGranted = true;
            int count = Math.min(permissionsLen, grantResultLen);
            for (int i = 0; i < count; i++) {
                String permission = permissions[i];
                int granted = grantResults[i];
                boolean isGranted = granted == PackageManager.PERMISSION_GRANTED;

                // 如果其中一个处理了，就直接返回
                boolean params = mListener.onResult(permission, isGranted);
                if (params) return;

                // 是否所有的权限都获取
                isAllGranted = isAllGranted && isGranted;
            }

            // 最后回调
            mListener.endResult(isAllGranted);
        } finally {
            // 置空，一次只能使用一次
            mListener = null;
        }
    }

    public void goToSetting(final IActivityResult activityResult, final Runnable successRun) {
        if (!(activityResult instanceof Activity)) return;

        final Activity activity = (Activity) activityResult;
        DialogInterface.OnClickListener setting = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                    activityResult.startForResult(intent, new IResultListener() {
                        @Override
                        public void onResult(int resultCode, Intent data) {
                            if (isAllPermissionGranted(activity)) {
                                if (successRun != null) successRun.run();
                                return;
                            }
                            activity.finish();
                        }
                    });
                } catch (Exception e) {
                    activity.finish();
                    Toast.makeText(activity, "找不到对应的设置界面！", Toast.LENGTH_SHORT).show();
                }
            }
        };

        DialogInterface.OnClickListener exit = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        };

        StringBuilder buf = new StringBuilder();
        buf.append("当前应用缺少必要权限。").append("\r\n");
        buf.append("请点击\"设置\"-\"权限\"-打开所需权限。");

        // 初始化并显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_AlertDialog);
        builder.setTitle("帮助");
        builder.setMessage(buf.toString());
        builder.setNegativeButton("退出", exit);
        builder.setPositiveButton("设置", setting);
        builder.show();
    }

    public interface onAppPermissionApplyListener {
        boolean onResult(String permission, boolean granted);

        void endResult(boolean isAllGranted);
    }

    /********************************************需要进入设置界面手动设置的权限***************************************/
    public static void checkAlertWindowPermission(Activity activity) {
        if (hasSpecialPermission(activity, AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)) {
            return;
        }

        requestAlertWindowPermission(activity);
    }

    /**
     * 申请悬浮窗权限
     */
    public static void requestAlertWindowPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        startActivity(activity, intent);
    }

    public static void checkWriteSettingPermission(Activity activity) {
        if (hasSpecialPermission(activity, AppOpsManager.OPSTR_WRITE_SETTINGS)) {
            return;
        }

        requestWriteSettingPermission(activity);
    }

    /**
     * 申请修改设置的权限
     */
    public static void requestWriteSettingPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;

        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        startActivity(activity, intent);
    }

    public static void checkUsageStatusPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        // 是否拥有查询其他应用的权限
        if (hasSpecialPermission(activity, AppOpsManager.OPSTR_GET_USAGE_STATS)) {
            return;
        }

        requestUsageStatusPermission(activity);
    }

    public static void requestUsageStatusPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(activity, intent);
    }

    private static void startActivity(Activity activity, Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            L.logOnly(AppPermissionApply.class, "startActivityForSpecial::Exception", e.toString());
        }
    }

    /**
     * 经过测试需要跳转到设置界面上用户手动设置的权限用此方面进行判断
     */
    private static boolean hasSpecialPermission(Context context, String op) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true;

        try {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(op, android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 跳转到应用详情设置界面
     */
    public static void gotoAppDetailSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        startActivity(activity, intent);
    }
}
