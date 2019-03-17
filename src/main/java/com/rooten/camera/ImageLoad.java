package com.rooten.camera;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


class ImageLoad {
    public static Bitmap loadFromUri(Context context, String uri, int maxW, int maxH) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BufferedInputStream stream = null;
        if (uri.startsWith(ContentResolver.SCHEME_CONTENT) ||
                uri.startsWith(ContentResolver.SCHEME_FILE)) {
            stream = new BufferedInputStream(
                    context.getContentResolver().openInputStream(Uri.parse(uri)),
                    16384);
        }
        if (stream != null) {
            options.inSampleSize = computeSampleSize(stream, maxW, maxH);
            stream = null;
            stream = new BufferedInputStream(context.getContentResolver()
                    .openInputStream(Uri.parse(uri)), 16384);
        } else {
            return null;
        }
        options.inDither = false;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        bitmap = BitmapFactory.decodeStream(stream, null, options);
        if (bitmap != null) bitmap = resizeBitmap(bitmap, maxW, maxH);
        return bitmap;
    }

    private static int computeSampleSize(InputStream stream, int maxW, int maxH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        double w = options.outWidth;
        double h = options.outHeight;
        int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
        return sampleSize;
    }

    public static Bitmap resizeBitmap(Bitmap input, int destWidth, int destHeight) {
        int srcWidth = input.getWidth();
        int srcHeight = input.getHeight();
        boolean needsResize = false;
        float p;
        if (srcWidth > destWidth || srcHeight > destHeight) {
            needsResize = true;
            if (srcWidth > srcHeight && srcWidth > destWidth) {
                p = (float) destWidth / (float) srcWidth;
                destHeight = (int) (srcHeight * p);
            } else {
                p = (float) destHeight / (float) srcHeight;
                destWidth = (int) (srcWidth * p);
            }
        } else {
            destWidth = srcWidth;
            destHeight = srcHeight;
        }
        if (needsResize) {
            Bitmap output = Bitmap.createScaledBitmap(input, destWidth,
                    destHeight, true);
            return output;
        } else {
            return input;
        }
    }
}
