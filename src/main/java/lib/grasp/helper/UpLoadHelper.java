package lib.grasp.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.rooten.BaApp;
import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;
import lib.grasp.http.okhttpprogress.ProgressHelper;
import lib.grasp.http.okhttpprogress.ProgressRequestBody;
import lib.grasp.http.okhttpprogress.UIProgressRequestListener;
import lib.grasp.util.FileUtil;
import lib.grasp.util.NumberUtil;
import lib.grasp.widget.LoadingDlgGrasp;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传帮助类
 *
 * 使用代码实例：
 * String localFilePath = mLocalFilePath;
 *         List<String> list = new ArrayList<>();
 *         list.add(localFilePath);
 *
 *         UpLoadHelper loadHelper = new UpLoadHelper(this, mUpUrl, list);
 *         loadHelper.setLoadListener(new lib.grasp.helper.LoadListener() {
 *             @Override
 *             public void onSuccess() {
 *                 MessageBoxGrasp.infoMsg(TestActivity.this, "上传完成");
 *             }
 *
 *             @Override
 *             public void onFail() {
 *                 MessageBoxGrasp.infoMsg(TestActivity.this, "上传失败");
 *             }
 *         });
 *         loadHelper.startLoad();
 */
public class UpLoadHelper implements DialogInterface.OnDismissListener, IHandler {
    private Context mCtx;
    private BaApp mApp;

    private String mUrl;
    private List<String> mResFilePaths;

    private boolean isCancel = false;
    private LoadingDlgGrasp mDlg;

    private AppHandler mHandler;
    private AppHandler mLocalHandler;

    private LoadListener mLoadListener;

    public void setLoadListener(LoadListener mLoadListener) {
        this.mLoadListener = mLoadListener;
    }

    public UpLoadHelper(Context mCtx, String mUrl, List<String> resFilePaths) {
        this.mCtx = mCtx;
        this.mUrl = mUrl;
        this.mResFilePaths = resFilePaths;

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
        if(TextUtils.isEmpty(mUrl) || mResFilePaths == null || mResFilePaths.size() == 0) return;
        mDlg.show();

        List<File> files = new ArrayList<>();
        for(String str : mResFilePaths){
            if(!FileUtil.isFileExists(str)) continue;
            files.add(new File(str));
        }

        final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
                System.out.println("------onUIRequestProgress:" + NumberUtil.getProgress(bytesWrite, contentLength));

                if(mHandler == null || isCancel) return;
                Bundle bundle = new Bundle();
                bundle.putLong("curSize", bytesWrite);
                bundle.putLong("allLen" , contentLength);
                Message msg = Message.obtain();
                msg.setData(bundle);
                msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("------onFailure:" + e);

                Bundle bundle = new Bundle();
                Message msg = Message.obtain();
                msg.setData(bundle);
                msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS_FAIL;
                mLocalHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("------onResponse:" + response);

                if(mDlg == null || isCancel) return;
                Bundle bundle = new Bundle();
                Message msg = Message.obtain();
                msg.setData(bundle);
                msg.what = LoadingDlgGrasp.MSG_UPDATE_STATUS_SUCC;
                mLocalHandler.sendMessage(msg);
            }
        };

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for(File file : files){
            builder.addPart(RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }
        MultipartBody multipartBody = builder.build();

        ProgressRequestBody progressRequestBody = ProgressHelper
                .addProgressRequestListener(multipartBody, uiProgressRequestListener);

        final Request request = new Request
                .Builder()
                .url(mUrl)
                .post(progressRequestBody)
                .build();

        new OkHttpClient()
                .newCall(request)
                .enqueue(uiProgressRequestListener);
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
