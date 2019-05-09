package lib.grasp.http.volley;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.minidev.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.ctrl.widget.SwipeRefreshLayout;
import com.rooten.help.LocalBroadMgr;

import lib.grasp.util.StringUtil;
import lib.grasp.http.BaseResponse;
import lib.grasp.http.volley.gsonrequest.JsonObjRequest;
import lib.grasp.http.volley.gsonrequest.ParamRequest;
import lib.grasp.util.L;
import lib.grasp.util.TOAST;
import lib.grasp.widget.MessageBoxGrasp;
import lib.grasp.widget.ProgressDlgGrasp;

/**
 * Volley网络访问请求
 */
public class VolleyHelper<T> {
    protected BaApp mApp;
    protected Context mContext;

    private ProgressDlgGrasp mProgressDlg;
    private SwipeRefreshLayout mSwip;


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
    private int mMethod = Request.Method.POST;

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
     * 成功监听器
     */
    private Response.Listener<T> mSuccessListener;
    /**
     * 失败监听器
     */
    private Response.ErrorListener mErrorListener;

    public static VolleyHelper with(Context context) {
        BaApp mApp = (BaApp) context.getApplicationContext();
        return new VolleyHelper(mApp, context);
    }

    public VolleyHelper(BaApp app, Context context) {
        this(app, context, false);
    }

    public VolleyHelper(BaApp app, Context context, boolean isShowProg) {
        this.mApp = app;
        this.mContext = context;
        this.mIsShowProg = isShowProg;
    }

    private void initProgressDlg() {
        mProgressDlg = new ProgressDlgGrasp(mContext);
        mProgressDlg.setCanBeCancel(true);
        mProgressDlg.setCancelListener(v -> {
            mIsCancel = true;
            dismissView();
        });
    }

    public void execute(Type type) {
        if (mIsShowProg) {
            initProgressDlg();
            mProgressDlg.show();
            mProgressDlg.setMessage(TextUtils.isEmpty(mInfoStr) ? "正在操作" : mInfoStr);
        }

        mIsCancel = false;

        if (mMethod == Request.Method.GET) {
            mURL = encodeParameters(mURL, mParam);
        }

        Response.Listener<T> listener = orderResponse -> {  // 成功回调
            dismissView();
            if (mIsCancel) mIsCancel = false;
            if (orderResponse == null)      return;
            if (!filterCode(orderResponse)) return; // 在网络层通信成功的基础上, 判断业务层是否成功(捕捉业务失败)
            if (mSuccessListener != null) mSuccessListener.onResponse(orderResponse);
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {   // 失败回调
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                 L.logOnly("请求异常::" + volleyError.toString());

                dismissView();
                if (mIsCancel) mIsCancel = false;
                if (volleyError.networkResponse == null) {
                    TOAST.showShort(mContext, "网络异常\n" + volleyError.toString());
                    return;
                }

                if (mErrorListener != null) mErrorListener.onErrorResponse(volleyError);
            }
        };

        Request mRequest;
        if("application/json".equalsIgnoreCase(mHeadParam.get("Content-Type"))) {   // POST - Json
            mRequest = new JsonObjRequest<T>(
                    mMethod,
                    mURL,
                    JSONObject.toJSONString(mParam),    // 将请求参数添加到body
                    type,
                    listener,
                    errorListener
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    if (mHeadParam == null) mHeadParam = new HashMap<>();
                    return mHeadParam;
                }
            };
        }
        else mRequest = new ParamRequest<T>(                                        // POST - Param
                    mMethod,
                    mURL,
                    type,
                    listener,
                    errorListener) {

                @Override
                protected Map<String, String> getParams() {
                    if (mParam == null) mParam = new HashMap<>();
                    return mParam;
                }

                @Override
                public Map<String, String> getHeaders() {
                    if (mHeadParam == null) mHeadParam = new HashMap<>();
                    return mHeadParam;
                }
            };
        mRequest.setRetryPolicy(new DefaultRetryPolicy(mTimeout * 1000, 0, 1.0f));
        mApp.getRequestQueue().add(mRequest).setTag(mRequestCode);
        L.logOnly("请求URL:: " + mURL);
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
     * 设置请求成功之后的回调
     */
    public VolleyHelper<T> onSuccess(Response.Listener listener) {
        this.mSuccessListener = listener;
        return this;
    }

