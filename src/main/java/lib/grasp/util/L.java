package lib.grasp.util;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import lib.grasp.BuildConfig;

/**
 * 带日志文件输入的，又可控开关的日志调试
 * <br/>
 * todo 上传功能待实现
 */
public class L {

    /**
     * 打印
     */
    public static void log(Object msg) {
        Logger.d(msg);
    }

    public static void init(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(1)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("GRASP")           // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }
}