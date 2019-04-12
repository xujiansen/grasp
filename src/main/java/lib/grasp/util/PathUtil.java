package lib.grasp.util;

import android.content.Context;
import android.os.Environment;

import java.util.List;

import com.rooten.util.ListAvailableStorage;
import com.rooten.util.StorageInfo;

/**
 * 系统路径
 */
public class PathUtil {

    // 程序目录结构
    public static final String PATH_DIR_BASE        = "Netoo/";
    public static final String APP_DIR              = "SunUnbrella/";

    // 定义程序中所有实用的路径
    public static String PATH_DB            = "";           // DB目录
    public static String PATH_LOG           = "";           // 日志目录
    public static String PATH_LOG_ERROR     = "";           // APP异常日志
    public static String PATH_DOWN_TEMP     = "";           // 下载文件[临时]目录
    public static String PATH_DOWN_OK       = "";           // 下载文件[完成]目录
    public static String PATH_CAMERA_TEMP   = "";           // 拍照临时文件保存目录

    public static void initPath(Context context) {
        String projectRootPath  = getProjectRootPath(context);    // 项目cache目录

        PATH_DB                 = projectRootPath + "db/";
        PATH_LOG_ERROR          = projectRootPath + "error/";
        PATH_LOG                = projectRootPath + "log/";
        PATH_DOWN_TEMP          = projectRootPath + ".temp/";
        PATH_DOWN_OK            = projectRootPath + ".ok/";
        PATH_CAMERA_TEMP        = projectRootPath + ".cameraTemp/";

        // 创建目录
        FileUtil.createPath(PATH_DB);
        FileUtil.createPath(PATH_LOG_ERROR);
        FileUtil.createPath(PATH_DOWN_TEMP);
        FileUtil.createPath(PATH_DOWN_OK);
        FileUtil.createPath(PATH_LOG);
        FileUtil.createPath(PATH_CAMERA_TEMP);
    }

    /** 获取项目根目录 */
    private static String getProjectRootPath(Context context){

        String bizRootPath = "/" + PathUtil.PATH_DIR_BASE + PathUtil.APP_DIR;

        // 测试是否插入了可卸载的SD卡
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory() + bizRootPath;
        }

        // 测试是否存在不可卸载的存储卡
        List<StorageInfo> list = ListAvailableStorage.listAvailableStorage(context);
        for (StorageInfo storage : list) {
            if (!storage.isRemovable && storage.isValid) {// 如果该存储卡不能移除，则是手机内存卡
                return storage.path + bizRootPath;
            }
        }

        // 使用data空间
        return Environment.getDataDirectory() + bizRootPath;
    }

    /** 获取apk版本文件的文件目录 */
    public static String getApkAbsPath(String appName, boolean isDownPath, String versionName){
        if(isDownPath) return PathUtil.PATH_DOWN_OK + "/" + appName + "_" + versionName + ".apk";
        return PathUtil.PATH_DOWN_TEMP + "/" + appName + "_" + versionName + ".apk";
    }

    public static String getErrorPath(){
        return PathUtil.PATH_LOG_ERROR;
    }

}
