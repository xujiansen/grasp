package lib.grasp.helper.interf;

/**
 * 传输监听
 */
public interface LoadListener {
    void onSuccess(String url);
    void onFail(String url);
    void onProgress(String url, long curSize, long allSize);
}
