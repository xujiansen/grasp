package lib.grasp.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lib.grasp.widget.MessageBoxGrasp;


/**
 * 手机相关操作/信息
 */
public class PhoneUtil {

    /** 拨打电话（直接拨打电话） */
    public static void callPhone(AppCompatActivity activity, final String phoneNum) {
        if(!NumberUtil.isPhoneNum(phoneNum)){
            TOAST.showShort(activity, "联系号码有误!");
            return;
        }

        MessageBoxGrasp.confirmMsg(activity, "确认", "拨打公司客服电话[" + NumberUtil.getFormatPhone(phoneNum) + "]？", v -> {
            if (!PermissionRxUtil.checkDangerousPermission(activity, android.Manifest.permission.CALL_PHONE)) return;
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            activity.startActivity(intent);
        }, null, true);
    }

    /** 获取设备品牌 */
    static public String getDeviceType() {
        return Build.BRAND;
    }

    /** 获取设备名称 */
    static public String getDeviceName() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public static boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            L.log(e.getMessage());
        }
        return bool;
    }

    /**
     * 授权root用户权限
     * @param command
     */
    public static boolean rootCommand(String command) {
        Process process = null;
        DataOutputStream dos = null;
        try {
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(command+"\n");
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 获取手机IMEI号
     * <p>
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    public static String getIMEI(AppCompatActivity activity) {
        if (!PermissionRxUtil.checkDangerousPermission(activity, android.Manifest.permission.READ_PHONE_STATE))
            return "";
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /** 获取手机号码(成功率低) */
    private void getNumber(AppCompatActivity activity) {
        if (!PermissionRxUtil.checkDangerousPermission(activity, android.Manifest.permission.READ_PHONE_STATE))
            return;
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return;
        String phoneNumber1 = tm.getLine1Number();  //手机号码

//        String phoneNumber2 = tm.getGroupIdLevel1();

        boolean isDouble = isDoubleSim(activity);
        if (isDouble) {
            // tv.setText("这是双卡手机！");
        } else {
            // tv.setText("这是单卡手机");
        }

    }

    /** 是否双卡 */
    public static boolean isDoubleSim(Context context){
        boolean isDouble = true;
        Method method = null;
        Object result_0 = null;
        Object result_1 = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            // 只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
            method = TelephonyManager.class.getMethod("getSimStateGemini", new Class[]{int.class});
            // 获取SIM卡1
            result_0 = method.invoke(tm, new Object[]{new Integer(0)});
            // 获取SIM卡2
            result_1 = method.invoke(tm, new Object[]{new Integer(1)});
        } catch (SecurityException e) {
            isDouble = false;
            e.printStackTrace();
            // System.out.println("1_ISSINGLETELEPHONE:"+e.toString());
        } catch (NoSuchMethodException e) {
            isDouble = false;
            e.printStackTrace();
            // System.out.println("2_ISSINGLETELEPHONE:"+e.toString());
        } catch (IllegalArgumentException e) {
            isDouble = false;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            isDouble = false;
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            isDouble = false;
            e.printStackTrace();
        } catch (Exception e) {
            isDouble = false;
            e.printStackTrace();
            // System.out.println("3_ISSINGLETELEPHONE:"+e.toString());
        }
        return isDouble;
    }

    /** 测试双卡可用状态 */
    public void testDoubleStatus(Context context) {
        boolean isDouble = true;
        Method method = null;
        Object result_0 = null;
        Object result_1 = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            method = TelephonyManager.class.getMethod("getSimStateGemini", new Class[]{int.class}); // 只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
            result_0 = method.invoke(tm, new Object[]{new Integer(0)}); // 获取SIM卡1
            result_1 = method.invoke(tm, new Object[]{new Integer(1)}); // 获取SIM卡2
        } catch (Exception e) {
            isDouble = false;
        }
        if (isDouble) {
            // 如下判断哪个卡可用.双卡都可以用
            if (result_0.toString().equals("5") && result_1.toString().equals("5")) {
                // "双卡可用"
            } else if (!result_0.toString().equals("5") && result_1.toString().equals("5")) {// 卡二可用
                // "卡二可用"
            } else if (result_0.toString().equals("5") && !result_1.toString().equals("5")) {// 卡一可用
                // "卡一可用"
            } else {
                // "飞行模式", 两个卡都不可用(飞行模式会出现这种种情况)
            }
        } else {
            // 保存为单卡手机
        }
    }
}
