package lib.grasp.http.volley.gsonrequest;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import lib.grasp.util.L;

/**
 * [自定义]带泛型的Request of volley
 */
public class ParamRequest<T> extends Request<T> {
    private final static String TAG = ParamRequest.class.getSimpleName();

    private Type mType;
    private Gson mGson;
    private Class<T> mClass;

    private final Response.Listener<T> mListener;

    public ParamRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mClass = clazz;
        mListener = listener;
    }

    public ParamRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    public ParamRequest(int method, String url, Type type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mListener = listener;
        mType = type;
    }

    public ParamRequest(String url, Type type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, type, listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (!TextUtils.isEmpty(jsonString)) L.logOnly("jsonString" + jsonString);
            L.logOnly("服务器: --- " + jsonString);
            T result = null;
            if((mType != null) && new TypeToken<byte[]>() {}.getType().equals(mType))   result = (T)response.data;
            else if (mType != null)                                                     result = mGson.fromJson(jsonString, mType);
            else                                                                        result = mGson.fromJson(jsonString, mClass);

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }
}
