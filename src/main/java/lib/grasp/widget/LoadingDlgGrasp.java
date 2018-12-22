package lib.grasp.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.rooten.ctrl.widget.RoundProgressBar;
import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;
import cn.com.rooten.help.filehttp.HttpUploadRequest;
import cn.com.rooten.help.filehttp.HttpUtil;
import cn.com.rooten.util.Util;
import lib.grasp.R;
import lib.grasp.util.L;
import lib.grasp.util.NumberUtil;

import static cn.com.rooten.help.filehttp.FileDownloadMgr.DownloadStatus_FALIURE;
import static cn.com.rooten.help.filehttp.FileDownloadMgr.DownloadStatus_SUCCESS;

/** 带进度回调的弹窗 */
public class LoadingDlgGrasp extends AlertDialog implements IHandler, View.OnClickListener {
    private final int MSG_UPDATE_TIME   = 1;
    public static final int MSG_UPDATE_STATUS = 2;
    public static final int MSG_UPDATE_STATUS_SUCC = 3;
    public static final int MSG_UPDATE_STATUS_FAIL = 4;

    private Date        mStartTime;
    private TextView    tvContent;
    private TextView    mTextTime;

    private View        line;

    private LinearLayout ll;
    private TextView    tvCancel;

    private RoundProgressBar mProgressBar;

    /** 是否显示"取消"按钮 */
    private boolean mIsShowBtnCancel = false;

    /** 是否点击提示框外部取消 */
    private boolean mEnableCancel = true;

    private Timer mTimer = null;
    private View.OnClickListener mListener = null;

    public LoadingDlgGrasp(Context context) {
        super(context, R.style.dialog_grasp);
    }

    private AppHandler mHandler = new AppHandler(this);

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_UPDATE_TIME) {
            Date time = Calendar.getInstance().getTime();
            long nSeconds = (time.getTime() - mStartTime.getTime()) / 1000;
            if(nSeconds <= 10) return true;
            mTextTime.setVisibility(View.VISIBLE);
            final String strTime = String.format(Locale.getDefault(),
                    "%02d:%02d:%02d",
                    nSeconds / 3600,
                    (nSeconds / 60) % 60,
                    nSeconds % 60);
            mTextTime.setText(strTime);
        }
        else if (msg.what == MSG_UPDATE_STATUS) {
            Bundle bundle = msg.getData();
            if(bundle == null || mProgressBar == null) return true;
            long curSize    = Util.getLong(bundle, "curSize");
            long allLen     = Util.getLong(bundle, "allLen");
            int newProgress = NumberUtil.getProgress(curSize, allLen);
            if(mProgressBar.getProgress() == newProgress) return true;
            mProgressBar.setProgress(newProgress);
            L.logOnly("下载进度", NumberUtil.getProgress(curSize, allLen));
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_loading);
        setCanceledOnTouchOutside(mEnableCancel);
        setCancelable(mEnableCancel);

        mStartTime  = Calendar.getInstance().getTime();
        tvContent   = findViewById(R.id.content);
        mTextTime   = findViewById(R.id.time);

        line        = findViewById(R.id.horizontal_line);

        ll          = findViewById(R.id.ll_btn);
        tvCancel    = findViewById(R.id.cancel);

        mProgressBar= findViewById(R.id.progress_bar);

        tvCancel.setOnClickListener(this);
        ll.setVisibility(mIsShowBtnCancel ? View.VISIBLE : View.GONE);
        line.setVisibility(mIsShowBtnCancel ? View.VISIBLE : View.GONE);

        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        };

        mTimer = new Timer();
        mTimer.schedule(timeTask, 1000, 1000);
    }

    public void setMessage(final String strMsg) {
        if (tvContent != null) tvContent.setText(strMsg);
    }

    public void setCancelListener(View.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (mEnableCancel) {
            if(mListener != null) mListener.onClick(tvCancel);
            super.onBackPressed();
        }
    }

    @Override
    public void dismiss() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        // 点击取消按钮
        if(mListener != null) mListener.onClick(tvCancel);
        this.dismiss();
    }

    public void setCanBeCancel(boolean cancelable) {
        mEnableCancel = cancelable;
    }

    public void setBtnCancelVisible(boolean isVisible) {
        mIsShowBtnCancel = isVisible;
    }

    public AppHandler getHandler() {
        return mHandler;
    }
}
