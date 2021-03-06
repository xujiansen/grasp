package lib.grasp.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.rooten.frame.ActivityMgr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Apk相关操作
 * <br/>
 * App相关操作(是否存活, 推到前台, 手动结束)
 */
public class AppUtil {

    /**
     * 启动远程服务
     */
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * 获取apk文件的ICON
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        if (!FileUtil.fileExists(apkPath)) return null;

        PackageManager pckManager = context.getPackageManager();
        PackageInfo info = pckManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info == null) return null;

        ApplicationInfo appInfo = info.applicationInfo;
        appInfo.sourceDir = apkPath;
        appInfo.publicSourceDir = apkPath;
        return appInfo.loadIcon(pckManager);
    }


    /**
     * 安装APK
     */
    public static void installAPK(Context context, String filePath) {
        installAPK(context, new File(filePath));
    }

    /**
     * 安装APK(7.0之前)
     */
    public static void installAPK(Context context, File file) {
        installAPK(context, file, "");
    }

    /**
     * 安装APK
     */
    public static void installAPK(Context context, String filePath, String provider) {
        installAPK(context, new File(filePath), provider);
    }

    /**
     * 安装APK(7.0及以后)
     */
    public static void installAPK(Context context, File file, String provider) {
        if(context == null) return;
        if (!FileUtil.isFileExists(file)) return;

        if (Build.VERSION.SDK_INT >= 24) install_7(context, file, provider);
        else install_456(context, file);
    }

    /**
     * android6.0之后的升级更新
     */
    private static void install_456(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取文件的mine类型
     */
    private static String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    /**
     * android7.0之后的更新
     * 通过隐式意图调用系统安装程序安装APK
     */
    private static void install_7(Context context, File file, String provider) {
        Uri apkUri = FileProvider.getUriForFile(context, provider, file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 方法描述：判断某一[应用]是否正在运行
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false表示没有运行
     */
    public static boolean isAppAlive(Context context, String packageName) {
        int uid = getPackageUid(context, packageName);
        if (uid > 0) {
            boolean rstA = isAppRunning(context, packageName);  //   目标APP是否崩了(不包括远程APP,例如:remote)
            boolean rstB = isProcessRunning(context, uid);      //   进程是否(全)崩了(包含主进程与远程进程),, 一个应用只有一个uid，但是可以有多个pid（通过process属性来指定进程）
            return rstA || rstB;
        } else {
            return false;
        }
    }

    /**
     * 方法描述：判断某一[Service]是否正在运行
     *
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断本应用是否[位于前台]
     *
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        /**枚举进程*/
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将本应用[推到前台]
     * <br/>
     * 当本应用位于后台时，则将它切换到最前端
     */
    public static void setTopApp(Context context) {
        if (!isRunningForeground(context)) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            if (activityManager == null) return;

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);
            if (appTask == null) return;
            for (ActivityManager.RecentTaskInfo taskInfo : appTask) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.baseIntent.toString().contains(context.getPackageName())) {
                    context.startActivity(taskInfo.baseIntent);
//                    activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                    break;
                }
            }

//            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
//            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
//                /**找到本应用的 task，并将它切换到前台*/
//                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
//                    activityManager.moveTaskToFront(taskInfo.id, 0);
//                    break;
//                }
//            }
        }
    }

    /**
     * [辅助]判断某一[应用]是否正在运行
     *
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * [辅助]获取已安装应用的 uid，-1 表示未安装此应用或程序异常
     */
    public static int getPackageUid(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                return applicationInfo.uid;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    /**
     * [辅助]判断某一 uid 的程序是否有正在运行的进程，即是否存活
     *
     * @param context 上下文
     * @param uid     已安装应用的 uid
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isProcessRunning(Context context, int uid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() > 0) {
            for (ActivityManager.RunningServiceInfo appProcess : runningServiceInfos) {
                if (uid == appProcess.uid) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取App的VersionName
     */
    public static String getAppVersionName(Context context) {
        String packageName = context.getPackageName();
        return getAppVersionName(context, packageName);
    }

    /**
     * 获取App的VersionName
     */
    public static String getAppVersionName(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 获取本App的VersionCode
     */
    public static int getAppVersionCodeCurr(Context context) {
        String packageName = context.getPackageName();
        return getAppVersionCodeTarget(context, packageName);
    }

    /**
     * 获取指定报名App的VersionCode
     */
    public static int getAppVersionCodeTarget(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static void installSilence(Context context, String apkPath) {
        if(!PhoneUtil.isRoot()){
            Toast.makeText(context, "当前设备暂未ROOT或者本应用未获取到设备权限", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            boolean result = false;
            DataOutputStream dataOutputStream = null;
            BufferedReader errorStream = null;
            try {
                // 申请su权限
                Process process = Runtime.getRuntime().exec("su");
                dataOutputStream = new DataOutputStream(process.getOutputStream());
                // 执行pm install命令
                String command = "pm install -r " + apkPath + "\n";
                dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
                dataOutputStream.flush();
                dataOutputStream.writeBytes("exit\n");
                dataOutputStream.flush();
                process.waitFor();
                errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String msg = "";
                String line;
                // 读取命令的执行结果
                while ((line = errorStream.readLine()) != null) {
                    msg += line;
                }
                L.log("install msg is " + msg);
                // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
                if (!msg.contains("Failure")) {
                    result = true;
                }
            } catch (Exception e) {
                L.log(e.getMessage());
            } finally {
                try {
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                    if (errorStream != null) {
                        errorStream.close();
                    }
                } catch (IOException e) {
                    L.log(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 完全退出App,
     * 如果不把当前所未关闭的Activity关闭, 在杀死进程之后, 会重新启动栈顶的Activity
     */
    public static void destroyApp(boolean isKillProcess) {
        // 销毁Activity管理类
        ActivityMgr.getDefault().onDestroy();

        // 杀死本进程
        if (isKillProcess) android.os.Process.killProcess(android.os.Process.myPid());

//        System.exit(1); // ?
    }
}
