package lib.grasp.helper.validatecode;

import android.app.Activity;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.AppHandler;
import com.rooten.interf.IHandler;
import lib.grasp.R;

/** 验证码帮助类 */
public class SmsClockHelper implements IHandler {

    private final int MSG_UPDATE_TIME = 1;

    private Activity mAct;
    private BaApp mApp;

    private Timer mTimer	    = null;
    private Date mStartTime;
    private AppHandler mHandler    = new AppHandler(this);

    private TextView mBtnGetConfirm;

    private SmsSendHelper mSmsHelper;

    /** 手机号码 */
    private String mTargetTel;

    /** 验证码帮助类 */
    /**
     * 验证码帮助类
     * @param act Activity
     * @param hostTextView 宿主TextView(就是倒计时的那个)
     * @param url 验证码获取的url
     */
    public SmsClockHelper(Activity act, TextView hostTextView, String url){
        mAct            = act;
        mBtnGetConfirm  = hostTextView;
        init(url);
    }

    private void init(String url){
        mSmsHelper = new SmsSendHelper(mAct, this, url);
    }

    /** 发送验证码 */
    public void doGetConfirmCode(String tel){
        doGetConfirmCode(tel, null);
    }

    /** 发送验证码 */
    public void doGetConfirmCode(String tel, HashMap<String, String> arg){
        mTargetTel = tel;
        mSmsHelper.sendSms(tel, arg);

        if(mBtnGetConfirm != null){
            mBtnGetConfirm.setEnabled(false);
            mBtnGetConfirm.setText("正在发送");
            mBtnGetConfirm.setTextColor(Color.LTGRAY);
        }
    }

    /** 验证码发送回调 */
    public void doSendResult(boolean sendResult){
        if(sendResult){ // 发送成功
            startClocking();
        }
        else{           // 发送失败
            stopClocking();
        }
    }

    private void startClocking(){

        mStartTime = Calendar.getInstance().getTime();
        TimerTask timeTask = new TimerTask()
        {
            @Override
            public void run()
            {
                // 开始计时
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        };
        mTimer = new Timer();
        mTimer.schedule(timeTask, 1000, 1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_TIME:{
                Date now = Calendar.getInstance().getTime();
                long nSeconds = (now.getTime() - mStartTime.getTime()) / 1000;
                if(nSeconds > Constant.YZM_VAILDATE_TIME) {
                    stopClocking();
                    break;
                }
                long timeLeft = Constant.YZM_VAILDATE_TIME - nSeconds;
                if(mBtnGetConfirm != null) mBtnGetConfirm.setText(String.valueOf(timeLeft).concat("秒后重新获取"));
                break;
            }
        }
        return true;
    }

    private void stopClocking(){
        if(mBtnGetConfirm != null){
            mBtnGetConfirm.setText("获取验证码");
            mBtnGetConfirm.setEnabled(true);
            mBtnGetConfirm.setTextColor(mApp.getResources().getColor(R.color.colorPrimary));
        }

        if(mTimer == null) return;
        mTimer.cancel();
        mTimer = null;
    }

    /** 发起验证(异步) */
    public void doVerifyAsync(OnSmsVerifyListener listener, String code){
        mSmsHelper.doVerifyAsync(listener, mTargetTel, code);
    }

    /** 发起验证(同步) */
    public boolean doVerify(String code){
        return mSmsHelper.doVerify(code);
    }

    /** 解注册，防止00M */
    public void doRelease(){
        mSmsHelper.unRegisterEventHandler();
    }


    /** 验证码，验证结果监听 */
    public interface OnSmsVerifyListener {
        void onVerifyResult(boolean result);

    }
}
