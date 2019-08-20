package lib.grasp.util.glide;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * (配置时请将本类拷入主模块, 并解注释@GlideModule)
 * 用了这个注解之后, 就不必在清单文件中添加
 */
//@GlideModule
public class GraspAppGlideModule extends AppGlideModule {

    private int diskSize = 1024 * 1024 * 100;
    private int memorySize = (int) (Runtime.getRuntime().maxMemory()) / 8;  // 取1/8最大内存作为最大缓存

    public GraspAppGlideModule() {
        super();
        System.out.println("------------------------------GraspAppGlideModule-----------------");
    }

    /** 是否禁用Manifest清单解析。这样可以改善 Glide 的初始启动时间 */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        System.out.println("----------------------------------applyOptions-------------");
        // 定义缓存大小和位置
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskSize));  // 内存中
//         builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "cache123", diskSize)); // sd卡中
        String downloadDirectoryPath = Environment.getExternalStorageDirectory().getPath() + "/test123123";
        builder.setDiskCache(new DiskLruCacheFactory( downloadDirectoryPath, diskSize )); // sd卡中

        // 默认内存和图片池大小
//         MemorySizeCalculator calculator = new MemorySizeCalculator(context);
//         int defaultMemoryCacheSize = calculator.getMemoryCacheSize(); // 默认内存大小
//         int defaultBitmapPoolSize = calculator.getBitmapPoolSize(); // 默认图片池大小
//         builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize));
//         builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));

        // 自定义内存和图片池大小
        builder.setMemoryCache(new LruResourceCache(memorySize)); // 自定义内存大小
        builder.setBitmapPool(new LruBitmapPool(memorySize)); // 自定义图片池大小

        // 定义图片格式
//         builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565)); // 默认
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }
}
