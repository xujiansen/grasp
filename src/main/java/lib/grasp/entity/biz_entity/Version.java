package lib.grasp.entity.biz_entity;

import com.google.gson.annotations.SerializedName;

/**
 * 版本检查
 */
public class Version {

    @SerializedName("downloadPath")
    public String downloadPath;

    @SerializedName("publishDate")
    public long publishDate;

    @SerializedName("remark")
    public String remark;

    @SerializedName("versionCode")
    public int versionCode;

    @SerializedName("versionName")
    public String versionName;

    @SerializedName("appSize")
    public long size;

}
