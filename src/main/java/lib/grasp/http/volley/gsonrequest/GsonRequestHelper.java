package lib.grasp.http.volley.gsonrequest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import lib.grasp.http.volley.util.VolleyUtil;

/**
 * GsonReques请求的封装类
 */
public class GsonRequestHelper {
    private final static String TAG = GsonRequestHelper.class.getSimpleName();

    /**
     * 请求
     */
    private GsonRequest mGsonRequest;
    /**
     * 请求队列
     */
    private static RequestQueue mRequestQueue;
    /**
     * Helper
     */
    private static GsonRequestHelper mInstance;

    public static GsonRequestHelper with(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        if (mRequestQueue == null) throw new IllegalArgumentException("RequestQueue not found!");
        if (mInstance == null) mInstance = new GsonRequestHelper();
        return mInstance;
    }

    public interface SuccessListener<T> {
        void onResponse(T response);
    }

    /**
     * 向服务器发送一个GET请求，解析服务器返回的Gson格式的数据，并以指定类型的对象对数据进行封装
     *
     * @param url  请求地址
     * @param type 对象的类型
     */
    public <T> GsonRequestHelper get(String url, Type type, final SuccessListener<T> successListener) {
        mGsonRequest = new GsonRequest<>(
                url,

                type,

                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T orderRes) {
                        if (successListener == null) return;
                        successListener.onResponse(orderRes);
                    }
                },

                VolleyUtil.getErrorListener());
        mRequestQueue.add(mGsonRequest);
        return this;
    }

    /**
     * 向服务器发送一个GET请求，解析服务器返回的Gson格式的数据，并以指定类型的对象对数据进行封装
     *
     * @param url   请求地址
     * @param clazz 对象的类型
     */
    public <T> GsonRequestHelper get(String url, Class<T> clazz, final SuccessListener<T> successListener) {
        mGsonRequest = new GsonRequest<>(
                url,

                clazz,

                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T orderRes) {
                        if (successListener == null) return;
                        successListener.onResponse(orderRes);
                    }
                },

                VolleyUtil.getErrorListener());
        mRequestQueue.add(mGsonRequest);
        return this;
    }

    /**
     * 向服务器发送一个POST请求，解析服务器返回的Gson格式的数据，并以指定类型的对象对数据进行封装
     *
     * @param url             请求地址
     * @param params          参数
     * @param clazz           对象的类型
     * @param successListener 自定义回调监听
     */
    public <T> GsonRequestHelper post(String url, final Map<String, String> params, Class<T> clazz, final SuccessListener<T> successListener) {
        Response.Listener<T> listener = new Response.Listener<T>() {
            @Override
            public void onResponse(T orderRes) {
                if (successListener == null) return;
                successListener.onResponse(orderRes);
            }
        };

        mGsonRequest = new GsonRequest<T>(Request.Method.POST, url, clazz, listener, VolleyUtil.getErrorListener()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                if (params == null) throw new IllegalArgumentException("Map not found!");
                params.putAll(map);
                return params;
            }
        };
        mRequestQueue.add(mGsonRequest);
        return this;
    }

    /**
     * 向服务器发送一个POST请求，解析服务器返回的Gson格式的数据，并以指定类型的对象对数据进行封装
     *
     * @param url             请求地址
     * @param params          参数
     * @param type            对象的类型
     * @param successListener 自定义回调监听
     */
    public <T> GsonRequestHelper post(String url, final Map<String, String> params, Type type, final SuccessListener<T> successListener) {
        Response.Listener<T> listener = new Response.Listener<T>() {
            @Override
            public void onResponse(T orderRes) {
                if (successListener == null) return;
                successListener.onResponse(orderRes);
            }
        };

        mGsonRequest = new GsonRequest<T>(Request.Method.POST, url, type, listener, VolleyUtil.getErrorListener()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                if (params == null) throw new IllegalArgumentException("Map not found!");
                params.putAll(map);
                return params;
            }
        };
        mRequestQueue.add(mGsonRequest);
        return this;
    }

    public void cancelAll() {
        if (mGsonRequest == null) return;
        mRequestQueue.cancelAll(mGsonRequest);
    }
}
