package lib.grasp.helper;


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
import com.rooten.frame.AppHandler;
import com.rooten.frame.IHandler;
import lib.grasp.R;

/** 验证码 */
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

    public SmsClockHelper(BaApp app, Activity act, TextView mMonitor, String url){
        mAct            = act;
        mApp            = app;
        mBtnGetConfirm  = mMonitor;
        init(url);
    }

    private void init(String url){
        mSmsHelper = new SmsSendHelper(mApp, mAct, this, url);

    }

    /** 发送验证码 */
    public void doGetConfirmCode(String tel){
        mTargetTel = tel;
        mSmsHelper.sendSms(tel);

        mBtnGetConfirm.setEnabled(false);
        mBtnGetConfirm.setText("正在发送");
        mBtnGetConfirm.setTextColor(Color.LTGRAY);
    }

    /** 发送验证码 */
    public void doGetConfirmCode(String tel, HashMap<String, String> arg){
        mTargetTel = tel;
        mSmsHelper.sendSms(tel, arg);

        mBtnGetConfirm.setEnabled(false);
        mBtnGetConfirm.setText("正在发送");
        mBtnGetConfirm.setTextColor(Color.LTGRAY);
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
                mBtnGetConfirm.setText((Constant.YZM_VAILDATE_TIME - nSeconds) + "秒后重新获取");
                break;
            }
        }
        return true;
    }

    private void stopClocking(){
        mBtnGetConfirm.setText("获取验证码");
        mBtnGetConfirm.setEnabled(true);
        mBtnGetConfirm.setTextColor(mApp.getResources().getColor(R.color.colorPrimary));

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















    //----------------------------------------------------------------------------工具
    /** 判断手机号码是否合理 */
    public static boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        return false;
    }

    private static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }
    private static boolean isMobileNO(String mobileNums) {
        String telRegex = "[1]\\d{10}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /** 验证码，验证结果监听 */
    public interface OnSmsVerifyListener {
        void onVerifyResult(boolean result);

    }
}
