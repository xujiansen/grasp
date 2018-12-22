package lib.grasp.http.volley.jsonobject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import lib.grasp.http.volley.util.VolleyUtil;

/**
 * Request请求的封装类
 */
public class JsonObjectRequestHelper {
    private final static String TAG = JsonObjectRequestHelper.class.getSimpleName();

    /**
     * 请求队列
     */
    private static RequestQueue mRequestQueue;
    /**
     * Helper
     */
    private static JsonObjectRequestHelper mInstance;

    public static JsonObjectRequestHelper with(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        if (mRequestQueue == null) throw new IllegalArgumentException("RequestQueue not found!");
        if (mInstance == null) mInstance = new JsonObjectRequestHelper();
        return mInstance;
    }

    public void cancelAll(String tag) {
        mRequestQueue.cancelAll(tag);
    }

    /**
     * 以Get方式向服务器发送请求,并将数据以JSONObject对象返回
     *
     * @param url      服务器url地址
     * @param tag      加入到RequestQueue队列的TAG值
     * @param listener 请求成功返回的回调
     */
    public JsonObjectRequestHelper doGetRequest(String url, String tag, Response.Listener<JSONObject> listener) {
        return doPostRequest(url, null, tag, listener);
    }

    /**
     * 以Get方式向服务器发送请求,并将数据封装到Bean对象中返回
     *
     * @param url             服务器url地址
     * @param tag             加入到RequestQueue队列的TAG值
     * @param type            返回数据的Type类型
     * @param successListener 请求成功返回的回调
     */
    public <T> JsonObjectRequestHelper doGetRequest(String url, String tag, final Type type, final JSONOBJECTSuccessListener<T> successListener) {
        final Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    T response = new Gson().fromJson(jsonObject.toString(), type);
                    successListener.onResponse(response);
                }
            }
        };
        return doPostRequest(url, null, tag, listener);
    }

    /**
     * 以Post方式向服务器发送请求,并将数据以JSONObject对象返回
     *
     * @param url         服务器url地址
     * @param jsonRequest 携带的JSONObject格式的json参数
     * @param tag         加入到RequestQueue队列的TAG值
     * @param listener    请求成功返回的回调
     */
    public JsonObjectRequestHelper doPostRequest(String url, JSONObject jsonRequest, String tag, Response.Listener<JSONObject> listener) {

        JsonObjectRequest request = new JsonObjectRequest(url, jsonRequest, listener, VolleyUtil.getErrorListener());
        mRequestQueue.add(request).setTag(tag);
        return mInstance;
    }

    /**
     * 以Post方式向服务器发送请求,并将数据封装到Bean对象中返回
     *
     * @param url             服务器url地址
     * @param jsonRequest     携带的JSONObject格式的json参数
     * @param tag             加入到RequestQueue队列的TAG值
     * @param type            type
     * @param successListener 请求成功返回的回调
     */
    public <T> JsonObjectRequestHelper doPostRequest(String url, JSONObject jsonRequest, String tag, final Type type, final JSONOBJECTSuccessListener<T> successListener) {

        final Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    T response = new Gson().fromJson(jsonObject.toString(), type);
                    successListener.onResponse(response);
                }
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(url, jsonRequest, listener, VolleyUtil.getErrorListener());
        mRequestQueue.add(request).setTag(tag);
        return mInstance;
    }

    /**
     * 以Post方式向服务器发送请求,并将数据封装到Bean对象中返回
     *
     * @param url             服务器url地址
     * @param json            携带的map格式的json参数
     * @param tag             加入到RequestQueue队列的TAG值
     * @param type            type
     * @param successListener 请求成功返回的回调
     */
    public <T> JsonObjectRequestHelper doPostRequest(String url, Map json, String tag, final Type type, final JSONOBJECTSuccessListener<T> successListener) {

        return doPostRequest(url, new JSONObject(json), tag, type, successListener);
    }

    /**
     * 以Post方式向服务器发送请求,并将数据封装到Bean对象中返回
     *
     * @param url             服务器url地址
     * @param json            携带的String格式的json参数
     * @param tag             加入到RequestQueue队列的TAG值
     * @param type            type
     * @param successListener 请求成功返回的回调
     */
    public <T> JsonObjectRequestHelper doPostRequest(String url, String json, String tag, final Type type, final JSONOBJECTSuccessListener<T> successListener) {

        try {
            return doPostRequest(url, new JSONObject().getJSONObject(json), tag, type, successListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 以Post方式向服务器发送请求,并将数据封装到Bean对象中返回
//     *
//     * @param url             服务器url地址
//     * @param tag             加入到RequestQueue队列的TAG值
//     * @param params          携带的map格式的json参数
//     * @param type            type
//     * @param successListener 请求成功返回的回调
//     */
//    public <T> JsonObjectRequestHelper doPostRequest(String url, String tag, final Map params, final Type type, final JSONOBJECTSuccessListener<T> successListener) {
//
//        final BaseResponse.Listener<String> listener = new BaseResponse.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                LogUtil.logOnly("JSONOBJECTRequestHelper", response);
//                T data = new Gson().fromJson(response, type);
//                successListener.onResponse(data);
//            }
//        };
//            StringRequest stringRequest =
//                new StringRequest(Request.Method.POST, url, listener, VolleyUtil.getErrorListener()) {
//                    @Override
//                    protected Map<String, String> getParams() {
//                        return params;
//                    }
//                };
//        mRequestQueue.add(stringRequest).setTag(tag); // 放入到请求队列， 自动执行
//        return mInstance;
//    }

    public interface JSONOBJECTSuccessListener<T> {
        void onResponse(T response);
    }
}
