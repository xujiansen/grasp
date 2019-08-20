package lib.grasp.http.okhttp;

import com.google.gson.annotations.SerializedName;

/**
 * 查对象
 */
public class ObjResponse<T> extends BaseResponse {
    @SerializedName("data")
    public T data;
}
