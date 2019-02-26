package lib.grasp.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.widget.Toast;

import lib.grasp.widget.MessageBoxGrasp;

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

        if(Build.VERSION.SDK_INT >= 23){    // 6.0及以上
            isGranted = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED; // 6.0(23|360手机), 7.1(25|小米), 8.1(27|小米)
//            boolean isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;       // 7.1(25|小米), 8.1(27|小米)
        }
        else {
            isGranted = true;
        }

        if(!(context instanceof Activity)) return isGranted;

        if(!isGranted){ // 没有授予权限
            // 用户上次拒绝时是否没有选中"不再提醒"
            boolean isCanRequireAgain = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission); // 7.1(25|小米), 8.1(27|小米), 360手机权限管理失败(一直false)
            if(!isCanRequireAgain){ // 不能再弹出申请框了
                TOAST.showShort(context, "本应用未能获取权限:" + permission + ", 请手动设置");
            }
            else{
                requireDangerousPermission((Activity)context, permission);
            }
        }
        return isGranted;
    }


    /** 申请权限 */
    public static void requireDangerousPermission(Activity context, String permission){
        ActivityCompat.requestPermissions(context, new String[]{permission}, 123);
    }

    /** 申请权限 */
    public static void requireDangerousPermission(Activity context, String[] permission){
        ActivityCompat.requestPermissions(context, permission, 123);
    }


    /**
     * 测试-摄像头
     */
    public static boolean isCameraEnable() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
        }
        return canUse;
    }

    /**
     * 测试-定位
     */
    public static boolean isLocateEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager == null) return false;
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }
}
