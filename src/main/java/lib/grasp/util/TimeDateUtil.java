package lib.grasp.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cn.com.rooten.util.Utilities;

/**
 * 时间格式化
 */
public class TimeDateUtil {

    /** 秒转时分秒 */
    public static String second2Time(int time){
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + "分" + unitFormat(second)+ "秒";
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99时59分59秒";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + "时" + unitFormat(minute) + "分" + unitFormat(second)+ "秒";
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /** 获取两个时间的距离 */
    public static long getTimeDistance(String time1, String time2){
        if(TextUtils.isEmpty(time1) || TextUtils.isEmpty(time2)) return -1;
        Date date1 = Utilities.parseTimeEs(time1);
        Date date2 = Utilities.parseTimeEs(time2);
        long long1 = date1.getTime();
        long long2 = date2.getTime();
        if(long2 < long1) return -1;
        return (long2 - long1);
    }

    /** 获取以arg为月数距离的年份 */
    public static int getYearWithDistance(int distance){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, distance);
        int yeartarget = now.get(Calendar.YEAR);
        return yeartarget;
    }

    /** 获取以arg为月数距离的月份 */
    public static int getMothWithDistance(int distance){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, distance);
        int monthtarget = now.get(Calendar.MONTH) + 1;
        return monthtarget;
    }

    public static Date getDateTime(final Date d, final Date t) {
        return new Date(d.getYear(), d.getMonth(), d.getDate(),
                t.getHours(), t.getMinutes(), t.getSeconds());
    }

    public static String getDateTime(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(dateTime);
    }

    public static String getDateTimeEx(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(dateTime);
    }

    public static String getDateTimeEs(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return df.format(dateTime);
    }

    public static String getDateTimeEss(final Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(date);
    }

    public static String getDateMonthEss(final Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return df.format(date);
    }

    public static String getMonthDate(final Date date) {
        DateFormat df = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return df.format(date);
    }

    public static String getMonthTime(final Date date) {
        DateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return df.format(date);
    }

    public static String getTimeEs(final Date date) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return df.format(date);
    }

    public static String getTime(final Date date) {
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return df.format(date);
    }

    public static Date parseTimeEss(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date parseTimeMillis(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date parseTimeEs(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date parseTimeEsWithT(final String time) {
        try {
            String time1 = time.replaceAll("T" , " ");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return df.parse(time1);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date parseTimeEs_Camera(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date parseTimeEm(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date parseDateEs(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy年M月d日", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static String getDateTimeMillis(final Date date) {
        if (date == null) return "";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        return df.format(date);
    }

    public static String getDateTimeMillisEs(final Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return df.format(date);
    }

    public static String getDateTimeMillisEs2(final Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return df.format(date);
    }

    public static String getDateTime1(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(dateTime);
    }

    public static String getDateTimeCN(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyy年M月d日H时m分");
        return df.format(dateTime);
    }

    public static String getDateTimeEsCN(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyy年M月d日H时m分s秒");
        return df.format(dateTime);
    }

    public static String getDateCN(final Date date) {
        if (date == null) return "";

        DateFormat df = new SimpleDateFormat("yyyy年M月d日");
        return df.format(date);
    }

    /**
     * 取本地时间
     */
    public static String getLocationTime(long milliseconds) {
        Date time = new Date(milliseconds);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(time);
    }

    /**
     * 取UTC时间
     */
    public static String getUTCTime(long milliseconds) {
        Date time = new Date(milliseconds);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(time);
    }
}
