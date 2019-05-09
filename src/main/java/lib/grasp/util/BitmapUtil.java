package lib.grasp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;

/**
 * Created by GaQu_Dev on 2019/5/8.
 */
public class BitmapUtil {
    public static Bitmap scaleBitmap(Bitmap src, float scale) {
        if (src == null) return null;

        int width = src.getWidth();
        int height = src.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
    }

    public static void recycle(Bitmap bmp) {
        try {
            bmp.recycle();
            bmp = null;
        } catch (Exception e) {
            L.logOnly("recycle" + e.toString());
        }
    }


    /** 显示视频第一帧 */
    public static Bitmap shotFirstFrame(Context context, String videoPath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoPath);
            return retriever.getFrameAtTime(0);
        } catch (Exception e) {
            return null;
        }
    }
}
