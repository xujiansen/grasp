package lib.grasp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class PreferenceUtil {

    /**
     * ------------------------------------存储对象--------------------------------------------------------
     */

    /**
     * 使用SharedPreference保存对象
     *
     * @param key        储存对象的key
     * @param saveObject 储存的对象
     */
    public static void saveSerializableEntity(Context context, String key, Object saveObject) {
        putString(context, key, Object2String(saveObject));
    }

    /**
     * 获取SharedPreference保存的对象
     *
     * @param key     储存对象的key
     * @return object 返回根据key得到的对象
     */
    public static Object getSerializableEntity(Context context, String key) {
        String str = getString(context, key, "");
        if(TextUtils.isEmpty(str)) return null;
        return String2Object(str);
    }


    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     *
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    private static String Object2String(Object object) {
        if(object == null) return "";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            L.logOnly(e);
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


    /**
     * ------------------------------------是否包含--------------------------------------------------------
     */

    public static boolean contains(Context mContext, String key) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            return sharedPreferences.contains(key);
        }
        return false;
    }

    /**
     * ------------------------------------取--------------------------------------------------------
     */

    public static String getString(Context mContext, String key, String defValue) {
        if (mContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getString(key, defValue);
        }
        return defValue;
    }

    public static int getInt(Context mContext, String key, int defValue) {
        if (mContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getInt(key, defValue);
        }
        return defValue;
    }

    public static long getLong(Context mContext, String key, long defValue) {
        if (mContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getLong(key, defValue);
        }
        return defValue;
    }

    public static float getFloat(Context mContext, String key, float defValue) {
        if (mContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getFloat(key, defValue);
        }
        return defValue;
    }

    public static boolean getBoolean(Context mContext, String key, boolean defValue) {
        if (mContext != null) {
            return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(key, defValue);
        }
        return defValue;
    }


    /**
     * ------------------------------------存--------------------------------------------------------
     */

    public static void putString(Context mContext, String key, String value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public static void putInt(Context mContext, String key, int value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.apply();
        }
    }

    public static void putLong(Context mContext, String key, long value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.apply();
        }
    }

    public static void putFloat(Context mContext, String key, float value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putFloat(key, value);
            editor.apply();
        }
    }

    public static void putBoolean(Context mContext, String key, boolean value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    /**
     * ------------------------------------删--------------------------------------------------------
     */

    public static void remove(Context mContext, String... keys) {
        if (keys != null && mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.apply();
        }
    }
}
