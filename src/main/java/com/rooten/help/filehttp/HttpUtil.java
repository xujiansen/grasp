package com.rooten.help.filehttp;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lib.grasp.util.L;
import lib.grasp.util.StreamUtil;

public class HttpUtil {
    /** 上传文件 */
    public static boolean uploadFile(HttpUploadRequest req) {
        return uploadFile(req, req.progress);
    }

    /** 上传文件 */
    public static boolean uploadFile(HttpUploadRequest req, onHttpProgressListener l) {
        if (req == null) return false;
        if (req.headParams == null) req.headParams = new HashMap<>();
        if (req.bodyParams == null) req.bodyParams = new HashMap<>();

        int BLOCK_SIZE = 64 * 1024; // 大小
        long fileLength = req.uploadFile.length();
        if (fileLength > 0 && fileLength <= 100 * 1024) BLOCK_SIZE = (int) fileLength / 2;
        if (fileLength > 100 * 1024 && fileLength <= 400 * 1024) BLOCK_SIZE = 64 * 1024;
        if (fileLength > 400 * 1024 && fileLength <= 1024 * 1024) BLOCK_SIZE = 120 * 1024;
        if (fileLength > 1024 * 1024) BLOCK_SIZE = 200 * 1024;

        final String CHARSET = "utf-8"; // 设置编码

        HttpURLConnection conn = null;
        BufferedOutputStream paramsOut = null;
        DataOutputStream dos = null;
        InputStream is = null;

        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        BOUNDARY = "---------------------------123821742118716";    // (云盘接口定制)boundary就是request头和上传文件内容的分隔符
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            if (req.uploadFile == null || !req.uploadFile.exists()) {
                L.log("uploadFile::uploadFile" + "上传文件为空或者不存在");
                return false;
            }

            L.log("uploadFile::requestUrl" + req.requestUrl);
            L.log("uploadFile::uploadFile" + req.uploadFile.toString());

            URL url = new URL(req.requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60 * 1000);
            conn.setConnectTimeout(60 * 1000);
            conn.setDoInput(true);                            // 允许输入流
            conn.setDoOutput(true);                        // 允许输出流
            conn.setUseCaches(false);                        // 不允许使用缓存
            conn.setRequestMethod("POST");                    // 请求方式
            conn.setChunkedStreamingMode(BLOCK_SIZE);        // 设置块大小
            conn.setRequestProperty("Charset", CHARSET);    // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + "; boundary=" + BOUNDARY);

            // 传输头里面的参数
            for (Map.Entry<String, String> entry : req.headParams.entrySet())    //构造文本类型参数的实体数据
            {
                String value = urlEncode(entry.getValue());
                conn.setRequestProperty(entry.getKey(), value);
            }
            conn.connect();

            // 传输请求体参数
            StringBuilder textEntity = new StringBuilder();
            for (Map.Entry<String, Object> entry : req.bodyParams.entrySet())    //构造文本类型参数的实体数据
            {
                textEntity.append(PREFIX);
                textEntity.append(BOUNDARY);
                textEntity.append(LINE_END);
                textEntity.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"");
                textEntity.append("\r\n\r\n");
                textEntity.append(entry.getValue());
                textEntity.append(LINE_END);
            }

            // 在connect();之后执行
            paramsOut = new BufferedOutputStream(conn.getOutputStream());
            paramsOut.write(textEntity.toString().getBytes(CHARSET));
            paramsOut.flush();

            // 把文件包装并且上传
            dos = new DataOutputStream(conn.getOutputStream());
            StringBuilder fileEntity = new StringBuilder();
            fileEntity.append(PREFIX);
            fileEntity.append(BOUNDARY);
            fileEntity.append(LINE_END);

            // 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
            // filename是文件的名字，包含后缀名的 比如:abc.png
            fileEntity.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + req.uploadFile.getName() + "\"" + LINE_END);
