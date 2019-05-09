package com.rooten.help.filehttp;


import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** 网络请求封装类(包含监听者) */
public class HttpDownloadRequest {
    /** 下载请求id */
    public String reqId = "";
    /** 开始下载的位置 */
    public long offset = 0;
    /** 请求路径 */
    public String requestUrl = "";
    /** 下载保存文件夹路径(包含文件名) */
    public File saveFile = null;
    /** 下载的内容的长度 */
    public long contentLength = 0;

    /** 请求头参数 */
    public Map<String, String> headParams;
    /** 请求体参数 */
    public Map<String, String> bodyParams;

    /** 额外的参数（传递用） */
    public Map<String, Object> extraParams = new HashMap<>();

    /** 进度监听者 */
    public HttpUtil.onHttpProgressListener mProgressListener;

    public static HttpDownloadRequest createDefaultReq(long offset, String requestUrl, File saveFile, Map<String, String> headParams, Map<String, String> bodyParams, HttpUtil.onHttpProgressListener listener) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpDownloadRequest req = new HttpDownloadRequest();
        req.reqId       = UUID.randomUUID().toString();
        req.offset      = offset;
        req.requestUrl  = requestUrl;
        req.saveFile    = saveFile;
        req.headParams  = headParams;
        req.bodyParams  = bodyParams;
        req.mProgressListener = listener;
        return req;
    }

    public static HttpDownloadRequest createDefaultReq(String requestUrl, File saveFile, Map<String, String> headParams, Map<String, String> bodyParams) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpDownloadRequest req = new HttpDownloadRequest();
        req.reqId       = UUID.randomUUID().toString();
        req.offset      = 0;
        req.requestUrl  = requestUrl;
        req.saveFile    = saveFile;
        req.headParams  = headParams;
        req.bodyParams  = bodyParams;
        return req;
    }

    public static HttpDownloadRequest createDefaultReq(String requestUrl, File saveFile, Map<String, String> headParams, Map<String, String> bodyParams, HttpUtil.onHttpProgressListener listener) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpDownloadRequest req = new HttpDownloadRequest();
        req.reqId       = UUID.randomUUID().toString();
        req.offset      = 0;
        req.requestUrl  = requestUrl;
        req.saveFile    = saveFile;
        req.headParams  = headParams;
        req.bodyParams  = bodyParams;
        req.mProgressListener = listener;
        return req;
    }

    public void addHeadParams(String name, String value) {
        if (TextUtils.isEmpty(name)) return;

        if (headParams == null) {
            headParams = new HashMap<>();
        }

        headParams.put(name, value);
    }

    public void addBodyParams(String name, String value) {
        if (TextUtils.isEmpty(name)) return;

        if (bodyParams == null) {
            bodyParams = new HashMap<>();
        }

        bodyParams.put(name, value);
    }

    public void addExtraParams(String name, Object value) {
        if (extraParams == null || TextUtils.isEmpty(name)) return;
        extraParams.put(name, value);
    }

    public Object getExtraParamValue(String name) {
        if (TextUtils.isEmpty(name)) return "";
        return extraParams.get(name);
    }
}
