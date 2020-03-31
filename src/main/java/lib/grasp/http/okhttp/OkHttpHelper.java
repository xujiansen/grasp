package lib.grasp.http.okhttp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rooten.BaApp;
import com.rooten.ctrl.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lib.grasp.util.L;
import lib.grasp.util.StringUtil;
import lib.grasp.widget.ProgressDlgGrasp;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

/*
    OkHttpHelper.with(this)
        .setURL("http://192.168.11.21:13000/test/http")
        .setMethod(OkHttpHelper.POST_FORM)
        .addHeadParam("head1", "head11")
        .addHeadParam("head2", "head22")
        .addParam("form1", "form11")
        .addParam("form2", "form22")
        .setIsShowProg(true, "测试")
        .setRequestCode("123")
        .setSwip(null)
        .setTimeout(30)
        .execute(new ResponseCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TOAST.showShort("String:" + s);
            }
            @Override
            public void onFailure(Exception e) {
                TOAST.showShort("onFailure:" + e);
            }
        });
 */

/**
 * OkHttp网络访问请求
 */
public class OkHttpHelper {
    protected BaApp mApp;
    protected Activity mActivity;

    private ProgressDlgGrasp mProgressDlg;
    private SwipeRefreshLayout mSwip;

    /** 普通get */
    public static final int GET       = 1;
    /** 表单post */
    public static final int POST_FORM = 2;
    /** JSON_post */
    public static final int POST_JSON = 3;


    /**
     * 是否取消
     */
    private boolean mIsCancel = false;
    /**
     * 是否显示prog
     */
    private boolean mIsShowProg = false;
    /**
     * 超时秒
     */
    private int mTimeout = 10;

    /**
     * 本次请求的请求ID
     */
    private String mRequestCode = UUID.randomUUID().toString();
    /**
     * 本次请求的URL
     */
    private String mURL = "";
    /**
     * 本次请求的请求(默认POST)
     */
    private int mMethod = POST_FORM;

    /**
     * POST-body参数
     */
    private Map<String, String> mHeadParam = new HashMap<>();
    /**
     * 头参数
     */
    private Map<String, String> mParam = new HashMap<>();

    /**
     * 进度条提示信息
     */
    private String mInfoStr;

    /**
     * 监听器
     */
    private OkHttpCallback mCallbackInner;

    private static OkHttpClient mOkHttpClient;

    public static OkHttpHelper with(Activity activity) {
        BaApp mApp = (BaApp) activity.getApplicationContext();
        return new OkHttpHelper(mApp, activity);
    }

    private OkHttpHelper(BaApp app, Activity activity) {
        this(app, activity, false);
    }

    private OkHttpHelper(BaApp app, Activity activity, boolean isShowProg) {
        this.mApp = app;
        this.mActivity = activity;
        this.mIsShowProg = isShowProg;
        this.mCallbackInner = new OkHttpCallback<>(activity);
    }

