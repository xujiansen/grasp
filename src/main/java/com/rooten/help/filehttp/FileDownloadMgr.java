package com.rooten.help.filehttp;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.rooten.frame.AppHandler;
import com.rooten.frame.IHandler;
import com.rooten.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lib.grasp.util.FileUtil;
import lib.grasp.util.L;
import lib.grasp.util.PathUtil;

public class FileDownloadMgr implements IHandler {
    /** 锁 - 实现[添加]与[读取]任务都是同步互斥的 */
    private Lock mLock = new ReentrantLock();
    /** 是否准备停止各传输线程 */
    private boolean mQuit = false;

    /** 最大下载线程数(包含各类传输任务) */
    private int DOWNLOAD_MAX = 2;

    /** <传输任务种类ID, 每个任务种类(多个线程)的说明> */
    private Map<String, String>     mTypeDescription = new HashMap<>();
    /** <传输任务种类ID, 每个任务种类(多个线程)的线程数> */
    private Map<String, Integer>    mTypeThreadNum = new HashMap<>();
    /** <传输任务种类ID, 每个任务种类(多个线程)的信号量> */
    private Map<String, Semaphore>  mTypeSemaphore = new HashMap<>();
    /** <传输任务种类ID, 线程队列> */
    private Map<String, Queue<HttpDownloadRequest>> mTypeThreadQueue = new HashMap<>();

    /** 下载成功 */
    public static final long DOWNLOADSTATUS_SUCCESS = -1;
    /** 下载失败 */
    public static final long DOWNLOADSTATUS_FALIURE = -2;

    public AppHandler mHandler = new AppHandler(this);

    /** 下载线程数量 */
    public FileDownloadMgr(int threadNum) {
        DOWNLOAD_MAX = threadNum;
    }

    /** 开始运行传输线程(没有传入任务) */
    public void startDownload() {
        mQuit = false;
        for (Map.Entry<String, Integer> entry : mTypeThreadNum.entrySet()) {
            String categoryID = entry.getKey();
            int threadNum = entry.getValue();
            String description = getDescription(categoryID);

            for (int i = 0; i < threadNum; i++) {
                String threadName = description + "线程" + (i + 1);
                Thread t = new Thread(new DownRun(categoryID), threadName);
                t.start();
            }
        }
    }

    /** 停止传输线程 (这里允许各传输线程将当前的任务执行完成, 但不接收新任务) */
    public void stopDownload() {
        mQuit = true;   // 置退出标签
        for (Map.Entry<String, Integer> entry : mTypeThreadNum.entrySet()) {
            String categoryID = entry.getKey();
            int threadNum = entry.getValue();
            // 这里允许各传输线程将当前的任务执行完成, 但不接收新任务
            Semaphore semaphore = getSemaphore(categoryID);
            if (semaphore == null) continue;
            semaphore.release(threadNum);
        }

        mTypeDescription.clear();
        mTypeThreadNum.clear();
        mTypeSemaphore.clear();
        mTypeThreadQueue.clear();
    }

    /** 注册下载类型，返回注册成功的id，否则返回空字符串 */
    public String registerCategory(String description, int threadNum) {
        if (threadNum <= 0 || threadNum > DOWNLOAD_MAX) return "";

        int hasRegisterThreadNum = 0;
        for (Map.Entry<String, Integer> entry : mTypeThreadNum.entrySet()) {
            hasRegisterThreadNum += entry.getValue();
        }

        if (hasRegisterThreadNum >= DOWNLOAD_MAX) return "";

        int left = DOWNLOAD_MAX - hasRegisterThreadNum;
        if (threadNum > left) return "";

        String categoryID = UUID.randomUUID().toString();
        mTypeDescription.put(categoryID, description);
        mTypeThreadNum.put(categoryID, threadNum);

        init();

        return categoryID;
    }

    /** 解注册下载类型，返回解注册执行结果 */
    public boolean unRegisterCategory(String categoryID) {
        if (TextUtils.isEmpty(categoryID)) return false;
        if (!mTypeThreadNum.containsKey(categoryID)) return false;

        mTypeThreadNum.remove(categoryID);
        mTypeDescription.remove(categoryID);
        mTypeSemaphore.remove(categoryID);
        mTypeThreadQueue.remove(categoryID);

        init();

        return true;
    }

    /** 传入一个[传输任务种类ID] + [传输任务], 开始传输 */
    public void addDownWork(String categoryID, HttpDownloadRequest req) {
        Semaphore semaphore = getSemaphore(categoryID);
        if (semaphore == null) return;

        Queue<HttpDownloadRequest> queue = mTypeThreadQueue.get(categoryID);
        if(queue == null) return;
        mLock.lock();
        queue.add(req);
        semaphore.release();
        mLock.unlock();
    }


