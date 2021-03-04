package lib.grasp.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.rooten.AppHandler;
import com.rooten.interf.IHandler;
import lib.grasp.R;
import lib.grasp.util.L;

/** 一直转圈圈的Dialog */
public class ProgressDlgGrasp extends AlertDialog implements IHandler, View.OnClickListener {
    private final int MSG_UPDATE_TIME = 1;

    private Date        mStartTime;
    private TextView    tvContent;
    private TextView    mTextTime;

    private View        line;

    private LinearLayout ll;

    /** 是否显示"取消"按钮 */
    private boolean mIsShowBtnCancel = false;

    /** 是否点击提示框外部取消 */
    private boolean mEnableCancel = true;

    private Timer mTimer = null;
    private View.OnClickListener mListener = null;

    public ProgressDlgGrasp(Context context) {
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
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertdialog_progress);
        setCanceledOnTouchOutside(false);
        setCancelable(mEnableCancel);

        mStartTime  = Calendar.getInstance().getTime();
        tvContent   = findViewById(R.id.content);
        mTextTime   = findViewById(R.id.time);

        line        = findViewById(R.id.horizontal_line);

        ll          = findViewById(R.id.ll_btn);

        ll.setOnClickListener(this);
        ll.setVisibility(mEnableCancel && mIsShowBtnCancel ? View.VISIBLE : View.GONE);
        line.setVisibility(mEnableCancel && mIsShowBtnCancel ? View.VISIBLE : View.GONE);

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
            if(mListener != null) mListener.onClick(ll);
            super.onBackPressed();
        }
    }

    @Override
    public void dismiss() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        try {
            super.dismiss();
        }catch (Exception e){
            L.log(e);
        }
    }

    @Override
    public void onClick(View v) {
        // 点击取消按钮
        if(mListener != null) mListener.onClick(ll);
        this.dismiss();
    }

    public void setCanBeCancel(boolean cancelable) {
        mEnableCancel = cancelable;
    }

    public void setBtnCancelVisible(boolean isVisible) {
        mIsShowBtnCancel = isVisible;
    }
}
