package com.rooten.help.filehttp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.rooten.util.Utilities;

public class HttpUploadRequest {
    public String reqId = "";       // 请求id
    public long offset = 0;        // 开始上传的位置
    public String fileKey = "";       // 表单上传时候表示文件的Key
    public String requestUrl = "";       // 请求的URL
    public File uploadFile = null;     // 待上传的文件路径
    public long loadStatus = 0;     // 上传状态(>=0: 上传中, -1:成功, -2:失败, -3:放弃)
    public String resInfo = "";     // 服务器成功返回信息

    public Map<String, String> headParams;  // 请求头参数
    public Map<String, Object> bodyParams;  // 请求体参数

    // 额外的参数（传递用）
    public Map<String, Object> extraParams = new HashMap<>();

    public HttpUtil.onHttpProgressListener progress;    // 进度回调

    public static HttpUploadRequest createDefaultReq(String fileKey, String requestUrl, File uploadFile, Map<String, String> headParams, Map<String, Object> bodyParams) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpUploadRequest req = new HttpUploadRequest();
        req.reqId = UUID.randomUUID().toString();
        req.offset = 0;
        req.fileKey = fileKey;
        req.requestUrl = requestUrl;
        req.uploadFile = uploadFile;
        req.headParams = headParams;
        req.bodyParams = bodyParams;
        return req;
    }

    public static HttpUploadRequest createDefaultReq(String request, String fileKey, String requestUrl, File uploadFile, Map<String, String> headParams, Map<String, Object> bodyParams, HttpUtil.onHttpProgressListener progress) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpUploadRequest req = new HttpUploadRequest();
        req.reqId = request;
        req.offset = 0;
        req.fileKey = fileKey;
        req.requestUrl = requestUrl;
        req.uploadFile = uploadFile;
        req.headParams = headParams;
        req.bodyParams = bodyParams;
        req.progress = progress;
        return req;
    }

    public static HttpUploadRequest createDefaultReq(String fileKey, String requestUrl, File uploadFile, Map<String, String> headParams, Map<String, Object> bodyParams, HttpUtil.onHttpProgressListener progress) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpUploadRequest req = new HttpUploadRequest();
        req.reqId = UUID.randomUUID().toString();
        req.offset = 0;
        req.fileKey = fileKey;
        req.requestUrl = requestUrl;
        req.uploadFile = uploadFile;
        req.headParams = headParams;
        req.bodyParams = bodyParams;
        req.progress = progress;
        return req;
    }

    public static HttpUploadRequest createDefaultReq(long offset, String fileKey, String requestUrl, File uploadFile, Map<String, String> headParams, Map<String, Object> bodyParams, HttpUtil.onHttpProgressListener progress) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpUploadRequest req = new HttpUploadRequest();
        req.reqId = UUID.randomUUID().toString();
        req.offset = offset;
        req.fileKey = fileKey;
        req.requestUrl = requestUrl;
        req.uploadFile = uploadFile;
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

    public void addBodyParams(String name, Object value) {
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
