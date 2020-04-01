
package com.rooten.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lib.grasp.R;

final public class Util {

    public static int str2Int(String val, int defVal) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static float str2Float(String val, float defVal) {
        try {
            return Float.parseFloat(val);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static long str2Long(String val, long defVal) {
        try {
            if (val.contains(".")) {
                float num = Float.parseFloat(val);
                return (long) num;
            }

            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static String getString(Map<?, ?> map, String key) {
        if (map == null) return "";
        String value = (String) map.get(key);
        return value == null ? "" : value;
    }

    public static String obj2String(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return "";
    }

    public static String getString(String[] arr, int index) {
        try {
            return arr[index];
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean getBoolean(String str) {
        return str != null && str.equals("true");
    }

    public static int getInt(Map<?, ?> map, String key, int defaultValue) {
        try {
            return (Integer) map.get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static double getDouble(Map<?, ?> map, String key, double defaultValue) {
        try {
            return (Double) map.get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(JSONObject obj, String key, int defaultValue) {
        try {
            return (Integer) obj.get(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(Intent intent, String key, int defaultValue) {
        try {
            int value = intent.getIntExtra(key, defaultValue);
            return value;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getString(List<? extends CharSequence> list, int position) {
        try {
            return list.get(position).toString();
        } catch (Exception e) {
        }
        return "";
    }

    public static String substring(String str, int start, int end) {
        try {
            return str.substring(start, end);
        } catch (Exception e) {
        }
        return "";
    }

    public static Object get(List<? extends Object> list, int position) {
        try {
            return list.get(position);
        } catch (Exception e) {
        }
        return null;
    }

    public static Object get(JSONObject obj, String name) {
        if (obj == null) return null;
        Object value = obj.get(name);
        return value;
    }

    public static String getString(JSONObject obj, String name) {
        if (obj == null) return "";
        String value = (String) obj.get(name);
        return value == null ? "" : value;
    }

    public static String getString(Intent obj, String name) {
        if (obj == null) return "";
        String value = obj.getStringExtra(name);
        return value == null ? "" : value;
    }

    public static double getDouble(Intent obj, String name)
    {
        if (obj == null) return 0;
        return obj.getDoubleExtra(name, 0);
    }

    public static long getLong(Intent obj, String name)
    {
        if (obj == null) return 0;
        return obj.getLongExtra(name, 0);
    }


    public static long getLong(Bundle obj, String name)
    {
        if (obj == null) return 0;
        Object object = obj.get(name);
        return object instanceof Long ? (long) object : 0;
    }

    public static String getString(Bundle obj, String name) {
        if (obj == null) return "";
        String value = obj.getString(name);
        return value == null ? "" : value;
    }

    public static boolean getBoolean(JSONObject obj, String name) {
        if (obj == null) return false;
        Object object = obj.get(name);
        return object instanceof Boolean ? (Boolean) object : false;
    }

    public static boolean getBoolean(Map<String, Boolean> obj, String name) {
        if (obj == null) return false;
        Object object = obj.get(name);
        return object == null ? false : (Boolean) object;
    }

    public static boolean getBoolean(Intent intent, String name) {
        return intent != null && intent.getBooleanExtra(name, true);
    }

    public static boolean getBoolean(Intent intent, String name, boolean defVal) {
        return intent != null && intent.getBooleanExtra(name, defVal);
    }

    public static String[] split(String str, String regularExpression) {
        try {
            String[] arr = str.split(regularExpression);
            ArrayList<String> list = new ArrayList<>();
            for (String s : arr) {
                if (TextUtils.isEmpty(s)) continue;
                list.add(s);
            }

            return list.toArray(new String[]{});
        } catch (Exception e) {
            return new String[]{};
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static String getOSName() {
        return "Android";
    }

    public static void setStatusBarBgInKitKat(Activity activity) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBg = activity.getResources().getColor(R.color.colorPrimaryDark);
            activity.getWindow().getDecorView().setBackgroundColor(statusBg);
        }
    }
}
