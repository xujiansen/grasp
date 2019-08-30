package lib.grasp.http.retrofit;

import android.content.Context;
import android.text.TextUtils;

import com.rooten.BaApp;

import lib.grasp.util.L;
import lib.grasp.widget.ProgressDlgGrasp;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 将一些重复的操作提出来，放到父类以免Loader 里每个接口都有重复代码
 */

public class ObjectLoader {

    protected   BaApp mApp;
    protected   Context mContext;
    private     ProgressDlgGrasp mProgressDlg;

    /**
     * 是否取消
     */
    private boolean mIsCancel = false;
    /**
     * 是否显示prog
     */
    private boolean mIsShowDialog = false;
    /**
     * 进度条提示信息
     */
    private String mInfoStr;

    public ObjectLoader(Context mContext, boolean mIsShowDialog, String mInfoStr) {
        this.mContext = mContext;
        this.mIsShowDialog = mIsShowDialog;
        this.mInfoStr = mInfoStr;
    }

    /**
     * 在主线程中生成被观察者, 接下在的代码运行在IO线程中
     */
    protected  <T> Observable<T> observe(Observable<T> observable){
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(mOnSubscribe)
                .doOnCompleted(mOnCompleted)  // 完成事件(跟onCompleted同为出口, 互斥)
                .doOnError(mErrAction);       // 异常事件([[重要]]如果最后error了, 之前的所有的操作(哪怕成功)都不会通知观察者, 只通知error事件, 也就说明原本这边先把流程走完, 再依次通知观察者)
    }

    private Action0 mOnSubscribe = new Action0() {
        @Override
        public void call() {
            // 这里按要求显示加载框
            if(mIsShowDialog){
                if(mProgressDlg == null) initProgressDlg();
                mProgressDlg.show();
                mProgressDlg.setMessage(TextUtils.isEmpty(mInfoStr) ? "正在请求" : mInfoStr);
            }
        }
    };

    private Action0 mOnCompleted = new Action0() {
        @Override
        public void call() {
            // 这里记得关掉加载框
            if(mIsShowDialog && mProgressDlg != null && mProgressDlg.isShowing()){
                mProgressDlg.dismiss();
            }
        }
    };

    private Action1<Throwable> mErrAction = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            L.logOnly("---------------报错啦, throwable" + throwable);
            // 这里记得关掉加载框
            if(mIsShowDialog && mProgressDlg != null && mProgressDlg.isShowing()){
                mProgressDlg.dismiss();
            }

            if (throwable instanceof Fault) {
                Fault fault = (Fault) throwable;
                if (fault.getErrorCode() == 404) {
                    //错误处理
                } else if (fault.getErrorCode() == 500) {
                    //错误处理
                } else if (fault.getErrorCode() == 501) {
                    //错误处理
                }
            }
        }
    };

    private void initProgressDlg() {
        mProgressDlg = new ProgressDlgGrasp(mContext);
        mProgressDlg.setCanBeCancel(true);
        mProgressDlg.setCancelListener(v -> {
            mIsCancel = true;
            dismissView();
        });
    }

    /** 清除所有的加载界面的显示 */
    private void dismissView() {
        if (mProgressDlg != null) {
            mProgressDlg.dismiss();
            mProgressDlg = null;
        }
    }

}
