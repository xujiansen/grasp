package lib.grasp.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.camera.TakeCamera;
import com.rooten.help.CompressImage;

import lib.grasp.util.FileUtil;
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
    private ImageSingleSelectListener mListener;

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
        if (!FileUtil.isFileExists(mAvatarURL)) return "";
        String mCompressImageURL = CompressImage.compress(TakeCamera.AVATAR_IMAGE_SIZE, 100, mAvatarURL, mApp.getUserData().getCompressAvatarPath(), Constant.SUFFIX_AVATAR_NAME);
        String result = TextUtils.isEmpty(mCompressImageURL) ? mAvatarURL : mCompressImageURL;
        if (mReturnBase64) mBase64Code = FileUtil.fileToBase64(new File(result));
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
            mListener.onSelected(result1, mBase64Code);
        }
    }

    private void noti(String strMsg) {
        if(mContext == null) return;
        Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
    }

    public void setListener(ImageSingleSelectListener mListener) {
        this.mListener = mListener;
    }

    /** 是否进行base64编码,并返回 */
    public void setReturnBase64(boolean mReturnBase64) {
        this.mReturnBase64 = mReturnBase64;
    }

}
