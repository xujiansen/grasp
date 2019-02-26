package lib.grasp.http.volley;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

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

import cn.com.rooten.BaApp;
import cn.com.rooten.Constant;
import cn.com.rooten.ctrl.widget.SwipeRefreshLayout;
import cn.com.rooten.help.LocalBroadMgr;
import cn.com.rooten.util.Utilities;
import lib.grasp.http.BaseResponse;
import lib.grasp.http.volley.gsonrequest.ParamRequest;
import lib.grasp.http.volley.gsonrequest.JsonObjRequest;
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
    /**
     * 请求成功之后跳转
     */
    private Intent mIntent;

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
    private int mTimeout = 30;

    /**
     * 本次请求的请求ID
     */
    private String mRequestCode = "";
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
        this(app, context, null, false);
    }

    public VolleyHelper(BaApp app, Context context, Intent it, boolean isShowProg) {
        this.mApp = app;
        this.mContext = context;
        this.mIntent = it;
        this.mIsShowProg = isShowProg;
    }

    private void initProgressDlg() {
        mProgressDlg = new ProgressDlgGrasp(mContext);
        mProgressDlg.setCanBeCancel(true);
        mProgressDlg.setCancelListener(v -> {
            mIsCancel = true;
            if (mProgressDlg != null) {
                mProgressDlg.dismiss();
                mProgressDlg = null;
            }
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

        Response.Listener<T> listener = orderResponse -> {
            if (mSwip != null) {
                mSwip.setRefreshing(false);
                mSwip.setLoading(false);
            }
            if (orderResponse == null) {
                TOAST.showShort(mContext, "请求失败");
                return;
            }

            if (!filterCode(orderResponse)) {
                if (mProgressDlg != null) {
                    mProgressDlg.dismiss();
                    mProgressDlg = null;
                }
                return;
            }

            if (mSuccessListener != null) mSuccessListener.onResponse(orderResponse);
            onPostExecute();
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                L.logOnly(this.getClass(), "请求URL", mURL);
                L.logOnly(this.getClass(), "请求异常", volleyError.toString());

                if (mSwip != null) {
                    mSwip.setRefreshing(false);
                    mSwip.setLoading(false);
                }

                if (volleyError.networkResponse == null) {
                    TOAST.showShort(mContext, "网络异常\n" + volleyError.toString());
                    return;
                }

                if (mErrorListener != null) mErrorListener.onErrorResponse(volleyError);
                noti(volleyError.toString());
                onPostExecute();
            }
        };

        Request mRequest = null;
        if (TextUtils.equals(mHeadParam.get("Content-Type"), "application/json"))
            mRequest = new JsonObjRequest<>(
                    mMethod,
                    mURL,
                    JSONObject.toJSONString(mParam),
                    type,
                    listener,
                    errorListener
            );
        else mRequest = new ParamRequest<T>(
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
        mRequest.setRetryPolicy(new DefaultRetryPolicy(mTimeout * 1000, 1, 1.0f));
        mApp.getRequestQueue().add(mRequest).setTag(mRequestCode);
    }

    private void onPostExecute() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }

        if (mIsCancel) {
            mIsCancel = false;
            return;
        }

        if (mIntent != null) {
            mContext.startActivity(mIntent);
        }
    }

    private void noti(String strMsg) {
        if (mIsCancel) return;
        Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
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
     * 设置请求成功之后的跳转
     */
    public VolleyHelper<T> setIntent(Intent mIntent) {
        this.mIntent = mIntent;
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
                encodedParams.append(Utilities.toURLEncoded(entry.getValue()));
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
                    intent.putExtra("data", verStr);
                    mContext.sendBroadcast(intent);
                    return false;
                }
                MessageBoxGrasp.infoMsg(mContext, "当前应用版本已过期, 请联系管理员");
                return false;
            }
        }
        return true;
    }

}
