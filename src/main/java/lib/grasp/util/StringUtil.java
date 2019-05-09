package lib.grasp.util;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    public static String toURLEncoded(String paramString) {
        if (TextUtils.isEmpty(paramString)) return "";

        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException) {
            L.logOnly("toURLEncoded error:" + paramString + localException);
        }
        return "";
    }

    /**
     * 判断字符串是否包含汉字
     */
    public static boolean isContainsChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 判断字符串是否是由字母和数据组成
     */
    public static boolean isAlphaNum(final String value) {
        if (value.length() == 0) return false;
        return value.matches("[a-zA-Z0-9]+");
    }

    /**
     * 判断字符串是否是0-9的数字组成
     */
    public static boolean isNum(final String value) {
        if (value.length() == 0) return false;
        return value.matches("[0-9]+");
    }

    /**
     * 获取String列表中的指定坐标
     */
    public static String getStringAt(final ArrayList<String> arr, int index) {
        if (arr == null || index < 0 || index >= arr.size()) return "";
        return arr.get(index);
    }
}
