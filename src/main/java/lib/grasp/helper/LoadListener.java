package lib.grasp.helper;

/**
 * Created by GaQu_Dev on 2018/12/10.
 */
public interface LoadListener {
    void onSuccess(String url);
    void onFail(String url);
    void onProgress(String url, long curSize, long allSize);
}
