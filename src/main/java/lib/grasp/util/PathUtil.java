package lib.grasp.util;

import android.os.Environment;

import com.rooten.BaApp;
import com.rooten.util.ListAvailableStorage;
import com.rooten.util.StorageInfo;

import java.util.List;

import lib.grasp.R;

/**
 * 系统路径
 */
public class PathUtil {

    // 程序目录结构
    /** SD卡相关 */
    private static String APP_ROOT_DIR      = "JS_grasp/";
    /** 项目相关 */
    private static String APP_PROJECT_DIR   = "JS_grasp/";
    // 还有APP版本相关
    // 还有用户相关

    private static final String DIR_NAME_DB                 = "db";               // 日志目录
    private static final String DIR_NAME_LOG                = "error";            // 日志目录
    private static final String DIR_NAME_LOG_ERROR          = "log";              // APP异常日志
    private static final String DIR_NAME_DOWN_TEMP          = ".temp";            // 下载文件[临时]目录
    private static final String DIR_NAME_DOWN_OK            = ".ok";              // 下载文件[完成]目录
    private static final String DIR_NAME_CAMERA_TEMP        = ".cameraTemp";      // 拍照临时文件保存目录
    private static final String DIR_NAME_APK                = ".apk";             // apk下载目录
    private static final String DIR_AVATAR_COMPRESS         = "avatar/compress";  // 头像压缩
    private static final String DIR_AVATAR_CROP             = "avatar/crop";      // 头像裁切

    /**
     * 本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     * <br/>
     * 项目的根目录与资源文件里面的 ${dir_name} 字符串有关
     */
    public static void initPath() {
        APP_ROOT_DIR        = BaApp.getApp().getResources().getString(R.string.dir_name) + "/";
        APP_PROJECT_DIR     = getProjectRootPath();    // 项目cache目录
    }

    /** 头像临时目录(压缩) */
    public static String getCompressAvatarPath() {
        String path = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_AVATAR_COMPRESS + "/";
        FileUtil.ensurePathExists(path);
        return path;
    }

    /** 头像临时目录(裁切) */
    public static String getCropAvatarPath() {
        String path =APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_AVATAR_CROP + "/";
        FileUtil.ensurePathExists(path);
        return path;
    }

    /**
     * 数据库路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getDbPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_DB + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /**
     * LOG路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getLogPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_LOG + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /**
     * LOG_ERROR路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getLogErrorPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_LOG_ERROR + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /**
     * DOWN_TEMP路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getDownTempPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_DOWN_TEMP + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /**
     * DOWN_OK路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getDownOkPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_DOWN_OK + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /**
     * CAMERA_TEMP路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getCameraTempPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_CAMERA_TEMP + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /**
     * APK路径
     * <br/>本方法最好在用户登录成功之后调用, 因为具体文件是以用户为单位存放的
     */
    public static String getApkOkPath(){
        String filePath = APP_PROJECT_DIR + BaApp.getApp().getUserData().mStrUserID + "/" + DIR_NAME_APK + "/";
        FileUtil.ensurePathExists(filePath);
        return filePath;
    }

    /** 获取apk版本文件的文件目录 */
    public static String getApkAbsPath(String appName, boolean isDownPath, String versionName){
        if(isDownPath) return getApkOkPath() + appName + "_" + versionName + ".apk";
        return getDownTempPath() + appName + "_" + versionName + ".apk";
    }

    /** 获取项目根目录 */
    private static String getProjectRootPath(){

        String bizRootPath = "/" + PathUtil.APP_ROOT_DIR;

        // 测试是否插入了可卸载的SD卡
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return Environment.getExternalStorageDirectory() + bizRootPath;
        }

        // 测试是否存在不可卸载的存储卡
        List<StorageInfo> list = ListAvailableStorage.listAvailableStorage(BaApp.getApp());
        for (StorageInfo storage : list) {
            if (!storage.isRemovable && storage.isValid) {// 如果该存储卡不能移除，则是手机内存卡
                return storage.path + bizRootPath;
            }
        }

        // 使用data空间
        return Environment.getDataDirectory() + bizRootPath;
    }
}