//            fileEntity.append("Content-Disposition: form-data; name=\"" + req.fileKey + "\"; filename=\"" + req.uploadFile.getName() + "\"" + LINE_END);
            fileEntity.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
            fileEntity.append(LINE_END);
            dos.write(fileEntity.toString().getBytes());

            is = new FileInputStream(req.uploadFile);
            long uploadSize = req.offset;
            if (uploadSize > 0) {
                long skipBytes = is.skip(uploadSize);
                if (skipBytes != uploadSize) {
                    L.log("uploadFile::skip-error" + "跳过头错误!");
                    return false;
                }
            }

            byte[] bytes = new byte[BLOCK_SIZE];
            int len = 0;
            while ((len = is.read(bytes, 0, BLOCK_SIZE)) != -1) {
                dos.write(bytes, 0, len);
                uploadSize += len;

                if (l != null) {
                    if (l.isQuit()) {
                        return false; // 取消
                    }

                    l.onProgress(req.reqId, req.requestUrl, uploadSize, req.uploadFile.length());
                }
            }
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();

            // 获取响应码 200=成功 当响应成功，获取响应的流
            int code = conn.getResponseCode();
            L.log("uploadFile::code" + String.valueOf(code));
            if (code == 200) {
                String res = readResponseString(conn);
                return true;
            } else {
                String err = readErrStream(conn);
                L.log("uploadFile::err" + err);
                return false;
            }
        } catch (Exception e) {
            L.log("uploadFile::Exception" + e.toString());
            return false;
        } finally {
            if (conn != null) conn.disconnect();
            closeInputStream(is);
            closeOutputStream(paramsOut);
            closeOutputStream(dos);
        }
    }

    /** 下载文件 */
    public static boolean downloadFile(HttpDownloadRequest req) {
        return downloadFile(req, req.mProgressListener); // 失败
    }

    /** 下载文件 */
    public static boolean downloadFile(HttpDownloadRequest req, onHttpProgressListener l) {
        if (req == null) return false;
        if (req.headParams == null) req.headParams = new HashMap<>();
        if (req.bodyParams == null) req.bodyParams = new HashMap<>();

        int BLOCK_SIZE = 64 * 1024; // 大小
        final String CHARSET = "utf-8";   // 设置编码

        HttpURLConnection conn = null;
        RandomAccessFile rac = null;
        BufferedOutputStream out = null;
        InputStream in = null;

        try {
            L.log("downloadFile::requestUrl: " + req.requestUrl);

            // 因为是urlEncode编码格式并且是GET方法所以，参数必须拼接在url后面
            // 传输请求体参数
            StringBuilder bodyEntity = new StringBuilder();
            for (Map.Entry<String, String> entry : req.bodyParams.entrySet())    //构造文本类型参数的实体数据
            {
                String key = entry.getKey();
                String value = entry.getValue();
                boolean isFirst = bodyEntity.length() == 0;
                bodyEntity.append(isFirst ? "" : "&");
                bodyEntity.append(key).append("=").append(urlEncode(value));
            }

            URL url = new URL(req.requestUrl + "?" + bodyEntity);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60 * 1000);
            conn.setConnectTimeout(60 * 1000);
            conn.setDoInput(true);                            // 允许输入流
//            conn.setDoOutput(true);                        // 允许输出流
            conn.setUseCaches(false);                        // 不允许使用缓存
            conn.setRequestMethod("GET");                    // 请求方式
            conn.setRequestProperty("Charset", CHARSET);    // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Accept-Encoding", "identity");
//            conn.setRequestProperty("Range", "bytes=" + req.offset + "-");

            // 传输头里面的参数
//            for (Map.Entry<String, String> entry : req.headParams.entrySet())    //构造文本类型参数的实体数据
//            {
//                String value = urlEncode(entry.getValue());
//                conn.setRequestProperty(entry.getKey(), value);
//            }
            conn.connect();

            int code = conn.getResponseCode();
            L.log("downloadFile::code" + String.valueOf(code));
            if (code < 200 || code >= 300) {
                String err = readErrStream(conn);
                System.out.println("downloadFile::err" + err);
                L.log("downloadFile::err" + err);
                return false;
            }

            // 读取文件数据
            int contentLength = conn.getContentLength();
            L.log("downloadFile::contentLength: " + String.valueOf(contentLength));
            if (contentLength <= 0) return false;

            // 设置下载块的大小
            if (contentLength <= 100 * 1024)
                BLOCK_SIZE = (int) contentLength / 2;
            if (contentLength > 100 * 1024 && contentLength <= 400 * 1024) BLOCK_SIZE = 64 * 1024;
            if (contentLength > 400 * 1024 && contentLength <= 1024 * 1024) BLOCK_SIZE = 120 * 1024;
            if (contentLength > 1024 * 1024) BLOCK_SIZE = 200 * 1024;

            byte[] buf = new byte[BLOCK_SIZE];
            in = conn.getInputStream();

            File saveFile = req.saveFile;
            if (saveFile == null) {
                L.log("downloadFile::saveFile: 保存路径为空");
                return false;
            }

            File parent = saveFile.getParentFile();
            if (!parent.exists()) {
                boolean isMkdirs = parent.mkdirs();
                if (!isMkdirs) {
                    L.log("downloadFile::mkdirs-savePath" + "创建保存父路径失败");
                    return false;
                }
            }

            rac = new RandomAccessFile(saveFile, "rw");
            long size = req.offset;
            if (size > 0) {
                rac.seek(size);
            }

            int len;
            while ((len = in.read(buf, 0, BLOCK_SIZE)) != -1) {
                rac.write(buf, 0, len);
                size += len;

                if (l != null) {
                    if (l.isQuit()) {
                        return false; // 取消
                    }

                    l.onProgress(req.reqId, req.requestUrl, size, contentLength);
                }
            }

            L.log("downloadFile::finish-length" + String.valueOf(rac.length()));
            return true; // 成功
        } catch (Exception e) {
            L.log("downloadFile::Exception" + e.toString());
            return false; // 失败
        } finally {
            StreamUtil.closeRandomAccessStream(rac);
            closeInputStream(in);
            closeOutputStream(out);
            if (conn != null) conn.disconnect();
        }
    }

    public static boolean downloadFileByJson(HttpDownloadRequest req) {
        return downloadFile(req, req.mProgressListener); // 失败
    }

    public static boolean downloadFileByJson(HttpDownloadRequest req, onHttpProgressListener l) {
        if (req == null) return false;
        if (req.headParams == null) req.headParams = new HashMap<>();
        if (req.bodyParams == null) req.bodyParams = new HashMap<>();

        int BLOCK_SIZE = 64 * 1024; // 大小
        final String CHARSET = "utf-8";   // 设置编码

        HttpURLConnection conn = null;
        RandomAccessFile rac = null;
        BufferedOutputStream out = null;
        InputStream in = null;

        try {
            L.log("downloadFileByJson::requestUrl" + req.requestUrl);

            URL url = new URL(req.requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60 * 1000);
            conn.setConnectTimeout(60 * 1000);
            conn.setDoInput(true);                            // 允许输入流
            conn.setDoOutput(true);                        // 允许输出流
            conn.setUseCaches(false);                        // 不允许使用缓存
            conn.setRequestMethod("GET");                    // 请求方式
            conn.setRequestProperty("Charset", CHARSET);    // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Accept-Encoding", "identity");
            conn.setRequestProperty("Range", "bytes=" + req.offset + "-");
            conn.setRequestProperty("Content-Type", "application/json");

            // 传输头里面的参数
            for (Map.Entry<String, String> entry : req.headParams.entrySet())    //构造文本类型参数的实体数据
            {
                String value = urlEncode(entry.getValue());
                conn.setRequestProperty(entry.getKey(), value);
            }
            conn.connect();

            // 传输请求体参数
            String textEntity = JSONObject.toJSONString(req.bodyParams);

            // 在connect();之后执行
            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(textEntity.getBytes("utf-8"));
            out.flush();

            int code = conn.getResponseCode();
            L.log("downloadFileByJson::code" + String.valueOf(code));
            if (code < 200 || code >= 300) {
                String err = readErrStream(conn);
                L.log("downloadFileByJson::err" + err);
                return false;
            }

            // 读取文件数据
            int contentLength = conn.getContentLength();
            L.log("downloadFileByJson::contentLength" + String.valueOf(contentLength));
            if (contentLength <= 0) return false;

            // 设置下载块的大小
            if (contentLength > 0 && contentLength <= 100 * 1024)
                BLOCK_SIZE = (int) contentLength / 2;
            if (contentLength > 100 * 1024 && contentLength <= 400 * 1024) BLOCK_SIZE = 64 * 1024;
            if (contentLength > 400 * 1024 && contentLength <= 1024 * 1024) BLOCK_SIZE = 120 * 1024;
            if (contentLength > 1024 * 1024) BLOCK_SIZE = 200 * 1024;

            byte[] buf = new byte[BLOCK_SIZE];
            in = conn.getInputStream();

            File saveFile = req.saveFile;
            if (saveFile == null) {
                L.log("downloadFileByJson::saveFile" + "保存路径为空");
                return false;
            }

            File parent = saveFile.getParentFile();
            if (!parent.exists()) {
                boolean isMkdirs = parent.mkdirs();
                if (!isMkdirs) {
                    L.log("downloadFileByJson::mkdirs-savePath" + "创建保存父路径失败");
                    return false;
                }
            }

            rac = new RandomAccessFile(saveFile, "rw");
            long size = req.offset;
            if (size > 0) {
                rac.seek(size);
            }

            int len;
            while ((len = in.read(buf, 0, BLOCK_SIZE)) != -1) {
                rac.write(buf, 0, len);
                size += len;

                if (l != null) {
                    if (l.isQuit()) {
                        return false; // 取消
                    }

                    l.onProgress(req.reqId, req.requestUrl, size, contentLength);
                }
            }

            L.log("downloadFileByJson::finish-length" + String.valueOf(rac.length()));
            return true; // 成功
        } catch (Exception e) {
            L.log("downloadFileByJson::Exception" + e.toString());
            return false; // 失败
        } finally {
            closeRandomAccessStream(rac);
            closeInputStream(in);
            closeOutputStream(out);
            if (conn != null) conn.disconnect();
        }
    }

    /** Post发送表单, 返回ResponseCode */
    public static boolean postParam(String requestUrl, Map<String, String> params) {
        if (params == null) params = new HashMap<>();

        HttpURLConnection conn = null;
        OutputStream out = null;

        try {
            L.log("urlPost::requestUrl" + requestUrl);

            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(60 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            StringBuilder textEntity = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet())    //构造文本类型参数的实体数据
            {
                String key = entry.getKey();
                String value = entry.getValue();
                boolean isFirst = textEntity.length() == 0;
                textEntity.append(isFirst ? "" : "&");
                textEntity.append(key).append("=").append(urlEncode(value));
            }

            // 在connect();之后执行
            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(textEntity.toString().getBytes("utf-8"));
            out.flush();

            int code = conn.getResponseCode();
            L.log("urlPost::code" + String.valueOf(code));
            if (code != 200) {
                String err = readErrStream(conn);
                L.log("urlPost::err" + err);
            }
            return code == 200;
        } catch (Exception e) {
            L.log("urlPost::Exception" + e.toString());
            return false;
        } finally {
            closeOutputStream(out);
            if (conn != null) conn.disconnect();
        }
    }

    /** Post发送json, 返回ResponseCode */
    public static boolean postJson(String requestUrl, Map<String, String> params) {
        if (params == null) params = new HashMap<>();

        HttpURLConnection conn = null;
        OutputStream out = null;

        try {
            L.log("urlPostByJson::requestUrl" + requestUrl);

            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(60 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            String textEntity = JSONObject.toJSONString(params);

            // 在connect();之后执行
            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(textEntity.getBytes("utf-8"));
            out.flush();

            int code = conn.getResponseCode();
            L.log("urlPostByJson::code" + String.valueOf(code));
            if (code != 200) {
                String err = readErrStream(conn);
                L.log("urlPostByJson::err" + err);
            }
            return code == 200;
        } catch (Exception e) {
            L.log("urlPostByJson::Exception" + e.toString());
            return false;
        } finally {
            closeOutputStream(out);
            if (conn != null) conn.disconnect();
        }
    }

    /** Post发送json, 返回ResponseString */
    public static String postJson4String(String requestUrl, Map<String, String> headParams, Map<String, String> bodyParams) {
        if (headParams == null) headParams = new HashMap<>();
        if (bodyParams == null) bodyParams = new HashMap<>();

        HttpURLConnection conn = null;
        OutputStream out = null;

        try {
            L.log("urlPostByJson::requestUrl" + requestUrl);

            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(60 * 1000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type", "application/json");

            // 传输头里面的参数
            for (Map.Entry<String, String> entry : headParams.entrySet())    //构造文本类型参数的实体数据
            {
                String value = urlEncode(entry.getValue());
                conn.setRequestProperty(entry.getKey(), value);
            }
            conn.connect();

            // 在connect();之后执行
            String textEntity = JSONObject.toJSONString(bodyParams);
            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(textEntity.getBytes("utf-8"));
            out.flush();

            int code = conn.getResponseCode();
            L.log("urlPostByJson::code" + String.valueOf(code));
            if (code != 200) {
                String err = readErrStream(conn);
                L.log("urlPostByJson::err" + err);
            }

            return readResponseString(conn);
        } catch (Exception e) {
            L.log("urlPostByJson::Exception" + e.toString());
            return "";
        } finally {
            closeOutputStream(out);
            if (conn != null) conn.disconnect();
        }
    }


    
    /** 对GET请求的URL进行Encode */
    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception e) {
            return "";
        }
    }



    /** 读返回的字节流(一般二进制文件) */
    private static String readResponseStream(URLConnection conn) throws IOException {
        int len;
        byte[] buf = new byte[1024];
        InputStream in = conn.getInputStream();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        while ((len = in.read(buf, 0, buf.length)) != -1) {
            byteOut.write(buf, 0, len);
            byteOut.flush();
        }

        byte[] data = byteOut.toByteArray();
        return new String(data, 0, data.length);
    }

    /** 读返回的字符流(一般文字) */
    private static String readResponseString(HttpURLConnection conn) throws IOException {
        InputStream inputStream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            stringBuffer.append(line);
        }
        reader.close();
        return stringBuffer.toString();
    }

    /** 读返回的异常字节流(一般文字) */
    private static String readErrStream(HttpURLConnection conn) throws IOException {
        int len;
        byte[] buf = new byte[1024];
        InputStream in = conn.getErrorStream();
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        while ((len = in.read(buf, 0, buf.length)) != -1) {
            byteOut.write(buf, 0, len);
            byteOut.flush();
        }
        byte[] data = byteOut.toByteArray();
        return new String(data, 0, data.length);
    }


    /** 关闭输出流 */
    public static void closeOutputStream(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
        }
    }

    /** 关闭输入流 */
    public static void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    /** 关闭输入流 */
    public static void closeRandomAccessStream(RandomAccessFile is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }

    public interface onHttpProgressListener {
        /** 是否放弃本次回调 */
        boolean isQuit();
        /** 传输过程回调 */
        void    onProgress(String requestID, String url, long curSize, long allLen);
//        /** 传输完成返回文字数据 */
//        void    onResMsg(String requestID, String url, String res);
    }
}
