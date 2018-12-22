package lib.grasp.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 查列表
 */
public class PageResponse<T> extends BaseResponse {

    @SerializedName("page")
    public Page<T> page;
}
