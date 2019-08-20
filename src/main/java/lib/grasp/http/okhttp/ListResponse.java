package lib.grasp.http.okhttp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查列表
 */
public class ListResponse<T> extends BaseResponse {
    @SerializedName("datas")
    public List<T> datas;
}
