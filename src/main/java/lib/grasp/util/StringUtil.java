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
            L.log("toURLEncoded error:" + paramString + localException);
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

    /**
     * 对字符串处理:将指定位置到指定位置的字符以星号代替
     *
     * @param content
     *            传入的字符串
     * @param begin
     *            开始位置
     * @param end
     *            结束位置
     * @return
     */
    public static String getStarString(String content, int begin, int end) {

        if (begin >= content.length() || begin < 0) {
            return content;
        }
        if (end >= content.length() || end < 0) {
            return content;
        }
        if (begin >= end) {
            return content;
        }
        String starStr = "";
        for (int i = begin; i < end; i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, begin) + starStr + content.substring(end, content.length());
    }




    /**
     * 对字符加星号处理：除前面几位和后面几位外，其他的字符以星号代替
     *
     * @param content
     *            传入的字符串
     * @param frontNum
     *            保留前面字符的位数
     * @param endNum
     *            保留后面字符的位数
     * @return 带星号的字符串
     */

    public static String getStarString2(String content, int frontNum, int endNum) {

        if (frontNum >= content.length() || frontNum < 0) {
            return content;
        }
        if (endNum >= content.length() || endNum < 0) {
            return content;
        }
        if (frontNum + endNum >= content.length()) {
            return content;
        }
        String starStr = "";
        for (int i = 0; i < (content.length() - frontNum - endNum); i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, frontNum) + starStr
                + content.substring(content.length() - endNum, content.length());

    }
}
