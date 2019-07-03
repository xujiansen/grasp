package lib.grasp.util;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by GaQu_Dev on 2018/12/12.
 */
public class SimCardUtil {
    static String ISDOUBLE;
    static String SIMCARD;
    static String SIMCARD_1;
    static String SIMCARD_2;

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

    /** 双卡状态 */
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
