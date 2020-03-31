package com.rooten.frame;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import lib.grasp.R;
import lib.grasp.eventbus.entity.Event;
import lib.grasp.mvp.IMvpPresenter;
import lib.grasp.util.EventBusUtil;

import com.rooten.util.LinearLayoutHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AppActivity<P extends IMvpPresenter> extends ActivityEx<P> implements OnSubmitCompletedListener, IHandler {
    protected View mRoot;
    protected MenuItem mItem;
    public Toolbar mToolbar;
    protected LinearLayout mLayoutPage;

    protected int mToolbarRes = -1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置背景色
        setStatusBarBgInKitKat();
        setContentView(init(savedInstanceState));

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
    }

    private View init(Bundle savedInstanceState) {
        RelativeLayout contentLayout = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.activity_root, null);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            contentLayout.setClipToPadding(true);
            contentLayout.setFitsSystemWindows(true);
        }

        int toolBarRes = getToolBarRes();
        toolBarRes = toolBarRes != -1 ? toolBarRes : R.layout.app_toolbar;
        mToolbar = (Toolbar) LayoutInflater.from(this).inflate(toolBarRes, null);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);

        LinearLayout layout = new LinearLayout(this);
        int bgColor = getResources().getColor(R.color.window_color);
        layout.setBackgroundColor(bgColor);
        mLayoutPage = LinearLayoutHelper.createVertical(this);    // 创建子页面布局

        int width = LinearLayoutHelper.MATCH_PARENT;
        int height = LinearLayoutHelper.MATCH_PARENT;
        layout.addView(mLayoutPage, width, height);

        int w = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int m = RelativeLayout.LayoutParams.MATCH_PARENT;
        contentLayout.addView(mToolbar, m, w);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(m, m);
        params.addRule(RelativeLayout.BELOW, mToolbar.getId());
        contentLayout.addView(layout, params);

        mRoot = contentLayout;
        initView(savedInstanceState);

        return contentLayout;
    }

    protected int getToolBarRes() {
        return mToolbarRes;
    }

    public void setToolbarRes(int mToolbarRes) {
        this.mToolbarRes = mToolbarRes;
    }

    protected void initView(Bundle savedInstanceState) {

    }

    public void setPageBgColor(int color) {
        mRoot.setBackgroundColor(color);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mItem = menu.add(1, 1, 1, "确定");
        MenuItemCompat.setShowAsAction(mItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        initActionMenu(mItem);
        return true;
    }

    protected void initActionMenu(MenuItem item) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onAppIconClick();
                return true;
            case 1:
                onOK();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void installView(View view, float weight) {
        LinearLayoutHelper.addView(mLayoutPage, view, LinearLayoutHelper.MATCH_PARENT, LinearLayoutHelper.WRAP_CONTENT, weight);
    }

    protected void installView(View view, LayoutParams lp) {
        LinearLayoutHelper.addView(mLayoutPage, view, lp);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//            if (mItem != null && mItem.isVisible()) onOK();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    protected void onBack() {
        finish();
    }

    protected void onOK() {
    }

    protected void onAppIconClick() {
        onBack();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_ENTER || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    @Override
    public void onSubmitCompleted() {
    }

    @Override
    public void onSubmitError() {
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            if (getSupportActionBar() == null) return;
            getSupportActionBar().setTitle(title);
        }
    }

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
