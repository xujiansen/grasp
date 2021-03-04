package lib.grasp.http.okhttp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rooten.Constant;
import com.rooten.help.LocalBroadHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import lib.grasp.util.L;
import lib.grasp.widget.MessageBoxGrasp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by JS_grasp on 2019/6/30.
 */
public class OkHttpCallback<T> implements Callback {

    /**
     * code是根据接口返回的标识 实际根据自己接口返回为准
     */
    protected final String RESULT_CODE = "code";
    /**
     * 正常访问
     */
    protected final int RESULT_CODE_VALUE = 0;

    /**
     * errorMsg字段提示信息，实际根据自己接口返回为准
     */
    protected final String ERROR_MSG    = "msg";
    protected final String NETWORK_MSG  = "请求失败";
    protected final String JSON_MSG     = "解析失败";

    /**
     * 自定义异常类型
     */
    protected final int NETWORK_ERROR = -1; //网络失败
    protected final int JSON_ERROR = -2; //解析失败
    protected final int OTHER_ERROR = -3; //未知错误
    protected final int TIMEOUT_ERROR = -4; //请求超时
    protected final int SESSION_INVALIDATE = 401; //会话已过期
    protected final int VERSION_INVALIDATE_402 = 402; //版本已过期
    protected final int VERSION_INVALIDATE_403 = 403; //版本已过期

    private Handler mDeliveryHandler; //进行消息的转发
    private ResponseCallback<T> mListener;
    private Activity mActivity;

    public OkHttpCallback(Activity ctx) {
        this.mActivity = ctx;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    public void setListener(ResponseCallback<T> listener){
        this.mListener = listener;
    }


    /**
     * 请求失败的处理
     */
    @Override
    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
        L.log("请求失败=" + e.getMessage());
        if(mListener == null) return;
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                if (e instanceof SocketTimeoutException) {
                    //判断超时异常
                    mListener.onFailure(new OkHttpException(TIMEOUT_ERROR, "请求超时"));
                } else if (e instanceof ConnectException) {
                    //判断超时异常
                    mListener.onFailure(new OkHttpException(OTHER_ERROR, "请求服务器失败"));
                } else {
                    mListener.onFailure(new OkHttpException(NETWORK_ERROR, e.getMessage()));
                }
            }
        });
    }

    /**
     * 请求成功的处理 回调在主线程
     */
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if(mListener == null) return;
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    /**
     * 处理Http成功的响应
     */
    private void handleResponse(Object responseObj) {
        if (responseObj == null || responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, NETWORK_MSG));
            return;
        }

        try {
            JSONObject result = new JSONObject(responseObj.toString());
            if (result.has(RESULT_CODE)) {
                //从JSON对象中取出我们的响应码，如果为0，则是正确的响应 (实际情况按你们接口文档)
                if (result.getInt(RESULT_CODE) == RESULT_CODE_VALUE) {

                    /*
                     * 1. 优先转成ResponseCallback<T>的泛型
                     * 2. 其次转成OkHttpHelper<T>的泛型
                     *
                     * 判断是否需要解析成实体类还是json字符串
                     * class com.google.gson.internal.$Gson$Types$ParameterizedTypeImpl
                     */
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    T obj = null;
                    if (!mListener.mType.getClass().equals("java.lang.Class")) {
                        obj = gson.fromJson((String) responseObj, mListener.mType);
                    } else {
                        obj = (T) responseObj;
                    }

                    if (obj != null) {
                        mListener.onSuccess(obj);
                    } else {
                        mListener.onFailure(new OkHttpException(JSON_ERROR, JSON_MSG));
                    }
                }
                else if(result.getInt(RESULT_CODE) == SESSION_INVALIDATE){
                    MessageBoxGrasp.infoMsg(mActivity, "提示", "会话已过期,请重新登录!", false, v -> {
                        LocalBroadHelper.getDefault().broadAction(Constant.ARG_TOKEN_EXPIRE);
                    });
                }
                else if (result.getInt(RESULT_CODE) == VERSION_INVALIDATE_402 || result.getInt(RESULT_CODE) == VERSION_INVALIDATE_402){
                    String verStr = result.getString("msg");
                    if (!TextUtils.isEmpty(verStr)) {
                        Intent intent = new Intent();
                        intent.setAction(Constant.ARG_NEW_VERSION);
                        intent.setPackage(mActivity.getPackageName());
                        intent.putExtra("data", verStr);
                        mActivity.sendBroadcast(intent);
                    }
                    MessageBoxGrasp.infoMsg(mActivity, "当前应用版本已过期, 请联系管理员");
                }
                else { //将服务端返回的异常回调到应用层去处理
                    mListener.onFailure(new OkHttpException(OTHER_ERROR, result.get(ERROR_MSG) + ""));
                    L.log("onResponse处理失败");
                }
            }
        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
            L.log("onResponse处理失败" + e.getMessage());
        }
    }
}