    private static void initOkHttpClient(Activity activity, int mTimeout) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new LogInterceptor()).setLevel(HttpLoggingInterceptor.Level.BODY);

        synchronized (OkHttpHelper.class){
            mOkHttpClient = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(mTimeout,   TimeUnit.SECONDS)
                    .writeTimeout(  mTimeout,   TimeUnit.SECONDS)
                    .readTimeout(   mTimeout,   TimeUnit.SECONDS)
                    .addInterceptor(new ParamInterceptor(activity))
                    .addInterceptor(interceptor)
//                    .addNetworkInterceptor(new HttpLoggingInterceptor(new LogInterceptor()))
                    .retryOnConnectionFailure(false)
                    .build();
        }
    }

    private void initProgressDlg() {
        mProgressDlg = new ProgressDlgGrasp(mActivity);
        mProgressDlg.setCanBeCancel(true);
        mProgressDlg.setCancelListener(v -> {
            mIsCancel = true;
            dismissView();
        });
    }

    public void execute(ResponseCallback callback) {
        this.mCallbackInner.setListener(callback);
        if (mIsShowProg) {
            initProgressDlg();
            mProgressDlg.show();
            mProgressDlg.setMessage(TextUtils.isEmpty(mInfoStr) ? "正在操作" : mInfoStr);
        }

        mIsCancel = false;

        if (mMethod == GET) mURL = encodeParameters(mURL, mParam);
        if (mOkHttpClient == null) initOkHttpClient(mActivity, mTimeout);

        Request request = null;

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = new Headers.Builder();
        // 装载请求头参数
        for(Map.Entry<String,String> entry : mHeadParam.entrySet()){
            headerBuilder.add(entry.getKey(), entry.getValue());
        }

        switch (mMethod){
            case GET: {
                request = new Request.Builder()
                        .url(mURL)
                        .headers(headerBuilder.build())
                        .tag(mRequestCode)
                        .get()
                        .build();
                break;
            }
            case POST_FORM: {
                FormBody.Builder builder = new FormBody.Builder();
                //添加参数
                for(Map.Entry<String,String> entry : mParam.entrySet()){
                    builder.addEncoded(entry.getKey(), entry.getValue());
                }
                FormBody formBody = builder.build();

                request = new Request.Builder()
                        .url(mURL)
                        .headers(headerBuilder.build())
                        .tag(mRequestCode)
                        .post(formBody)
                        .build();
                break;
            }
            case POST_JSON: {
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                String param= new Gson().toJson(mParam);
                RequestBody requestBody = RequestBody.create(mediaType, param);

                request = new Request.Builder()
                        .url(mURL)
                        .headers(headerBuilder.build())
                        .tag(mRequestCode)
                        .post(requestBody)
                        .build();
                break;
            }
        }

//        mOkHttpClient.newCall(request).enqueue(new OkHttpCallback(mContext, mCallback));
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.log("OkHttp::" + e.getMessage());
                dismissView();
                if (mIsCancel) mIsCancel = false;
                mCallbackInner.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response == null) return;

                L.log("OkHttp::" + response.toString());
                dismissView();
                if (mIsCancel) mIsCancel = false;
                mCallbackInner.onResponse(call, response);
            }
        });

        L.log("请求URL:: " + mURL);
    }

    /** 清除所有的加载界面的显示 */
    private void dismissView() {
        if (mSwip != null) {
            mSwip.setRefreshing(false);
            mSwip.setLoading(false);
        }

        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

    /**
     * 设置Swip
     */
    public OkHttpHelper setSwip(SwipeRefreshLayout mSwip) {
        this.mSwip = mSwip;
        return this;
    }

    /**
     * 设置是否显示加载提示框, 通知栏显示文字
     */
    public OkHttpHelper setIsShowProg(boolean mIsShowProg, String msg) {
        this.mIsShowProg    = mIsShowProg;
        this.mInfoStr       = msg;
        return this;
    }

    /**
     * 本次请求的URL
     */
    public OkHttpHelper setURL(String mURL) {
        this.mURL = mURL;
        return this;
    }

    /**
     * 本次请求的请求(默认POST)
     */
    public OkHttpHelper setMethod(int mMethod) {
        this.mMethod = mMethod;
        return this;
    }

    /**
     * 本次请求的请求ID
     */
    public OkHttpHelper setRequestCode(String mRequestCode) {
        this.mRequestCode = mRequestCode;
        return this;
    }

    /**
     * 添加本次请求的参数
     */
    public OkHttpHelper addParam(String key, String value) {
        if(this.mParam == null) this.mParam = new HashMap<>();
        this.mParam.put(key, value);
        return this;
    }

    /**
     * 设置本次请求的参数
     */
    public OkHttpHelper setParam(Map<String, String> mParam) {
        this.mParam = mParam;
        return this;
    }

    /**
     * 添加本次请求的head参数
     */
    public OkHttpHelper addHeadParam(String key, String value) {
        if(this.mHeadParam == null) this.mHeadParam = new HashMap<>();
        this.mHeadParam.put(key, value);
        return this;
    }

    /**
     * 设置本次请求的head参数
     */
    public OkHttpHelper setHeadParam(Map<String, String> mHeadParam) {
        this.mHeadParam = mHeadParam;
        return this;
    }

    /**
     * 超时时间(秒)
     */
    public OkHttpHelper setTimeout(int mTimeout) {
        this.mTimeout = mTimeout;
        return this;
    }

    /**
     * 编辑get参数-方式1
     */
    private String encodeParameters(String url, Map<String, String> params) {
        StringBuilder encodedParams = new StringBuilder("?");
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(entry.getKey());
                encodedParams.append('=');
                encodedParams.append(StringUtil.toURLEncoded(entry.getValue()));
                encodedParams.append('&');
            }
            String newUrl = url + encodedParams.toString();
            if (newUrl.endsWith("&")) {
                int index = newUrl.lastIndexOf("&");
                newUrl = newUrl.substring(0, index);
            }

            return newUrl;
        } catch (Exception var6) {
            throw new RuntimeException("Param of GET error: ", var6);
        }
    }

    /**
     * 编辑get参数-方式2
     */
    protected String encodeParameters2(String url, Map<String, String> params) {
        if (url != null && params != null && !params.isEmpty()) {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            Set<String> keys = params.keySet();

            for (String key : keys) {
                builder.appendQueryParameter(key, params.get(key));
            }

            return builder.build().toString();
        } else {
            return url;
        }
    }



}
