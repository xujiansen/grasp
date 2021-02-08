package com.rooten;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import androidx.annotation.Nullable;

import java.util.Hashtable;

import com.rooten.interf.IActivityResult;
import com.rooten.interf.IHandler;
import com.rooten.interf.IResultListener;
import com.rooten.interf.IShowError;
import com.rooten.frame.ActivityMgr;
import com.rooten.util.AppHelper;
import com.rooten.help.NotificationHelper;
import com.rooten.util.Util;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lib.grasp.R;
import lib.grasp.eventbus.Event;
import lib.grasp.mvp.BaseMvpActivity;
import lib.grasp.mvp.BaseMvpPresenter;
import lib.grasp.mvp.IMvpPresenter;
import lib.grasp.util.L;
import lib.grasp.util.ViewUtil;
import lib.grasp.widget.MessageBoxGrasp;

public class ActivityEx<P extends BaseMvpPresenter>  extends BaseMvpActivity<P> implements IActivityResult {

    /** 页面跳转请求码 */
    private int mResultCode = 1;

    /** 本页面是否可见 */
    private boolean mIsOnResume = false;

    private Hashtable<Integer, IResultListener> mResultListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutParams lp = getWindow().getAttributes();
        lp.windowAnimations = R.style.ActivityAnim;

        getWindow().setAttributes(lp);

        mResultListener = new Hashtable<>();

        ActivityMgr.getDefault().addActivity(this);
        L.log("进入Activity");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return isOutOfBounds(event) || super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Rect r = new Rect();
        getWindow().getDecorView().getGlobalVisibleRect(r);
        return !r.contains(x, y);
    }

    @Override
    protected void onDestroy() {
        ActivityMgr.getDefault().removeActivity(this);
        super.onDestroy();
        L.log("退出Activity");
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false; // 已经处理返回true，否则返回false向下处理
    }

    @Override
    public void startForResult(Intent intent, IResultListener listener) {
        int requestCode = addResultListener(listener);
        startActivityForResult(intent, requestCode);
    }

    /** 添加页面返回监听 */
    protected int addResultListener(IResultListener l) {
        int resultCode = mResultCode++;
        mResultListener.put(resultCode, l);
        return resultCode;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IResultListener l = removeResultListener(requestCode);
        if (l == null) return;
        l.onResult(resultCode, data);
    }

    /** 删除指定页面返回监听 */
    @Nullable
    protected IResultListener removeResultListener(int resultCode) {
        if (mResultListener.containsKey(resultCode)) {
            return mResultListener.remove(resultCode);
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsOnResume = true;

        NotificationHelper.getDefault().cancelAll(); // 取消所有的notification
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsOnResume = false;

        boolean isBeforeLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
        int time = isBeforeLollipop ? 0 : 200;

        BaApp.getApp().runOnUiThread(action, time);
    }

    /** 去除本APP的notification */
    private final Runnable action = () -> {
        if (mIsOnResume) {
            NotificationHelper.getDefault().cancelAll();
            return;
        }

//			if (!mApp.isTopActivity())
//			{
//				mApp.getNotiHelper().addAppNotification();
//			}
    };


    /* ⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇EventBus⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇⬇ */

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(Event event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     * @param event 事件
     */
    protected void receiveEvent(Event event) { }

    /**
     * 接受到分发的粘性事件
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(Event event) { }

    /* ⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆EventBus⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆⬆ */
}