    /**
     * 设置请求失败之后的回调
     */
    public VolleyHelper<T> onError(Response.ErrorListener listener) {
        this.mErrorListener = listener;
        return this;
    }

    /**
     * 设置Swip
     */
    public VolleyHelper<T> setSwip(SwipeRefreshLayout mSwip) {
        this.mSwip = mSwip;
        return this;
    }

    /**
     * 设置是否显示加载提示框, 通知栏显示文字
     */
    public VolleyHelper<T> setIsShowProg(boolean mIsShowProg, String msg) {
        this.mIsShowProg = mIsShowProg;
        this.mInfoStr = msg;
        return this;
    }

    /**
     * 本次请求的URL
     */
    public VolleyHelper<T> setURL(String mURL) {
        this.mURL = mURL;
        return this;
    }

    /**
     * 本次请求的请求(默认POST)
     */
    public VolleyHelper<T> setMethod(int mMethod) {
        this.mMethod = mMethod;
        return this;
    }

    /**
     * 本次请求的请求ID
     */
    public VolleyHelper<T> setRequestCode(String mRequestCode) {
        this.mRequestCode = mRequestCode;
        return this;
    }

    /**
     * 本次请求的参数
     */
    public VolleyHelper<T> setParam(Map<String, String> mParam) {
        this.mParam = mParam;
        return this;
    }

    /**
     * 本次请求的head参数
     */
    public VolleyHelper<T> setHeadParam(Map<String, String> mHeadParam) {
        this.mHeadParam = mHeadParam;
        return this;
    }

    /**
     * 超时时间(秒)
     */
    public VolleyHelper<T> setTimeout(int mTimeout) {
        this.mTimeout = mTimeout;
        return this;
    }

    /**
     * 编辑get参数-方式1
     */
    private String encodeParameters(String url, Map<String, String> params) {
        StringBuilder encodedParams = new StringBuilder("?");
        try {
            for (Object o : params.entrySet()) {
                Map.Entry<String, String> entry = (Map.Entry) o;
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
            Iterator iterator = keys.iterator();

            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                builder.appendQueryParameter(key, (String) params.get(key));
            }

            return builder.build().toString();
        } else {
            return url;
        }
    }

    /**
     * 业务的code是否有效
     */
    private boolean filterCode(Object orderRes) {
        if (orderRes instanceof BaseResponse) {
            BaseResponse res = (BaseResponse) orderRes;

            if (res.code == 401) {
                MessageBoxGrasp.infoMsg(mContext, "提示", "会话已过期,请重新登录!", false, v -> {
                    BaApp app = (BaApp) mContext.getApplicationContext();
                    LocalBroadMgr localBroadMgr = new LocalBroadMgr(app);
                    localBroadMgr.broadAction(Constant.ARG_TOKEN_EXPIRE);
                });
                return false;
            }

            if (res.code == 402 || res.code == 403) {
                String verStr = res.msg;
                if (!TextUtils.isEmpty(verStr)) {
                    Intent intent = new Intent();
                    intent.setAction(Constant.ARG_NEW_VERSION);
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra("data", verStr);
                    mContext.sendBroadcast(intent);
                    return false;
                }
                MessageBoxGrasp.infoMsg(mContext, "当前应用版本已过期, 请联系管理员");
                return false;
            }

            if (res.code == 500) {
                String verStr = res.msg;
                TOAST.showShort(mContext, verStr);
                return false;
            }
        }
        return true;
    }

}
