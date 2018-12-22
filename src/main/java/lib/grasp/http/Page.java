package lib.grasp.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by GaQu_Dev on 2018/12/11.
 */
public class Page<T> {
    @SerializedName("currPage")
    public int currPage;

    @SerializedName("list")
    public List<T> list;

    @SerializedName("pageSize")
    public int pageSize;

    @SerializedName("totalCount")
    public int totalCount;

    @SerializedName("totalPage")
    public int totalPage;
}
