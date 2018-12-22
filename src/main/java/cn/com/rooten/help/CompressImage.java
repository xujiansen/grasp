package cn.com.rooten.help;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

//import cn.com.rooten.frame.CameraPage;
import cn.com.rooten.util.Utilities;

public class CompressImage {
    // 常用图片大小
    public final static String AVATAR_IMAGE_SIZE    = "100x100";
    public final static String PROTRAIT_IMAGE_SIZE  = "360x480";
    public final static String DEFAULT_IMAGE_SIZE   = "640x480";
    public final static String LARGER_IMAGE_SIZE    = "1920x1080";

    // 图片大小的临界值
    public final static int IMAGE_SIZE_20   = 2 * 1024;      // 20k
    public final static int IMAGE_SIZE_100  = 100 * 1024;    // 100k
    public final static int IMAGE_SIZE_200  = 200 * 1024;    // 200k
    public final static int IMAGE_SIZE_400  = 400 * 1024;    // 400k


    public static String compress(String picSize, int picWidth, String filepath, String desFilePath, String ext) {
        int fileQuality = 90;

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            String newFileName = UUID.randomUUID().toString() + ext;

            File srcFile = new File(filepath);
            File desFile = new File(desFilePath, newFileName);

            String upFileName = srcFile.getName().toUpperCase();
            if (upFileName.endsWith(".PNG") || isOverCritical(picSize, srcFile)) // 如果图片超出临界，或者是png图片
            {
                // 读图片
                in = new FileInputStream(srcFile);
                FileDescriptor fd = in.getFD();

                // 写入流
                out = new FileOutputStream(desFile);

                // 比例压缩
                Bitmap temp = BitmapFactory.decodeFileDescriptor(fd);
                int max = Math.max(temp.getWidth(), temp.getHeight());
                if (max > picWidth) {
                    float ratio = ((float) picWidth / (float) max);
                    Bitmap newBitmap = Utilities.scaleBitmap(temp, ratio);
                    Utilities.recycle(temp);

                    // png转jpg,输出到输出流中
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, fileQuality, out);
                    Utilities.recycle(newBitmap);
                } else {
                    // png转jpg,输出到输出流中
                    temp.compress(Bitmap.CompressFormat.JPEG, fileQuality, out);
                    Utilities.recycle(temp);
                }

                out.flush(); // 刷新缓冲到文件
            } else {
                // 直接复制
                if (!Utilities.copyFile(srcFile.toString(), desFile.toString())) {
                    return "";
                }
            }
            return desFile.toString();
        } catch (Exception e) {
        } finally {
            Utilities.closeInputStream(in);
            Utilities.closeOutputStream(out);
        }
        return "";
    }

    /**
     *
     * @param picSize 参考值
     * @param file      源文件
     * @return
     */
    private static boolean isOverCritical(String picSize, File file) {
        if (file == null) return false;

        long len = file.length();
        if (picSize.equals(AVATAR_IMAGE_SIZE))       // 100x100
        {
            return len > IMAGE_SIZE_20;
        }
        else if (picSize.equals(PROTRAIT_IMAGE_SIZE))    // 360x480
        {
            return len > IMAGE_SIZE_100;
        }
        else if (picSize.equals(DEFAULT_IMAGE_SIZE))    // 640x480
        {
            return len > IMAGE_SIZE_200;
        }
        else if (picSize.equals(LARGER_IMAGE_SIZE))    // 1920x1080
        {
            return len > IMAGE_SIZE_400;
        }
        return false;
    }
}
