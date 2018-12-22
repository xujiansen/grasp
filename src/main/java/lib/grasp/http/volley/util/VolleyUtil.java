package lib.grasp.http.volley.util;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import lib.grasp.util.L;


public class VolleyUtil {

    private final static String TAG = VolleyUtil.class.getSimpleName();

    /**
     * 请求异常通用回调器
     */
    public static Response.ErrorListener getErrorListener() {
        return volleyError -> L.logOnly(VolleyUtil.class.getSimpleName(), volleyError.getMessage() + " , " + volleyError);
    }
}
