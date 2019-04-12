package lib.grasp.http.volley.util;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import lib.grasp.util.L;


public class VolleyUtil {

    /**
     * 请求异常通用回调器
     */
    public static Response.ErrorListener getErrorListener() {
        return volleyError -> L.logOnly(volleyError.getMessage() + " , " + volleyError);
    }
}
