package lib.grasp.util.glide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.security.MessageDigest;

/**
 * Glide 工具类
 * <p>Google推荐的图片加载库，专注于流畅的滚动
 * <p>Glide 比Picasso  加载快 但需要更大的内存来缓存
 * <p>Glide 不光接受Context，还接受Activity 和 Fragment ,
 * <p>图片加载会和Activity/Fragment的生命周期保持一致 在onPause（）暂停加载，onResume（）恢复加载
 * <p>支持GIF格式图片加载
 *
 *
 *
 *
 *
 * <br/>仅从缓存加载图片.onlyRetrieveFromCache(true)
 * <br/>跳过内存缓存.skipMemoryCache(true)
 * <br/>跳过磁盘缓存.diskCacheStrategy(DiskCacheStrategy.NONE)
 * <a href="https://www.jianshu.com/p/2738b43017a1">参考</a>
 */
public class GlideUtils {
    private static GlideUtils instance;

    public static GlideUtils getInstance() {
        if (instance == null) {
            synchronized (GlideUtils.class) {
                if (instance == null) {
                    instance = new GlideUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 加载bitmap，如果是GIF则显示第一帧
     */
    public static String LOAD_BITMAP = "GLIDEUTILS_GLIDE_LOAD_BITMAP";

    /**
     * 加载gif动画
     */
    public static String LOAD_GIF = "GLIDEUTILS_GLIDE_LOAD_GIF";

    //-----[方形]-----------------------------------

    /**
     * 加载[方形]图片
     * <br/> 网络/sd卡
     */
    public void LoadContextBitmap(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(path)
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 加载[方形]图片
     * <br/> drawableID
     */
    public void LoadContextBitmap(Context context, int drawableID, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(drawableID)
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    //-----[动态图]-----------------------------------

    /**
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * 使用activity 会受到Activity生命周期控制
     * 使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param placeid     占位: 占位图就是指在图片的加载过程中，我们先显示一张临时的图片，等图片加载出来了再替换成要加载的图片
     * @param errorid     错误
     * @param bitmapOrgif 加载普通图片 或者GIF图片 ，GIF图片设置bitmap显示第一帧
     */
    public void LoadContextBitmap(Context context, String path, ImageView imageView, int placeid, int errorid, String bitmapOrgif) {
        if (bitmapOrgif == null || bitmapOrgif.equals(LOAD_BITMAP)) {
            Glide.with(context)
                    .asBitmap()
                    .load(path)
                    .placeholder(placeid)
                    .error(errorid)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(imageView);
        } else if (bitmapOrgif.equals(LOAD_GIF)) {
            Glide.with(context)
                    .asGif()
                    .load(path)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    /**
     * Glide请求[动态图]，会受到Fragment 生命周期控制。
     * @param bitmapOrgif 加载普通图片 或者GIF图片 ，GIF图片设置bitmap显示第一帧
     */
    public void LoadFragmentBitmap(android.app.Fragment fragment, String path, ImageView imageView, int placeid, int errorid, String bitmapOrgif) {
        if (bitmapOrgif == null || bitmapOrgif.equals(LOAD_BITMAP)) {
            Glide.with(fragment)
                    .asBitmap()
                    .load(path)
                    .placeholder(placeid)
                    .error(errorid)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(imageView);
        } else if (bitmapOrgif.equals(LOAD_GIF)) {
//            Glide.with(fragment).load(path).asGif().crossFade().into(imageView);
            Glide.with(fragment)
                    .asGif()
                    .load(path)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    /**
     * Glide请求[动态图]，会受到support.v4.app.Fragment生命周期控制。
     * @param bitmapOrgif 加载普通图片 或者GIF图片 ，GIF图片设置bitmap显示第一帧
     */
    public void LoadSupportv4FragmentBitmap(Fragment fragment, String path, ImageView imageView, int placeid, int errorid, String bitmapOrgif) {
        if (bitmapOrgif == null || bitmapOrgif.equals(LOAD_BITMAP)) {
            Glide.with(fragment)
                    .asBitmap()
                    .load(path)
                    .placeholder(placeid)
                    .error(errorid)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(imageView);
        } else if (bitmapOrgif.equals(LOAD_GIF)) {
            Glide.with(fragment)
                    .asGif()
                    .load(path)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView);
        }
    }

    //-----[圆形]-----------------------------------

    /**
     * 加载设置[圆形]图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadContextCircleBitmap(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).transform(new GlideCircleTransform()).into(imageView);
    }

    /**
     * Glide请求图片设置[圆形]，会受到android.app.Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadfragmentCircleBitmap(android.app.Fragment fragment, String path, ImageView imageView) {
        Glide.with(fragment).load(path).transform(new GlideCircleTransform()).into(imageView);
    }

    /**
     * Glide请求图片设置[圆形]，会受到android.support.v4.app.Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentCircleBitmap(Fragment fragment, String path, ImageView imageView) {
        Glide.with(fragment).load(path).transform(new GlideCircleTransform()).into(imageView);
    }

    //-----[圆角]----------------------------------------

    /**
     * 加载设置[圆角]图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param roundradius 圆角大小（>0）
     */
    @SuppressWarnings("unchecked")
    public void LoadContextRoundBitmap(Context context, String path, ImageView imageView, int roundradius) {
        if (roundradius < 0) {
            Glide.with(context).load(path).transform(new GlideRoundTransform(context)).into(imageView);
        } else {
            Glide.with(context).load(path).transform(new GlideRoundTransform(context, roundradius)).into(imageView);
        }
    }

    /**
     * Glide请求图片设置[圆角]，会受到android.app.Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadfragmentRoundBitmap(android.app.Fragment fragment, String path, ImageView imageView, int roundradius) {
        if (roundradius < 0) {
            Glide.with(fragment).load(path).transform(new GlideRoundTransform(fragment.getActivity())).into(imageView);
        } else {
            Glide.with(fragment).load(path).transform(new GlideRoundTransform(fragment.getActivity(), roundradius)).into(imageView);
        }
    }

    /**
     * Glide请求图片设置[圆角]，会受到android.support.v4.app.Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentRoundBitmap(Fragment fragment, String path, ImageView imageView, int roundradius) {
        if (roundradius < 0) {
            Glide.with(fragment).load(path).transform(new GlideRoundTransform(fragment.getActivity())).into(imageView);
        } else {
            Glide.with(fragment).load(path).transform(new GlideRoundTransform(fragment.getActivity(), roundradius)).into(imageView);
        }
    }

    //-----[模糊]--------------------------------------------

    /**
     * Glide 加载[模糊]图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadContextBlurBitmap(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).centerCrop().transform(new BlurTransformation(context)).into(imageView);
    }

    /**
     * Glide 加载[模糊]图片 会受到Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadFragmentBlurBitmap(android.app.Fragment fragment, String path, ImageView imageView) {
        Glide.with(fragment).load(path).transform(new BlurTransformation(fragment.getActivity())).into(imageView);
    }

    /**
     * Glide 加载[模糊]图片 会受到support.v4.app.Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentBlurBitmap(Fragment fragment, String path, ImageView imageView) {
        Glide.with(fragment).load(path).transform(new BlurTransformation(fragment.getActivity())).into(imageView);
    }

    //-----[旋转]----------------------------------------------------

    /**
     * [旋转]图片
     * 使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制
     * <BR/>使用activity 会受到Activity生命周期控制
     * <BR/>使用FragmentActivity 会受到FragmentActivity生命周期控制
     * @param rotateRotationAngle 旋转角度
     */
    @SuppressWarnings("unchecked")
    public void LoadContextRotateBitmap(Context context, String path, ImageView imageView, Float rotateRotationAngle) {
        Glide.with(context).load(path).transform(new RotateTransformation(context, rotateRotationAngle)).into(imageView);
    }

    /**
     * Glide 加载[旋转]图片 会受到Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadFragmentRotateBitmap(android.app.Fragment fragment, String path, ImageView imageView, Float rotateRotationAngle) {
        Glide.with(fragment).load(path).transform(new RotateTransformation(fragment.getActivity(), rotateRotationAngle)).into(imageView);
    }

    /**
     * Glide 加载[旋转]图片 会受到support.v4.app.Fragment生命周期控制
     */
    @SuppressWarnings("unchecked")
    public void LoadSupportv4FragmentRotateBitmap(Fragment fragment, String path, ImageView imageView, Float rotateRotationAngle) {
        Glide.with(fragment).load(path).transform(new RotateTransformation(fragment.getActivity(), rotateRotationAngle)).into(imageView);
    }


    //----------------------旋转---------------------------

    /**
     * 旋转
     */
    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);
            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

    /**
     * 图片转圆形
     */
    public class GlideCircleTransform extends BitmapTransformation {

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_4444);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

    //-----------------------------图片模糊----------------------------------

    /**
     * 图片模糊
     */
    public class BlurTransformation extends BitmapTransformation {

        private RenderScript rs;

        public BlurTransformation(Context context) {
            rs = RenderScript.create(context);
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.ARGB_8888, true);

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(
                    rs,
                    blurredBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SHARED
            );
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(10);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            toTransform.recycle();

            return blurredBitmap;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }

    //-------------------图片转换圆角图片------------------------------

    /**
     * 图片转换圆角图片
     */
    public class GlideRoundTransform extends BitmapTransformation {

        private float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        /**
         * 自定义圆角大小
         */
        public GlideRoundTransform(Context context, int dp) {
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }
}