package com.rooten.help.filehttp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.rooten.util.Utilities;
import lib.grasp.util.L;

public class FileUploadMgr {
    protected Lock mLock = new ReentrantLock();
    protected boolean mQuit = false;

    private static int UPLOAD_MAX = 2; // 最大上传线程数

    // 上传参数
    private Map<String, Integer> mUploadCategory = new HashMap<>();  // 上传的种类，每个种类占用的线程数
    private Map<String, String> mUploadDescription = new HashMap<>();  // 上传的种类，每个种类的说明
    private Map<String, Semaphore> mUploadSemaphore = new HashMap<>();  // 上传种类的信号量
    private Map<String, Queue<HttpUploadRequest>> mUploadQueue = new HashMap<>();  // 上传队列

    public static final long UploadStatus_SUCCESS = -1;
    public static final long UploadStatus_FALIURE = -2;
    public static final long UploadStatus_CANCLED = -3;

    /** 上传线程数量 */
    public FileUploadMgr(int threadNum) {
        UPLOAD_MAX = threadNum;
    }

    /**
     * 注册上传类型，返回注册成功的id，否则返回空字符串
     * @param description   线程名字
     * @param threadNum     线程数量
     * @return 返回新增上传下载任务的凭证ID
     */
    public String registerCategory(String description, int threadNum) {
        if (threadNum <= 0 || threadNum > UPLOAD_MAX) return "";

        int hasRegisterThreadNum = 0;
        for (Map.Entry<String, Integer> entry : mUploadCategory.entrySet()) {
            hasRegisterThreadNum += entry.getValue();
        }

        if (hasRegisterThreadNum >= UPLOAD_MAX) return "";

        int left = UPLOAD_MAX - hasRegisterThreadNum;
        if (threadNum > left) return "";

        String categoryID = UUID.randomUUID().toString();
        mUploadCategory.put(categoryID, threadNum);
        mUploadDescription.put(categoryID, description);

        init();

        return categoryID;
    }

    private HttpUploadRequest getReq(String categoryID) {
        Queue<HttpUploadRequest> queue = mUploadQueue.get(categoryID);
        mLock.lock();
        HttpUploadRequest req = queue.poll();
        mLock.unlock();
        return req;
    }

    public void addUploadWork(String categoryID, HttpUploadRequest req) {
        Semaphore semaphore = getSemaphore(categoryID);
        if (semaphore == null) return;

        mLock.lock();
        getQueue(categoryID).add(req);
        semaphore.release();
        mLock.unlock();
    }

    private Queue<HttpUploadRequest> getQueue(String categoryID) {
        Queue<HttpUploadRequest> queue = mUploadQueue.get(categoryID);
        if (queue == null) {
            queue = new LinkedList<>();
            mUploadQueue.put(categoryID, queue);
        }
        return queue;
    }

    private void init() {
        for (Map.Entry<String, Integer> entry : mUploadCategory.entrySet()) {
            String categoryID = entry.getKey();
            if (mUploadSemaphore.containsKey(categoryID)) continue;
            if (mUploadQueue.containsKey(categoryID)) continue;

            Semaphore newSemaphore = new Semaphore(0);
            mUploadSemaphore.put(categoryID, newSemaphore);
        }
    }

    public void startUpload() {
        for (Map.Entry<String, Integer> entry : mUploadCategory.entrySet()) {
            String categoryID = entry.getKey();
            int threadNum = entry.getValue();
            String description = getDescription(categoryID);

            for (int i = 0; i < threadNum; i++) {
                String threadName = description + "线程" + (i + 1);
                Thread t = new Thread(new UploadRun(categoryID), threadName);
                t.start();
            }
        }
    }

    public void stopUpload() {
        // 置退出标签
        mQuit = true;

        for (Map.Entry<String, Integer> entry : mUploadCategory.entrySet()) {
            String categoryID = entry.getKey();
            int threadNum = entry.getValue();

            Semaphore semaphore = getSemaphore(categoryID);
            if (semaphore == null) continue;
            semaphore.release(threadNum);
        }

        mUploadCategory.clear();
        mUploadDescription.clear();
        mUploadCategory.clear();
        mUploadQueue.clear();
    }

    protected Semaphore getSemaphore(String categoryID) {
        return mUploadSemaphore.get(categoryID);
    }

    protected String getDescription(String categoryID) {
        return mUploadDescription.get(categoryID);
    }

    private class UploadRun implements Runnable {
        public String mCategoryID = "";

        public UploadRun(String categoryID) {
            mCategoryID = categoryID;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            L.logOnly("UploadRun>>run::start"+threadName + "开始运行");

            while (!mQuit) {
                if (Utilities.isEmpty(mCategoryID)) break;

                Semaphore semaphore = getSemaphore(mCategoryID);
                if (semaphore == null) break;

                try {
                    semaphore.acquire();
                    if (mQuit) break;

                    HttpUploadRequest req = getReq(mCategoryID);
                    if (req == null) continue;

                    // 开始上传
                    doUpload(mCategoryID, req);
                } catch (Exception e) {
                    L.logOnly("UploadRun>>run" + e.toString());
                }
            }

            L.logOnly("UploadRun>>run::stop" + threadName + "停止运行");
        }
    }

    private void doUpload(String categoryID, HttpUploadRequest req) {
        UploadTask task = new UploadTask(categoryID, req);
        if (task.onUpload()) {
            req.progress.onProgress(req.reqId, req.requestUrl, 0, UploadStatus_SUCCESS);
        } else {
            req.progress.onProgress(req.reqId, req.requestUrl, 0, UploadStatus_FALIURE);
        }
    }

    private class UploadTask implements HttpUtil.onHttpProgressListener {
        private String mCategoryID;
        private HttpUploadRequest mReq;

        public UploadTask(String categoryID, HttpUploadRequest req) {
            mCategoryID = categoryID;
            mReq = req;
        }

        private boolean onUpload() {
            return HttpUtil.uploadFile(mReq, this);
        }

        @Override
        public boolean isQuit() {
            return mQuit;
        }

        @Override
        public void onProgress(String requestID, String url, long curSize, long allLen) {
            boolean hasCategory = mUploadCategory.containsKey(mCategoryID);
            if (mReq.progress == null || !hasCategory) return;
            mReq.progress.onProgress(mReq.reqId, mReq.requestUrl, curSize, allLen);
        }
    }
}
