package lib.grasp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public final class PreferenceUtil {

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
            editor.commit();
        }
    }

    public static void putInt(Context mContext, String key, int value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static void putLong(Context mContext, String key, long value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public static void putFloat(Context mContext, String key, float value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putFloat(key, value);
            editor.commit();
        }
    }

    public static void putBoolean(Context mContext, String key, boolean value) {
        if (mContext != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
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
            editor.commit();
        }
    }
}
