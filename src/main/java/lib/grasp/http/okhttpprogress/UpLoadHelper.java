package lib.grasp.http.okhttpprogress;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.List;

import lib.grasp.helper.interf.LoadListener;
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
 * 上传帮助类（包括界面显示, 多文件/重量级请使用{@link com.rooten.help.filehttp.FileUploadMgr} ）
 *
 * 使用代码实例：
 *          String localFilePath = "/sdcard/test.txt";
 *          List<String> list = new ArrayList<>();
 *          list.add(localFilePath);
 *          UpLoadHelper loadHelper = new UpLoadHelper(activity);
 *          loadHelper.setAllLoadListener(new LoadListener() {
 *              @Override
 *              public void onSuccess(String url) {
 *                  if(activity != null) MessageBoxGrasp.infoMsg(activity, "上传完成");
 *              }
 *
 *              @Override
 *              public void onFail(String url) {
 *                  if(activity != null)MessageBoxGrasp.infoMsg(activity, "上传失败");
 *              }
 *
 *              @Override
 *              public void onProgress(String url, long curSize, long allSize) {
 *
 *              }
 *          });
 *          loadHelper.startLoad("http://192.168.....", list);
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

    private LoadListener mAllLoadListener;  // 什么都听(只能有一个)
    private SparseArray<LoadListener> mLoadListeners = new SparseArray<>(); // 只听自己关心的

    public void setAllLoadListener(LoadListener mLoadListener) {
        this.mAllLoadListener = mLoadListener;
    }

    public UpLoadHelper(Context mCtx) {
        this.mCtx = mCtx;

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


//    /** 开始传输 */
//    public void startLoad(String mUrl, List<String> resFilePaths, LoadListener mLoadListener){
//        if(mLoadListeners == null) return;
//        int hash = mUrl.hashCode();
//        if(mLoadListeners.indexOfKey(hash) < 0){
//            mLoadListeners.append(hash, mLoadListener);
//        }
//        else{
//            TOAST.showShort("重复添加传输任务");
//            return;
//        }
//        startLoad(mUrl, resFilePaths);
//    }

    /** 开始传输 */
    public void startLoad(String url, List<String> resFilePaths){
        if(TextUtils.isEmpty(url) || mResFilePaths == null || mResFilePaths.size() == 0) return;
        this.mUrl = url;
        this.mResFilePaths = resFilePaths;
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
                bundle.putString("url", mUrl);
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
                bundle.putString("url", mUrl);
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
                bundle.putString("url", mUrl);
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
