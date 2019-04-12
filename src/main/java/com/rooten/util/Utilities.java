package com.rooten.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib.grasp.R;
import lib.grasp.util.L;

@Deprecated
public final class Utilities {

    public static Bitmap scaleBitmap(Bitmap src, float scale) {
        if (src == null) return null;

        int width = src.getWidth();
        int height = src.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            L.logOnly("toURLEncoded error:" + paramString + localException);
        }

        return "";
    }

    /**
     * 判断目录或文件是否存在
     */
    public static boolean fileExists(final String strFile) {
        try {
            if (isEmpty(strFile)) return false;

            final File filePath = new File(strFile);
            return filePath.exists();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 保证指定的目录存在
     */
    public static boolean ensurePathExists(final String strPath) {
        try {
            final File filePath = new File(strPath);
            boolean exists = filePath.exists();
            if (!exists) {
                return filePath.mkdirs();
            }
            return true;
        } catch (final Exception e) {
            return false;
        }
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
     * 判断字符串是否是数据组成
     */
    public static boolean isNum(final String value) {
        if (value.length() == 0) return false;
        return value.matches("[0-9]+");
    }

    /**
     * 删除文件
     */
    public static boolean delFile(final String filename) {
        if (isEmpty(filename)) return true;
        File file = new File(filename);

        if (!file.exists()) return true;
        if (!file.isFile()) return false;
        return file.delete();
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(final String srcName, final String desName) {
        if (srcName == null || desName == null) return false;

        File src = new File(srcName);
        if (!src.exists() || !src.isFile()) return false;

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(desName);

            byte[] data = new byte[8 * 1024];
            int len = fis.read(data);
            while (len != -1) {
                fos.write(data, 0, len);
                len = fis.read(data);
            }
            fis.close();
            fos.close();
            return true;
        } catch (Exception e) {
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    public static boolean setFocusView(final View view) {
        if (view != null) {
            return view.requestFocus();
        }
        return false;
    }

    public static String getStringAt(final ArrayList<String> arr, int index) {
        if (arr == null || index < 0 || index >= arr.size()) {
            return "";
        }
        return arr.get(index);
    }

    public static String getDateTimeDay(final Date dateTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(dateTime);
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

    public static Date parseTimeEs(final String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return df.parse(time);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static String getDateTimeMillisEs(final Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        return df.format(date);
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String text) {
        return (text == null || text.length() == 0);
    }

    /**
     * 关闭输入流
     */
    public static void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 关闭输入流
     */
    public static void closeBufferReader(BufferedReader is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 关闭输入流
     */
    public static void closeRandomAccessStream(RandomAccessFile is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 关闭输出流
     */
    public static void closeOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
        }
    }

    public static int getValueByDpi(Context ctx, int value) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    public static void shakeView(Context context, final View view) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_x));
    }

    public static String getExtName(File f) {
        if (f == null || !f.exists()) {
            return "";
        }

        String filename = f.getName();
        try {
            return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        } catch (Exception e) {
        }
        return "";
    }

    public static String getExtNameByPath(String filepath) {
        File file = new File(filepath);
        return getExtName(file);
    }

    public static String getExtName(String filename) {
        if (Utilities.isEmpty(filename)) {
            return "";
        }

        try {
            return filename.substring(filename.lastIndexOf(".") + 1, filename.length());
        } catch (Exception e) {
        }
        return "";
    }

    public static void recycle(Bitmap bmp) {
        try {
            bmp.recycle();
            bmp = null;
        } catch (Exception e) {
            L.logOnly("recycle" + e.toString());
        }
    }

    public static String getFileSize(Context cxt, long size) {
        return Formatter.formatFileSize(cxt, size); // 自动递进--B/KB/MB
    }

    public static String getFileSize(Context cxt, String path) {
        try {
            File file = new File(path);
            return getFileSize(cxt, file.length());
        } catch (Exception e) {
            return "";
        }
    }

    public static long getFileSize(String path) {
        try {
            File file = new File(path);
            return file.length();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String ToMD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileName(String text) {
        try {
            File file = new File(text);
            return file.getName();
        } catch (Exception e) {
            return "位置文件名";
        }
    }

    public static int getStatusBarHeight(Context cxt) {
        Resources resources = cxt.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId <= 0) return 0;
        return resources.getDimensionPixelSize(resourceId);
    }

    public static boolean contains(Context context, String name) {
        if (Utilities.isEmpty(name)) return false;

        SharedPreferences sharedPreferences = context.getSharedPreferences("info.txt", Context.MODE_MULTI_PROCESS);
        return sharedPreferences.contains(name);
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

    public static boolean isNetConnected(Context cxt) {
        ConnectivityManager connectivityManager = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNet = connectivityManager.getActiveNetworkInfo();
        return activeNet != null && activeNet.isConnected();
    }

    public static void closeCursor(Cursor cursor) {
        try {
            if (cursor == null) return;
            cursor.close();
        } catch (Exception e) {
        }
    }

    public static Bitmap shotFirstFrame(Context context, String videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            return retriever.getFrameAtTime(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isFileExists(final String strFile) {
        try {
            final File filePath = new File(strFile);
            return filePath.exists() && filePath.isFile();
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * 文件转base64字符串
     * @param file
     * @return
     */
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return base64;
    }
}
