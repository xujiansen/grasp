package lib.grasp.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.List;

import cn.com.rooten.util.ListAvailableStorage;
import cn.com.rooten.util.StorageInfo;

/**
 * 系统路径
 */
public class PathUtil {

    // 程序目录结构
    public static final String PATH_DIR_BASE        = "Gaqu/";
    public static final String APP_DIR              = "GaquPaopao/";
    public static final String APP_DIR_UPGRADE      = "upgrade/";
    public static final String APP_DIR_CACHE        = "cache/";
    public static final String FILE_DB_PARENT       = "/database/";

    // 定义程序中所有实用的路径
    public static String PATH_APP_ERROR = "";           // APP异常日志
    public static String PATH_DOWN_TEMP = "";           // 下载文件临时文件保存目录
    public static String PATH_DOWN_OK = "";             // 下载文件最终文件保存目录
    public static String PATH_CACHE_SEND = "";          // 文件发送的缓存目录
    public static String PATH_CACHE_RECEIVE = "";       // 文件接收缓存目录
    public static String PATH_CACHE_LOG = "";           // 日志目录
    public static String PATH_AVATAR_ONLINE = "";       // 在线头像缓存目录
    public static String PATH_AVATAR_OFFLINE = "";      // 离线头像缓存目录
    public static String PATH_MARKET_APP = "";          // 应用市场应用程序
    public static String PATH_CAMERA_TEMP = "";         // 拍照临时文件保存目录


    public static void initPath(Context context) {
        String sdCard       = getSdCard(context);
        String cachePath    = getCachePath(context);

        PATH_APP_ERROR          = cachePath + "error/";
        PATH_DOWN_TEMP          = cachePath + ".temp/";
        PATH_DOWN_OK            = cachePath + ".ok/";
        PATH_CACHE_SEND         = cachePath + "send/";
        PATH_CACHE_RECEIVE      = cachePath + "receive/";
        PATH_CACHE_LOG          = cachePath + "log/";
        PATH_AVATAR_ONLINE      = cachePath + ".head_online/";
        PATH_AVATAR_OFFLINE     = cachePath + ".head_offline/";
        PATH_MARKET_APP         = cachePath + "app/";
        PATH_CAMERA_TEMP        = cachePath + ".cameraTemp/";

        // 创建目录
        FileUtil.createPath(PATH_APP_ERROR);
        FileUtil.createPath(PATH_DOWN_TEMP);
        FileUtil.createPath(PATH_DOWN_OK);
        FileUtil.createPath(PATH_CACHE_SEND);
        FileUtil.createPath(PATH_CACHE_RECEIVE);
        FileUtil.createPath(PATH_CACHE_LOG);
        FileUtil.createPath(PATH_AVATAR_ONLINE);
        FileUtil.createPath(PATH_AVATAR_OFFLINE);
        FileUtil.createPath(PATH_MARKET_APP);
        FileUtil.createPath(PATH_CAMERA_TEMP);
    }

    public static String getCachePath(Context context) {
        return (getDataPath(context) + PathUtil.APP_DIR_CACHE);
    }

    public static String getDataPath(Context context) {
        List<StorageInfo> list = ListAvailableStorage.listAvailableStorage(context);
        for (StorageInfo storage : list) {
            if (!storage.isRemovable && storage.isValid) // 如果该存储卡不能移除，则是手机内存卡
            {
                return storage.path + "/" + PathUtil.PATH_DIR_BASE + PathUtil.APP_DIR;
            }
        }
        return Environment.getExternalStorageDirectory() + "/" + PathUtil.PATH_DIR_BASE + PathUtil.APP_DIR;
    }

    public static String getSdCard(Context context) {
        List<StorageInfo> list = ListAvailableStorage.listAvailableStorage(context);
        for (StorageInfo storage : list) {
            if (!storage.isRemovable && storage.isValid) // 如果该存储卡不能移除，则是手机内存卡
            {
                return storage.path;
            }
        }
        return Environment.getExternalStorageDirectory().toString();
    }

    /** 获取apk版本文件的文件目录 */
    public static String getApkAbsPath(String appName, boolean isDownPath, String versionName){
        if(isDownPath){
            return PathUtil.PATH_DOWN_OK + "/" + appName + "_" + versionName + ".apk";
        }
        else{
            return PathUtil.PATH_DOWN_TEMP + "/" + appName + "_" + versionName + ".apk";
        }
    }

    /** 获取apk版本文件的文件夹目录 */
    public static String getApkPath(boolean isDownPath){
        if(isDownPath){
            return PathUtil.PATH_DOWN_OK;
        }
        else{
            return PathUtil.PATH_DOWN_TEMP;
        }
    }

}
