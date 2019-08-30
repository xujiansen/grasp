package lib.grasp.http.retrofit;

import android.content.Context;

import com.rooten.BaApp;
import com.rooten.Constant;

import java.util.concurrent.TimeUnit;

import lib.grasp.http.okhttp.LogInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhouwei on 16/11/9.
 */

public class RetrofitServiceManager {

    private static final int DEFAULT_TIME_OUT = 5;          //超时时间 5s
    private static final int DEFAULT_READ_TIME_OUT = 10;    // 读超时
    private Retrofit mRetrofit;

    private RetrofitServiceManager() {
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        builder.writeTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);//读操作 超时时间

        // 添加日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new LogInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY);

        // 添加公共参数拦截器
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder()
                .addHeaderParams("platform", "android")
//                .addHeaderParams("token", BaApp.getApp().getUserData() != null ? BaApp.getApp().getUserData().token : "")
                .build();
        builder.addInterceptor(interceptor);
        builder.addInterceptor(commonInterceptor);

        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.BASE_URL)
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    /**
     * 获取RetrofitServiceManager
     */
    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}
