package lib.grasp.http.okhttp;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.rooten.BaApp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by GaQu_Dev on 2019/8/21.
 */
public class ParamInterceptor implements Interceptor {

    private Activity mActivity;
    private BaApp   mApp;

    public ParamInterceptor(Activity activity) {
        mActivity = activity;
        mApp = (BaApp) activity.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder requestBuilder = oldRequest.newBuilder();
//        requestBuilder.addHeader("Content-Type", "application/json");
        if (mApp != null && mApp.getUserData() != null) {
            String token = mApp.getUserData().token;
            if(!TextUtils.isEmpty(token)) requestBuilder.addHeader("token", mApp.getUserData().token);
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}