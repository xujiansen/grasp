package lib.grasp.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by GaQu_Dev on 2018/11/1.
 */
public class BaseResponse {
    @SerializedName("msg")
    public String msg;

    @SerializedName("code")
    public int code;

    @SerializedName("timeStamp")
    public long timeStamp;
}
