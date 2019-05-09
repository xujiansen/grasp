package com.rooten.frame;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import java.util.Hashtable;

import com.rooten.BaApp;
import com.rooten.help.AppHelper;

import lib.grasp.R;
import lib.grasp.mvp.BaseMvpActivity;
import lib.grasp.mvp.IMvpPresenter;
import lib.grasp.util.ViewUtil;
import lib.grasp.widget.MessageBoxGrasp;

public class ActivityEx extends BaseMvpActivity implements IShowError, IActivityResult, IHandler {
    protected BaApp mApp;

    private int mResultCode = 1;
    private boolean mIsOnResume = false;
    private boolean mIsExitApp = false;

    private Hashtable<Integer, IResultListener> mResultListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsExitApp = false;

        LayoutParams lp = getWindow().getAttributes();
        lp.windowAnimations = R.style.ActivityAnim;
        getWindow().setAttributes(lp);

        mApp = (BaApp) getApplication();
        mResultListener = new Hashtable<>();

        // 防止重复点击多次进入
//		boolean isSingleTask = AppHelper.isSingleTask(this);
//		boolean isContainsActivity = mApp.getActivityMgr().containsActivity(getClass());
//		if (!isSingleTask && isContainsActivity)
//		{
//			finish();
//			return;
//		}

        mApp.getActivityMgr().addActivity(this);
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
    public IMvpPresenter onBindPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        mApp.getActivityMgr().removeActivity(this);
        super.onDestroy();
    }

    protected void setStatusBarBgInKitKat() {
        AppHelper.setStatusBarBgInKitKat(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false; // 已经处理返回true，否则返回false向下处理
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IResultListener l = removeResultListener(requestCode);
        if (l == null) return;
        l.onResult(resultCode, data);
    }

    @Override
    public void startForResult(Intent intent, IResultListener listener) {
        int requestCode = addResultListener(listener);
        startActivityForResult(intent, requestCode);
    }

    protected int addResultListener(IResultListener l) {
        int resultCode = mResultCode++;
        mResultListener.put(resultCode, l);
        return resultCode;
    }

    protected IResultListener removeResultListener(int resultCode) {
        if (mResultListener.containsKey(resultCode)) {
            return mResultListener.remove(resultCode);
        }
        return null;
    }

    @Override
    public void showError(final View view, final String errMsg) {
        if (view != null) {
            ViewUtil.setFocusView(view);
        }

        if (!TextUtils.isEmpty(errMsg)) {
            MessageBoxGrasp.infoMsg(this, errMsg);
        }
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

        // 取消所有的notification
        mApp.getNotiHelper().cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsOnResume = false;

        boolean isBeforeLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
        int time = isBeforeLollipop ? 0 : 200;

        mApp.runOnUiThread(action, time);
    }

    public void exitApp() {
        mIsExitApp = true;
    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            if (mIsOnResume || mIsExitApp) {
                mApp.getNotiHelper().cancelAll();
                return;
            }

//			if (!mApp.isTopActivity())
//			{
//				mApp.getNotiHelper().addAppNotification();
//			}
        }
    };
}
