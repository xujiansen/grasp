package lib.grasp.http.okhttp;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * http://blog.csdn.net/lmj623565791/article/details/49734867/
 */
@SuppressWarnings("unused")
public class BaseOkHttpUtil {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    /**
     * PostString，带header(Map)，带json_body(JsonString)，带回调
     */
    public static <T> void doPostJsonBodyRequest(String url, Map<String, String> header, Map<String, String> body, BaseCallBack<T> callBack) {
        OkHttpUtils
                .postString()
                .url(url)
                .headers(header)
                .content(new Gson().toJson(body))
                .mediaType(JSON)
                .build()
                .execute(callBack);
    }


    /**
     * Post，带header(Map)，带body(Map)，带回调
     */
    public static <T> void doPostMapRequest(String url, Map<String, String> header, Map<String, String> body, BaseCallBack<T> callBack) {
        OkHttpUtils
                .post()
                .url(url)
                .headers(header)
                .params(body)
                .build()
                .execute(callBack);
    }

    /**
     * 普通get，带header，带body，带回调
     */
    public static <T> void doGetRequest(String url, Map<String, String> header, Map<String, String> body, BaseCallBack<T> callBack) {
        if (TextUtils.isEmpty(url)) return;
        if (header == null) header = new HashMap<>();
        if (body == null) header = new HashMap<>();

        OkHttpUtils
                .get()
                .url(url)
                .headers(header)
                .params(body)
                .build()
                .execute(callBack);
    }

    /**
     * 将file作为请求体传入到服务端
     */
    public static <T> void doPostFile(String url, File file, BaseCallBack<T> callBack) {
        OkHttpUtils
                .postFile()
                .url(url)
                .file(file)
                .build()
                .execute(callBack);
    }

    /**
     * 上传(字段+文件)
     */
    public static <T> void doPostMultiFile(String url, Map<String, String> header, Map<String, String> body, File file, BaseCallBack<T> callBack) {
        OkHttpUtils
                .post()
                .addParams("key1", "value1")
                .addFile("filename_key1", "filename_value1", file)
                .url(url)
                .headers(header)//
                .params(body)//
                .build()//
                .execute(callBack);
    }

    /**
     * 上传(多文件, 带监听)
     */
    public static void postFileWithProgress(String url, Map<String, String> header, List<File> files, LoadListener listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for(File file : files){
//            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
            builder.addPart(RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }
        MultipartBody multipartBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(header))
                .post(new ProgressRequestBody(multipartBody, listener))
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(listener);
    }
}

