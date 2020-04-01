package lib.grasp.http.okhttpprogress;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;

import com.rooten.BaApp;
import com.rooten.AppHandler;
import com.rooten.interf.IHandler;

import java.io.File;
import java.io.IOException;

import lib.grasp.helper.interf.LoadListener;
import lib.grasp.util.FileUtil;
import lib.grasp.util.TOAST;
import lib.grasp.widget.LoadingDlgGrasp;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载帮助类（包括界面显示, 多文件/重量级请使用{@link com.rooten.help.filehttp.FileDownloadMgr} ）
 *
 * 使用代码实例：
 * DownLoadHelper loadHelper = new DownLoadHelper(this, mDownUrl, mDownPath);
 *         loadHelper.setLoadListener(new lib.grasp.helper.interf.LoadListener() {
 *             @Override
 *             public void onSuccess() {
 *                 doConfirmToInstall();
 *             }
 *
 *             @Override
 *             public void onFail() {
 *                 doIndicateFailMsg();
 *             }
 *         });
 *         loadHelper.startLoad();
 */
public class DownLoadHelper implements DialogInterface.OnDismissListener, IHandler {
    private BaApp mApp;

    private String mUrl;
    private String mSaveFilePath;

    private boolean isCancel = false;
    private LoadingDlgGrasp mDlg;

    private AppHandler mHandler;
    private AppHandler mLocalHandler;

    private LoadListener mAllLoadListener;  // 什么都听(只能有一个)
    private SparseArray<LoadListener> mLoadListeners = new SparseArray<>(); // 只听自己关心的

    public void setAllLoadListener(LoadListener mAllLoadListener) {
        this.mAllLoadListener = mAllLoadListener;
    }

    private static DownLoadHelper mInstance;

    public static DownLoadHelper getInstance() {
        if(mInstance == null){
            mInstance = new DownLoadHelper();
        }
        return mInstance;
    }

    private DownLoadHelper() {
        mApp = BaApp.getApp();
        mLocalHandler = new AppHandler(this);
        initProgressDlg();
        mHandler = mDlg.getHandler();
    }

    private void initProgressDlg() {
        mDlg = new LoadingDlgGrasp(mApp);
        mDlg.setOnDismissListener(this);
        mDlg.setCanBeCancel(false);
        mDlg.setCancelListener(v -> {
            isCancel = true;
            if (mDlg != null) {
                mDlg.dismiss();
                mDlg = null;
            }
        });
    }

    /** 开始传输 */
    public void startLoad(String mUrl, String mSaveFilePath, LoadListener mLoadListener){
        if(mLoadListeners == null) return;
        int hash = mUrl.hashCode();
        if(mLoadListeners.indexOfKey(hash) < 0){
            mLoadListeners.append(hash, mLoadListener);
        }
        else{
            TOAST.showShort("重复添加传输任务");
            return;
        }
        startLoad(mUrl, mSaveFilePath);
    }

    /** 开始传输 */
    public void startLoad(String mUrl, String mSaveFilePath){
        if(TextUtils.isEmpty(mUrl) || TextUtils.isEmpty(mSaveFilePath)) return;
        this.mUrl = mUrl;
        this.mSaveFilePath = mSaveFilePath;
        mDlg.show();

        //这个是ui线程回调，可直接操作UI
        UIProgressResponseListener mUiProgressResponseListener = new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                if(mHandler == null || isCancel) return;
                Bundle bundle = new Bundle();
                bundle.putString("url", mUrl);
                bundle.putLong("curSize", bytesRead);
                bundle.putLong("allLen" , contentLength);
                Message msg = Message.obtain();
                msg.setData(bundle);
                msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("url", mUrl);
                Message msg = Message.obtain();
                msg.setData(bundle);
                msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS_FAIL;
                mLocalHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response){   // 开始，结束的时候格调一次
                if (!FileUtil.isFileExists(mSaveFilePath)) FileUtil.ensureFileExists(mSaveFilePath);

                Bundle bundle = new Bundle();
                bundle.putString("url", mUrl);
                Message msg = Message.obtain();
                msg.setData(bundle);
                try {
                    FileUtil.saveOkHttpFile(response, new File(mSaveFilePath)); // 从Response里面读取数据，同时维持http读取状态
                    if(mDlg == null || isCancel) return;
                    msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS_SUCC;
                    mLocalHandler.sendMessage(msg);
                }
                catch (Exception e){
                    msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS_FAIL;
                    mLocalHandler.sendMessage(msg);
                }
            }
        };

        //构造请求
        final Request request1 = new Request
                .Builder()
                .url(mUrl)
                .build();

        //包装Response使其支持进度回调
        ProgressHelper
                .addProgressResponseListener(mUiProgressResponseListener)
                .newCall(request1)
                .enqueue(mUiProgressResponseListener);
    }

    @Override
    public boolean handleMessage(Message msg1) {
        if(mDlg == null || isCancel) return true;
        if(mDlg.isShowing()) mDlg.dismiss();

        Bundle bundle = msg1.getData();
        String url = bundle.getString("url");
        if(TextUtils.isEmpty(url)) return true;
        LoadListener listener = mLoadListeners.get(url.hashCode());
        switch (msg1.what){
            case LoadingDlgGrasp.MSG_UPDATE_STATUS:{
                long curSize    = bundle.getLong("curSize");
                long allLen     = bundle.getLong("allLen");
                if(listener != null)            listener.onProgress(url, curSize, allLen);
                if(mAllLoadListener != null)    mAllLoadListener.onProgress(url, curSize, allLen);
                break;
            }
            case LoadingDlgGrasp.MSG_UPDATE_STATUS_SUCC:{
                if(listener != null)            listener.onSuccess(url);
                if(mAllLoadListener != null)    mAllLoadListener.onSuccess(url);
                mLoadListeners.remove(url.hashCode());
                break;
            }
            case LoadingDlgGrasp.MSG_UPDATE_STATUS_FAIL:{
                if(listener != null)            listener.onFail(url);
                if(mAllLoadListener != null)    mAllLoadListener.onFail(url);
                mLoadListeners.remove(url.hashCode());
                break;
            }
        }
        return true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isCancel = true;
    }
}