    /** 初始化新增任务类型的[信号量]与[线程队列] */
    private void init() {
        for (Map.Entry<String, Integer> entry : mTypeThreadNum.entrySet()) {
            String categoryID = entry.getKey();
            if (mTypeSemaphore.containsKey(categoryID)) continue;
            if (mTypeThreadQueue.containsKey(categoryID)) continue;
            Semaphore newSemaphore = new Semaphore(0);
            mTypeSemaphore.put(categoryID, newSemaphore);
            Queue<HttpDownloadRequest> queue = new LinkedList<>();
            mTypeThreadQueue.put(categoryID, queue);
        }
    }

    private Semaphore getSemaphore(String categoryID) {
        return mTypeSemaphore.get(categoryID);
    }

    private String getDescription(String categoryID) {
        return mTypeDescription.get(categoryID);
    }

    /** 单传输线程的run方法 */
    private class DownRun implements Runnable {
        String mCategoryID;

        DownRun(String categoryID) {
            mCategoryID = categoryID;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            L.logOnly("下载线程:" + threadName + "开始运行");
            while (!mQuit) {
                if (TextUtils.isEmpty(mCategoryID)) break;

                Semaphore semaphore = getSemaphore(mCategoryID);
                if (semaphore == null) break;

                try {
                    semaphore.acquire();
                    if (mQuit) break;

                    HttpDownloadRequest req = getReq(mCategoryID);
                    if (req == null) continue;

                    doDownload(mCategoryID, req);   // 开始下载
                } catch (Exception e) {
                    L.logOnly("下载线程异常:" + e.toString());
                }
            }
            L.logOnly("下载线程:" + threadName + "停止运行");
        }
    }

    /** 按照传入参数获取指定的下载请求对象 */
    private HttpDownloadRequest getReq(String categoryID) {
        Queue<HttpDownloadRequest> queue = mTypeThreadQueue.get(categoryID);
        if(queue == null) return null;
        mLock.lock();
        HttpDownloadRequest req = queue.poll();
        mLock.unlock();
        return req;
    }

    /** 开始一个传输任务, 回调传输过程中的各种数据 */
    private void doDownload(String categoryID, HttpDownloadRequest req) {
        DownloadTask task = new DownloadTask(categoryID, req);

        if (task.doRealDownload()) {
            L.logOnly("传输完成");
            sendMsg(req, categoryID, req.requestUrl, DOWNLOADSTATUS_SUCCESS, 1);   // 传输成功
        } else {
            L.logOnly("传输失败");
            sendMsg(req, categoryID, req.requestUrl, DOWNLOADSTATUS_FALIURE, 1);   // 传输成功
        }
    }

    /** 传输任务封装类, 传输过程中会不断回调进度  */
    private class DownloadTask implements HttpUtil.onHttpProgressListener {
        private String              mCategoryID;
        private HttpDownloadRequest mReq;

        /** 下载任务 */
        DownloadTask(String categoryID, HttpDownloadRequest req) {
            mCategoryID = categoryID;
            mReq = req;
        }

        /** 真正开始下载(会阻塞线程, 等到传输结束时返回传输结果) */
        private boolean doRealDownload() {
            String okFilePath = PathUtil.PATH_DOWN_OK + mReq.saveFile.getName();
            L.logOnly(okFilePath + "文件已经存在, 且传输完成");
            if(FileUtil.isFileExists(new File(okFilePath))) return true;
            return HttpUtil.downloadFile(mReq, this);
        }

        /** 是否取消 */
        @Override
        public boolean isQuit() {
            return mQuit;
        }

        /** 进度回调 */
        @Override
        public void onProgress(String requestID, String url, long curSize, long allLen) {
            boolean hasCategory = mTypeThreadNum.containsKey(mCategoryID);
            if (mReq.mProgressListener == null || !hasCategory) return;
            sendMsg(mReq, requestID, url, curSize, allLen);
        }
    }

    private void sendMsg(HttpDownloadRequest mReq, String requestID, String url, long curSize, long allLen){
        Bundle bundle = new Bundle();
        bundle.putString("uuid", requestID);
        bundle.putString("url", url);
        bundle.putLong("curSize", curSize);
        bundle.putLong("allLen", allLen);
        Message msg = new Message();
        msg.setData(bundle);
        msg.obj = mReq;
        mHandler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message msg) {
        Object o = msg.obj;
        if(!(o instanceof HttpDownloadRequest)) return false;
        HttpDownloadRequest request = (HttpDownloadRequest)o;
        Bundle bundle 	= msg.getData();
        String uuid 	= Util.getString(bundle, "uuid");
        String url 	    = Util.getString(bundle, "url");
        long curSize 	= Util.getLong(bundle, "curSize");
        long allLen 	= Util.getLong(bundle, "allLen");
        request.mProgressListener.onProgress(uuid, url, curSize, allLen);

        if(curSize == DOWNLOADSTATUS_SUCCESS){  // 下载成功, 将temp文件转移到OK文件夹
            String oldFile = request.saveFile.getAbsolutePath();
            String newFile = PathUtil.PATH_DOWN_OK + request.saveFile.getName();
            FileUtil.renameFile(oldFile, newFile);
        }
        return true;
    }
}
