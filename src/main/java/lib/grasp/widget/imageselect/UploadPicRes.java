package lib.grasp.widget.imageselect;

import com.google.gson.annotations.SerializedName;

/**
 * 登录返回
 */
public class UploadPicRes {
    // code
    @SerializedName("code")
    public int code;

    // id
    @SerializedName("id")
    public int id;

    // msg
    @SerializedName("msg")
    public String msg;

    // url
    @SerializedName("url")
    public String url;
}
