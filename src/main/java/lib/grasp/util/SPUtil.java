package lib.grasp.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import com.rooten.BaApp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SPUtil {

    /**
     * ------------------------------------取--------------------------------------------------------
     */

    public static String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getString(key, defValue);
    }

    public static String getAndDelString(String key, String defValue) {
        String result = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getString(key, defValue);
        remove(key);
        return result;
    }

    public static int getInt(String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getInt(key, defValue);
    }

    public static int getAndDelInt(String key, int defValue) {
        int result = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getInt(key, defValue);
        remove(key);
        return result;
    }

    public static long getLong(String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getLong(key, defValue);
    }

    public static long getAndDelLong(String key, long defValue) {
        long result = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getLong(key, defValue);
        remove(key);
        return result;
    }

    public static float getFloat(String key, float defValue) {
        return PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getFloat(key, defValue);
    }

    public static float getAndDelFloat(String key, float defValue) {
        float result = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getFloat(key, defValue);
        remove(key);
        return result;
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getBoolean(key, defValue);
    }

    public static boolean getAndDelBoolean(String key, boolean defValue) {
        boolean result = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp()).getBoolean(key, defValue);
        remove(key);
        return result;
    }

    /**
     * 获取SharedPreference保存的对象
     *
     * @param key 储存对象的key
     * @return object 返回根据key得到的对象
     */
    public static Object getSerializableEntity(String key) {
        String str = getString(key, "");
        if (TextUtils.isEmpty(str)) return null;
        return String2Object(str);
    }

    public static Object getAndDelSerializableEntity(String key) {
        if (TextUtils.isEmpty(key)) return null;
        String str = getString(key, "");
        if (TextUtils.isEmpty(str)) return null;
        Object object = String2Object(str);
        remove(key);
        return object;
    }


    /**
     * ------------------------------------存/改--------------------------------------------------------
     */

    public static void putString(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putInt(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putLong(String key, long value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putFloat(String key, float value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
        Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 使用SharedPreference保存对象
     *
     * @param key        储存对象的key
     * @param saveObject 储存的对象(支持序列化)
     */
    public static void putSerializableEntity(String key, Object saveObject) {
        putString(key, Object2String(saveObject));
    }

    /**
     * ------------------------------------删--------------------------------------------------------
     */

    public static void remove(String... keys) {
        if (keys != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
            Editor editor = sharedPreferences.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.apply();
        }
    }

    /**
     * ------------------------------------是否包含--------------------------------------------------------
     */

    public static boolean contains(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaApp.getApp());
        return sharedPreferences.contains(key);
    }

    /**
     * ------------------------------------帮助--------------------------------------------------------
     */

    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     *
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    private static String Object2String(Object object) {
        if (object == null) return "";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            L.log(e);
            return null;
        }
    }

    /**
     * 使用Base64解密String，返回Object对象
     *
     * @param objectString 待解密的String
     * @return object      解密后的object
     */
    private static Object String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.NO_WRAP);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
