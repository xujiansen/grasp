package lib.grasp.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字, 进度, 手机号, 身份证
 */
public class NumberUtil {

    /** 进度百分比 */
    public static int getProgress(long size, long allSize) {
        try {
            float s = size;
            float per = s / allSize;
            BigDecimal b = new BigDecimal(per);
            per = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return (int) (per * 100);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 校验身份证号码
     */
    public static boolean isIDCardValid(final String value) {
        if (!checkIdCardDate(value)) return false;

        if (value.length() == 18 && !isIDCard18(value)) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否是手机号码
     */
    public static boolean isPhoneNum(String phone) {
        if(TextUtils.isEmpty(phone)) return false;
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            return m.matches();
        }
    }

    /**
     * 判断是否是银行卡号
     */
    public static boolean isBankCard(String cardNo) {
        char bit = getBankCardCheckCode(cardNo.substring(0, cardNo.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardNo.charAt(cardNo.length() - 1) == bit;
    }

    /**
     * 以344格式格式化手机号码
     */
    public static String getFormatPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        if (!isPhoneNum(phone)) return phone;
        return phone.substring(0,3) + " " + phone.substring(3, 7) + " " + phone.substring(7, 11);
    }

    /**
     * 隐藏手机号码中间四位
     */
    public static String getSecretPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        if (!isPhoneNum(phone)) return phone;
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 隐藏身份证号码中间四位
     */
    public static String getSecretIdCard(String idCard) {
        if (TextUtils.isEmpty(idCard)) return "";
        return idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }

    /**
     * 校验是否是4位数字
     */
    public static boolean isAllDigtal(final String value) {
        return value.matches("^\\d{4}$");
    }

    /** 去掉小数点之后多余的0 */
    public static String subZeroAndDot(double money) {
        String s = String.valueOf(money);
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }


    /* -------------------------------------------------private-------------------------------------------------------------- */

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }


    private static boolean checkDate(int nYear, int nMonth, int nDay) {
        if (nYear < 1800 || nMonth < 1 || nMonth > 12 || nDay < 1 || nDay > 31)
            return false;

        if (nMonth == 2) {
            if (nDay > 29) return false;
            if (nDay == 29 && !((nYear % 4 == 0 && nYear % 100 != 0) || nYear % 400 == 0))
                return false;
            return true;
        }

        if (nMonth == 4 || nMonth == 6 || nMonth == 9 || nMonth == 11) {
            if (nDay < 31) return true;
            else return false;
        }
        return true;
    }

    /**
     * 校验8位日期
     */
    private static boolean checkDate8(String date) {
        if (date.length() != 8) return false;
        try {
            int year = Integer.valueOf(date.substring(0, 4)).intValue();
            int month = Integer.valueOf(date.substring(4, 6)).intValue();
            int day = Integer.valueOf(date.substring(6, 8)).intValue();
            return checkDate(year, month, day);
        } catch (NumberFormatException e) {
        }
        return false;
    }

    /**
     * 校验18位身份证号码
     */
    private static boolean isIDCard18(final String value) {
        if (value == null || value.length() != 18) return false;
        if (!value.matches("[\\d]+[X]?")) return false;

        String code = "10X98765432";
        int weight[] = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};

        int nSum = 0;
        for (int i = 0; i < 17; ++i) {
            nSum += (int) (value.charAt(i) - '0') * weight[i];
        }

        int nCheckNum = nSum % 11;
        char chrValue = value.charAt(17);
        char chrCode = code.charAt(nCheckNum);
        if (chrValue == chrCode) return true;
        if (nCheckNum == 2 && (chrValue + ('a' - 'A') == chrCode))
            return true;

        return false;
    }

    /**
     * 校验身份证的日期格式是否正确
     */
    private static boolean checkIdCardDate(String value) {
        if (value == null || !value.matches("[\\d]+[X]?")) {
            return false;
        }

        int nLen = value.length();
        if (nLen != 15 && nLen != 18) return false;

        String date = null;
        if (nLen == 15) {
            date = "19" + value.substring(6, 12);
        } else {
            date = value.substring(6, 14);
        }
        return checkDate8(date);
    }

    /** 获取4/6位短信验证码 */
    public static String getCode(Context context, String regex, String body) {
        if(TextUtils.isEmpty(body) || !body.contains(regex)) return "";

        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        Pattern pattern1 = Pattern.compile("(\\d{6})");//提取六位数字
        Matcher matcher1 = pattern1.matcher(body);//进行匹配

        Pattern pattern2 = Pattern.compile("(\\d{4})");//提取四位数字
        Matcher matcher2 = pattern2.matcher(body);//进行匹配

        if (matcher1.find()) {//匹配成功
            String code = matcher1.group(0);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", code);
            if(cm == null) return code;
            TOAST.showShort(context, "嘎趣跑跑：验证码已复制");
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return code;
        } else if (matcher2.find()) {
            String code = matcher2.group(0);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", code);
            if(cm == null) return code;
            TOAST.showShort(context, "嘎趣跑跑：验证码已复制");
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return code;
        }
        return "";
    }


    public static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 十六进制转换字符串
     * @param hexStr
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static byte[] str2Byte(String hexStr) {
        int b = hexStr.length() % 2;
        if (b != 0) {
            hexStr = "0" + hexStr;
        }
        String[] a = new String[hexStr.length() / 2];
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            a[i] = hexStr.substring(2 * i, 2 * i + 2);
        }
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(a[i], 16);
        }
        return bytes;
    }


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


    public static final int PRICE_FORMAT_DEFAULT = 0;
    public static final int PRICE_FORMAT_PREFIX = 1;
    public static final int PRICE_FORMAT_SUFFIX = 2;
    public static final int PRICE_FORMAT_PREFIX_WITH_BLANK = 3;
    public static final int PRICE_FORMAT_SUFFIX_WITH_BLANK = 4;
    public static final String[] PRICE_FORMATS = {
            "", "￥", "元", "￥ ", " 元"
    };

    /**获取价格，保留两位小数
     * @param price
     * @param formatType 添加单位（元）
     * @return
     */
    public static String getPrice(double price, int formatType) {
        String s = new DecimalFormat("#########0.00").format(price);
        switch (formatType) {
            case PRICE_FORMAT_PREFIX:
                return PRICE_FORMATS[PRICE_FORMAT_PREFIX] + s;
            case PRICE_FORMAT_SUFFIX:
                return s + PRICE_FORMATS[PRICE_FORMAT_SUFFIX];
            case PRICE_FORMAT_PREFIX_WITH_BLANK:
                return PRICE_FORMATS[PRICE_FORMAT_PREFIX_WITH_BLANK] + s;
            case PRICE_FORMAT_SUFFIX_WITH_BLANK:
                return s + PRICE_FORMATS[PRICE_FORMAT_SUFFIX_WITH_BLANK];
            default:
                return s;
        }
    }

}
