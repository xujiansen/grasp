package com.rooten.frame.tab;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rooten.interf.IActionReceiver;
import com.rooten.interf.IBackHandled;
import lib.grasp.R;

public class AppTabView extends LinearLayout implements View.OnClickListener {
    private Activity mActivity;
    private View mTabContent;

    private onAppTabClickListener mClick;



    public AppTabView(Context context) {
        super(context);
        init();
    }

    public AppTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.TRANSPARENT);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public void attachActivity(Activity activity) {
        mActivity = activity;
    }

    public void setOnAppTabClickListener(onAppTabClickListener l) {
        mClick = l;
    }

    public void hideAllFragment() {
        if (mActivity == null || mTabContent == null) return;

        FragmentManager manager = mActivity.getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            if (tab.fragment == null) continue;

            ft.hide(tab.fragment);
        }
        ft.commitAllowingStateLoss();
    }

    public void addAllFragment() {
        if (mActivity == null || mTabContent == null) return;

        FragmentManager manager = mActivity.getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            if (tab.fragment == null) continue;

            ft.add(mTabContent.getId(), tab.fragment);
        }
        ft.commitAllowingStateLoss();
    }

    private void showFragment(Fragment fragment) {
        if (mActivity == null || mTabContent == null || fragment == null) return;

        FragmentManager manager = mActivity.getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    public int addTab(int iconNormalRes, int iconPressedRes, Fragment fragment, String text) {
        // 最大tab数目
        final int TAB_MAX = 6;

        int tabCount = getChildCount();
        if (tabCount >= TAB_MAX) return -1; // 当前最多四个

        View itemView = addBottomTab(iconNormalRes, text);

        AppTab tab = new AppTab();
        tab.title = text;
        tab.itemView = itemView;
        tab.fragment = fragment;
        tab.iconNormalRes = iconNormalRes;
        tab.iconPressedRes = iconPressedRes;
        itemView.setTag(tab);

        return getChildCount() - 1;
    }

    // 添加显示在底部的Tab项，最多显示5个
    private View addBottomTab(int iconNormalRes, String text) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.app_tab_item, null);
        itemView.setOnClickListener(this);

        ImageView icon = (ImageView) itemView.findViewById(R.id.app_tab_item_icon);
        icon.setImageResource(iconNormalRes);

        TextView title = (TextView) itemView.findViewById(R.id.app_tab_item_title);
        title.setText(text);

        final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        params.weight = 1;
        addView(itemView, params);

        return itemView;
    }

    public AppTabTextView getTabTextView(int index) {
        int childCount = getChildCount();
        if (index < 0 || index > childCount - 1) return null;

        View child = getChildAt(index);
        return (AppTabTextView) child.findViewById(R.id.app_tab_item_pop);
    }

    @Override
    public void onClick(View v) {
        int index = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            if (v == child) {
                index = i;
                break;
            }
        }

        setCurTab(index);

        if (mClick == null) return;
        mClick.onTabClick(index);
    }

    public int getCurTab() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            if (tab.isPressed) return i;
        }
        return -1;
    }

    public void setCurTab(int index) {
        hideAllFragment();
        toggleTabRes(index);
    }

    private void toggleTabRes(int index) {
        int childCount = getChildCount();
        if (index < 0 || index > childCount - 1) return;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            ImageView icon = (ImageView) tab.itemView.findViewById(R.id.app_tab_item_icon);
            TextView title = (TextView) tab.itemView.findViewById(R.id.app_tab_item_title);

            if (index == i) {
                tab.isPressed = true;
                icon.setImageResource(tab.iconPressedRes);
                title.setTextColor(getResources().getColor(R.color.tabTextPressed));

                // 显示Content-Fragment
                showFragment(tab.fragment);
            } else {
                tab.isPressed = false;
                icon.setImageResource(tab.iconNormalRes);
                title.setTextColor(getResources().getColor(R.color.tabTextNormal));
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Context context = getContext();
        if (!(context instanceof Activity)) return;
        mActivity = (Activity) context;

        View contentView = mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        mTabContent = contentView.findViewById(R.id.app_tab_content);

        if (mTabContent == null) {
            throw new NullPointerException("tabContent的视图id必须设置为app_tab_content");
        }

        // 校验
        if (getChildCount() == 0) return;

        addAllFragment();   // 初始化页面
        setCurTab(0);       // 默认显示第一个
    }

    public boolean onBack() {
        int curTab = getCurTab();
        int childCount = getChildCount();
        if (curTab < 0 || curTab > childCount - 1) return false;

        View child = getChildAt(curTab);
        if (child == null) return false;

        Object obj = child.getTag();
        if (!(obj instanceof AppTab)) return false;

        AppTab tab = (AppTab) obj;
        if (!(tab.fragment instanceof IBackHandled)) return false;

        IBackHandled back = (IBackHandled) tab.fragment;
        return back.onBack();
    }

    public void onReceive(String action, Intent intent) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            if (!(tab.fragment instanceof IActionReceiver)) continue;

            if (!tab.fragment.isAdded()) continue;
            IActionReceiver l = (IActionReceiver) tab.fragment;
            l.onReceive(action, intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            if (tab.fragment == null) continue;

            tab.fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void clearTabView() {
        FragmentManager manager = mActivity.getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == null) continue;

            Object obj = child.getTag();
            if (!(obj instanceof AppTab)) continue;

            AppTab tab = (AppTab) obj;
            if (tab.fragment == null) continue;

            ft.remove(tab.fragment);
        }
        ft.commitAllowingStateLoss();

        // 移除子视图， 并清空列表， 重置
        removeAllViews();
    }

    public interface onAppTabClickListener {
        void onTabClick(int index);
    }
}
