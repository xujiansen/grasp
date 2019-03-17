package lib.grasp.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import cn.com.rooten.BaApp;
import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;
import lib.grasp.http.okhttpprogress.ProgressHelper;
import lib.grasp.http.okhttpprogress.UIProgressResponseListener;
import lib.grasp.util.FileUtil;
import lib.grasp.widget.LoadingDlgGrasp;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载帮助类（包括界面显示）
 *
 * 使用代码实例：
 * DownLoadHelper loadHelper = new DownLoadHelper(this, mDownUrl, mDownPath);
 *         loadHelper.setLoadListener(new lib.grasp.helper.LoadListener() {
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
    private Context mCtx;
    private BaApp mApp;

    private String mUrl;
    private String mSaveFilePath;

    private boolean isCancel = false;
    private LoadingDlgGrasp mDlg;

    private AppHandler mHandler;
    private AppHandler mLocalHandler;

    private LoadListener mLoadListener;

    public void setLoadListener(LoadListener mLoadListener) {
        this.mLoadListener = mLoadListener;
    }

    public DownLoadHelper(Context mCtx, String mUrl, String mSaveFilePath) {
        this.mCtx = mCtx;
        this.mUrl = mUrl;
        this.mSaveFilePath = mSaveFilePath;

        mApp = (BaApp) mCtx.getApplicationContext();
        mLocalHandler = new AppHandler(this);
        initProgressDlg();
        mHandler = mDlg.getHandler();
    }

    private void initProgressDlg() {
        mDlg = new LoadingDlgGrasp(mCtx);
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
    public void startLoad(){
        if(TextUtils.isEmpty(mUrl) || TextUtils.isEmpty(mSaveFilePath)) return;
        mDlg.show();

        //这个是ui线程回调，可直接操作UI
        UIProgressResponseListener mUiProgressResponseListener = new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                if(mHandler == null || isCancel) return;
                Bundle bundle = new Bundle();
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
                Message msg = Message.obtain();
                msg.setData(bundle);
                msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS_FAIL;
                mLocalHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response){   // 开始，结束的时候格调一次
                if (!FileUtil.isFileExists(mSaveFilePath)) FileUtil.ensureFileExists(mSaveFilePath);

                Bundle bundle = new Bundle();
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
        // 传输完成
        if(mDlg == null || isCancel) return true;
        mDlg.dismiss();
        mDlg = null;
        switch (msg1.what){
            case LoadingDlgGrasp.MSG_UPDATE_STATUS_SUCC:{
                if(mLoadListener != null) mLoadListener.onSuccess();
                break;
            }
            case LoadingDlgGrasp.MSG_UPDATE_STATUS_FAIL:{
                if(mLoadListener != null) mLoadListener.onFail();
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