package cn.com.rooten.help.filehttp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.com.rooten.util.Utilities;
import lib.grasp.util.L;

public class FileDownloadMgr {
    protected Lock mLock = new ReentrantLock();
    protected boolean mQuit = false;

    private static final int DOWNLOAD_MAX = 5; // 最大下载线程数

    // 下载参数
    private Map<String, Integer> mDownCategory = new HashMap<>();  // 下载的种类，每个种类占用的线程数
    private Map<String, String> mDownDescription = new HashMap<>();  // 下载的种类，每个种类的说明
    private Map<String, Semaphore> mDownSemaphore = new HashMap<>();  // 下载种类的信号量
    private Map<String, Queue<HttpDownloadRequest>> mDownQueue = new HashMap<>();  // 下载队列

    public static final long DownloadStatus_SUCCESS = -1;
    public static final long DownloadStatus_FALIURE = -2;
    public static final long DownloadStatus_CANCLED = -3;

    // 注册下载类型，返回注册成功的id，否则返回空字符串
    public String registerCategory(String description, int threadNum) {
        if (threadNum <= 0 || threadNum > 5) return "";

        int hasRegisterThreadNum = 0;
        for (Map.Entry<String, Integer> entry : mDownCategory.entrySet()) {
            hasRegisterThreadNum += entry.getValue();
        }

        if (hasRegisterThreadNum >= 5) return "";

        int left = DOWNLOAD_MAX - hasRegisterThreadNum;
        if (threadNum > left) return "";

        String categoryID = UUID.randomUUID().toString();
        mDownCategory.put(categoryID, threadNum);
        mDownDescription.put(categoryID, description);

        init();

        return categoryID;
    }

    public boolean unRegisterCategory(String categoryID) {
        if (Utilities.isEmpty(categoryID)) return false;
        if (!mDownCategory.containsKey(categoryID)) return false;

        mDownCategory.remove(categoryID);
        mDownDescription.remove(categoryID);
        mDownSemaphore.remove(categoryID);
        mDownQueue.remove(categoryID);

        init();

        return true;
    }

    private HttpDownloadRequest getReq(String categoryID) {
        Queue<HttpDownloadRequest> queue = mDownQueue.get(categoryID);
        mLock.lock();
        HttpDownloadRequest req = queue.poll();
        mLock.unlock();
        return req;
    }

    public void addDownWork(String categoryID, HttpDownloadRequest req) {
        Semaphore semaphore = getSemaphore(categoryID);
        if (semaphore == null) return;

        Queue<HttpDownloadRequest> queue = mDownQueue.get(categoryID);
        mLock.lock();
        queue.add(req);
        semaphore.release();
        mLock.unlock();
    }

    private void init() {
        for (Map.Entry<String, Integer> entry : mDownCategory.entrySet()) {
            String categoryID = entry.getKey();
            if (mDownSemaphore.containsKey(categoryID)) continue;
            if (mDownQueue.containsKey(categoryID)) continue;

            Semaphore newSemaphore = new Semaphore(0);
            mDownSemaphore.put(categoryID, newSemaphore);

            Queue<HttpDownloadRequest> queue = new LinkedList<>();
            mDownQueue.put(categoryID, queue);
        }
    }

    public void startDownload() {
        for (Map.Entry<String, Integer> entry : mDownCategory.entrySet()) {
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

    public void stopDownload() {
        // 置退出标签
        mQuit = true;
        for (Map.Entry<String, Integer> entry : mDownCategory.entrySet()) {
            String categoryID = entry.getKey();
            int threadNum = entry.getValue();

            Semaphore semaphore = getSemaphore(categoryID);
            if (semaphore == null) continue;
            semaphore.release(threadNum);
        }

        mDownCategory.clear();
        mDownDescription.clear();
        mDownSemaphore.clear();
        mDownQueue.clear();
    }

    protected Semaphore getSemaphore(String categoryID) {
        return mDownSemaphore.get(categoryID);
    }

    protected String getDescription(String categoryID) {
        return mDownDescription.get(categoryID);
    }

    private class DownRun implements Runnable {
        public String mCategoryID = "";

        public DownRun(String categoryID) {
            mCategoryID = categoryID;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            L.logOnly(FileDownloadMgr.class, "DownRun>>run::start", threadName + "开始运行");

            while (!mQuit) {
                if (Utilities.isEmpty(mCategoryID)) break;

                Semaphore semaphore = getSemaphore(mCategoryID);
                if (semaphore == null) break;

                try {
                    semaphore.acquire();
                    if (mQuit) break;

                    HttpDownloadRequest req = getReq(mCategoryID);
                    if (req == null) continue;

                    // 开始下载
                    doDownload(mCategoryID, req);
                } catch (Exception e) {
                    L.logOnly(FileDownloadMgr.class, "DownRun>>run", e.toString());
                }
            }

            L.logOnly(FileDownloadMgr.class, "DownRun>>run::stop", threadName + "停止运行");
        }
    }

    public void doDownload(String categoryID, HttpDownloadRequest req) {
        DownloadTask task = new DownloadTask(categoryID, req);

        if (task.onDownload()) {
            req.progress.onProgress(req.reqId, 0, DownloadStatus_SUCCESS);
        } else {
            req.progress.onProgress(req.reqId, 0, DownloadStatus_FALIURE);
        }
    }

    private class DownloadTask implements HttpUtil.onHttpProgressListener {
        private String mCategoryID;
        private HttpDownloadRequest mReq;

        public DownloadTask(String categoryID, HttpDownloadRequest req) {
            mCategoryID = categoryID;
            mReq = req;
        }

        private boolean onDownload() {
            return HttpUtil.downloadFile(mReq, this);
        }

        @Override
        public boolean isQuit() {
            return mQuit;
        }

        @Override
        public void onProgress(String requestID, long curSize, long allLen) {
            boolean hasCategory = mDownCategory.containsKey(mCategoryID);
            if (mReq.progress == null || !hasCategory) return;
            mReq.progress.onProgress(requestID, curSize, allLen);
        }

        @Override
        public void onResMsg(String requestID, String res) {

        }
    }
}
