package lib.grasp.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import cn.com.rooten.BaApp;
import cn.com.rooten.Constant;
import cn.com.rooten.camera.TakeCamera;
import cn.com.rooten.help.CompressImage;
import cn.com.rooten.util.Utilities;
import lib.grasp.widget.ProgressDlgGrasp;

public class CompressImageTask extends AsyncTask<Void, Void, String> {

    private BaApp mApp;
    private Context     mContext;
    private ProgressDlgGrasp mProgressDlg;
    private boolean     mIsShowProg = false;        // 是否显示prog
    private String      mAvatarURL = "";            // 图片路径
    private boolean     mReturnBase64 = false;      // 是否讲压缩后的图片进行base64编码并返回
    private String      mBase64Code = "";           // 图片Base64编码

    /** 加载结果监听 */
    private onCompressListener mListener;

    public CompressImageTask(BaApp app, Context context, boolean isShowProg, String src) {
        this.mApp           = app;
        this.mContext       = context;
        this.mIsShowProg    = isShowProg;
        this.mAvatarURL     = src;
        if (mIsShowProg) initProgressDlg();
    }

    private void initProgressDlg() {
        mProgressDlg = new ProgressDlgGrasp(mContext);
        mProgressDlg.setCanBeCancel(true);
        mProgressDlg.setCancelListener(v -> {
            if (mProgressDlg != null) {
                mProgressDlg.dismiss();
                mProgressDlg = null;
            }
        });
    }

    @Override
    protected void onPreExecute() {
        if (mIsShowProg && mProgressDlg != null) {
            mProgressDlg.show();
            mProgressDlg.setMessage("正在压缩图片");
            mProgressDlg.setCanBeCancel(true);
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        if (!Utilities.isFileExists(mAvatarURL)) return "";
        String mCompressImageURL = CompressImage.compress(TakeCamera.AVATAR_IMAGE_SIZE, 100, mAvatarURL, mApp.getUserData().getCompressAvatarPath(), Constant.SUFFIX_AVATAR_NAME);
        String result = TextUtils.isEmpty(mCompressImageURL) ? mAvatarURL : mCompressImageURL;
        if (mReturnBase64) mBase64Code = Utilities.fileToBase64(new File(result));
        return result;
    }


    @Override
    protected void onPostExecute(String result1) {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }

        if (TextUtils.isEmpty(result1)) {
            noti("图片压缩失败");
            return;
        }

        if (mListener != null) {
            mListener.onCompressFinished(result1, mBase64Code);
        }
    }

    private void noti(String strMsg) {
        if(mContext == null) return;
        Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
    }

    public void setListener(onCompressListener mListener) {
        this.mListener = mListener;
    }

    /** 是否进行base64编码,并返回 */
    public void setReturnBase64(boolean mReturnBase64) {
        this.mReturnBase64 = mReturnBase64;
    }

    public interface onCompressListener{
        void onCompressFinished(String picCompressPath, String base64Coder);
    }
}
