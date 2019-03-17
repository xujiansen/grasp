package com.rooten.help.filehttp;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.rooten.util.Utilities;

public class HttpDownloadRequest {
    public String reqId = "";   // 下载请求id
    public long offset = 0;    // 开始下载的位置
    public String requestUrl = "";   // 请求路径
    public File saveFile = null; // 下载保存文件夹路径(包含文件名)
    public long contentLength = 0;    // 下载的内容的长度

    public Map<String, String> headParams;  // 请求头参数
    public Map<String, String> bodyParams;  // 请求体参数

    // 额外的参数（传递用）
    public Map<String, Object> extraParams = new HashMap<>();

    public HttpUtil.onHttpProgressListener progress;    // 进度回调

    public static HttpDownloadRequest createDefaultReq(long offset, String requestUrl, File saveFile, Map<String, String> headParams, Map<String, String> bodyParams, HttpUtil.onHttpProgressListener progress) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpDownloadRequest req = new HttpDownloadRequest();
        req.reqId = UUID.randomUUID().toString();
        req.offset = offset;
        req.requestUrl = requestUrl;
        req.saveFile = saveFile;
        req.headParams = headParams;
        req.bodyParams = bodyParams;
        req.progress = progress;
        return req;
    }

    public static HttpDownloadRequest createDefaultReq(String requestUrl, File saveFile, Map<String, String> headParams, Map<String, String> bodyParams) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpDownloadRequest req = new HttpDownloadRequest();
        req.reqId = UUID.randomUUID().toString();
        req.offset = 0;
        req.requestUrl = requestUrl;
        req.saveFile = saveFile;
        req.headParams = headParams;
        req.bodyParams = bodyParams;
        return req;
    }

    public static HttpDownloadRequest createDefaultReq(String requestUrl, File saveFile, Map<String, String> headParams, Map<String, String> bodyParams, HttpUtil.onHttpProgressListener progress) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpDownloadRequest req = new HttpDownloadRequest();
        req.reqId = UUID.randomUUID().toString();
        req.offset = 0;
        req.requestUrl = requestUrl;
        req.saveFile = saveFile;
        req.headParams = headParams;
        req.bodyParams = bodyParams;
        req.progress = progress;
        return req;
    }

    public void addHeadParams(String name, String value) {
        if (Utilities.isEmpty(name)) return;

        if (headParams == null) {
            headParams = new HashMap<>();
        }

        headParams.put(name, value);
    }

    public void addBodyParams(String name, String value) {
        if (Utilities.isEmpty(name)) return;

        if (bodyParams == null) {
            bodyParams = new HashMap<>();
        }

        bodyParams.put(name, value);
    }

    public void addExtraParams(String name, Object value) {
        if (extraParams == null || Utilities.isEmpty(name)) return;
        extraParams.put(name, value);
    }

    public Object getExtraParamValue(String name) {
        if (Utilities.isEmpty(name)) return "";
        return extraParams.get(name);
    }
}
