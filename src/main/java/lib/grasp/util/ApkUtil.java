package lib.grasp.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

import cn.com.rooten.util.Utilities;

/**
 * Created by GaQu_Dev on 2018/10/31.
 */
public class ApkUtil {

    public static void installAPK(Context context, File file) {
        if(!FileUtil.isFileExists(file)) return;
        Uri apk = getImageContentUri(context, file);

        if (Build.VERSION.SDK_INT >= 24) {
            install(context, file);
        }

        else {
            openFile(context, file);
        }

//        else if (Build.VERSION.SDK_INT < 23) {
//            Intent intents = new Intent();
//            intents.setAction(Intent.ACTION_VIEW);
////                intents.addCategory("android.intent.category.DEFAULT");
//            intents.setDataAndType(apk, "application/vnd.android.package-archive");
//            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intents);
//        }
    }

    /**
     * android6.0之后的升级更新
     */
    public static void openFile(Context context, File file) {
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

    public static String getMIMEType(File var0) {
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
    public static void install(Context context, File file) {
        Uri apkUri = FileProvider.getUriForFile(context, "com.gaqu.gaqupaopao_support.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }










    public static Drawable getApkIcon(Context context, String apkPath) {
        if (!Utilities.fileExists(apkPath)) return null;

        PackageManager pckManager = context.getPackageManager();
        PackageInfo info = pckManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info == null) return null;

        ApplicationInfo appInfo = info.applicationInfo;
        appInfo.sourceDir = apkPath;
        appInfo.publicSourceDir = apkPath;
        return appInfo.loadIcon(pckManager);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static String getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
