package lib.grasp.helper;

import android.app.Activity;
import android.text.TextUtils;

import com.rooten.BaApp;

import java.util.HashMap;

import lib.grasp.http.ObjResponse;
import lib.grasp.http.okhttp.OkHttpHelper;
import lib.grasp.http.okhttp.ResponseCallback;
import lib.grasp.util.TOAST;

import static lib.grasp.http.okhttp.OkHttpHelper.GET;

/** 验证码 */
public class SmsSendHelper {

    private BaApp mApp;
    private Activity mAct;
    private SmsClockHelper  mCodeHelper;
    private String mCode;
    private String mUrl;

    public SmsSendHelper(BaApp app, Activity act, SmsClockHelper helper, String url) {
        this.mAct = act;
        this.mCodeHelper = helper;
        this.mApp = app;
        this.mUrl = url;
    }

    /** 发送验证码
     * 注意网络 */
    public void sendSms(String tel){
        if(TextUtils.isEmpty(tel) || !SmsClockHelper.judgePhoneNums(tel)) return;
        doGetSms(getRequestParam(tel), new HashMap<>());
    }

    /** 发送验证码
     * 注意网络 */
    public void sendSms(String tel, HashMap<String, String> arg){
        if(TextUtils.isEmpty(tel) || !SmsClockHelper.judgePhoneNums(tel)) return;
        if (arg == null) arg = new HashMap<>();
        doGetSms(getRequestParam(tel), arg);
    }

    /** 打包参数 */
    private HashMap<String, String> getRequestParam(String tel){
        HashMap<String, String> param = new HashMap<>();
        param.put("mobile"        , tel    );
        return param;
    }

    /** 具体上传 */
    private void doGetSms(HashMap<String, String> map, HashMap<String, String> headMap){
        OkHttpHelper.with(mAct)
                .setURL(mUrl)
                .setMethod(GET)
                .setHeadParam(headMap)
                .setParam(map)
                .setIsShowProg(false, "正在加载")
                .setSwip(null)
                .execute(new ResponseCallback<ObjResponse<String>>() {
                    @Override
                    public void onSuccess(ObjResponse<String> response) {
                        if (response == null || response.code != 0) {
                            TOAST.showShort(mAct, "获取失败" + ((response != null && !TextUtils.isEmpty(response.msg)) ? "," + response.msg : ""));
                            mCodeHelper.doSendResult(false);
                            return;
                        }
                        TOAST.showShort(mAct, "获取成功" + ((!TextUtils.isEmpty(response.msg)) ? ("," + response.msg) : ""));
                        mCodeHelper.doSendResult(true);
                        mCode = response.data;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        TOAST.showShort(mAct, "获取失败");
                        mCodeHelper.doSendResult(false);
                    }
                });
    }

    /** 发起验证(异步) */
    public void doVerifyAsync(SmsClockHelper.OnSmsVerifyListener listener, String tel, String code){
        if(TextUtils.isEmpty(code) || !TextUtils.equals(code, mCode)) listener.onVerifyResult(false);
        else listener.onVerifyResult(true);
    }

    /** 发起验证(同步) */
    public boolean doVerify(String code){
        return TextUtils.equals(code, mCode);
    }

    public void unRegisterEventHandler() {
    }
}
