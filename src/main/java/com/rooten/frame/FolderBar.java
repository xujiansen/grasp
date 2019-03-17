package com.rooten.frame;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import com.rooten.Constant;
import lib.grasp.R;
import com.rooten.util.IconifyUtil;

public class FolderBar extends LinearLayout {
    private final int ICON_SIZE = 26;

    private int mHomeViewEnablelColor = Constant.COLOR_TOOLBAR;
    private int mHomeViewDisablelColor = Color.parseColor("#eaeaea");

    private int mBackViewEnablelColor = Constant.COLOR_TOOLBAR;
    private int mBackViewDisablelColor = Color.parseColor("#eaeaea");

    public FolderBar(Context context) {
        super(context);
        init();
    }

    public FolderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setHomeViewColor(int enable, int disable) {
        mHomeViewEnablelColor = enable;
        mHomeViewDisablelColor = disable;

        initHomeAndReplyIcon();
    }

    public void setBackViewColor(int enable, int disable) {
        mBackViewEnablelColor = enable;
        mBackViewDisablelColor = disable;

        initHomeAndReplyIcon();
    }

    protected void init() {
        TabLayout tab = new TabLayout(getContext());
        tab.measure(0, 0);
        setMinimumHeight(tab.getMeasuredHeight());

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(R.layout.folder_bar, this, true);

        initHomeAndReplyIcon();
    }

    private void initHomeAndReplyIcon() {
        IconDrawable enableHomeIcon = getEnableHomeIcon();
        IconDrawable disableHomeIcon = getDisableHomeIcon();
        IconDrawable enableReplyIcon = getEnableReplyIcon();
        IconDrawable disableReplyIcon = getDisableReplyIcon();

        if (enableHomeIcon == null || disableHomeIcon == null) return;
        if (enableReplyIcon == null || disableReplyIcon == null) return;

        ImageView home = (ImageView) findViewById(R.id.btnHome);
        ImageView back = (ImageView) findViewById(R.id.btnBack);

        StateListDrawable homeDrawable = new StateListDrawable();
        homeDrawable.addState(new int[]{android.R.attr.state_enabled}, enableHomeIcon);
        homeDrawable.addState(new int[]{-android.R.attr.state_enabled}, disableHomeIcon);

        StateListDrawable replyDrawable = new StateListDrawable();
        replyDrawable.addState(new int[]{android.R.attr.state_enabled}, enableReplyIcon);
        replyDrawable.addState(new int[]{-android.R.attr.state_enabled}, disableReplyIcon);

        home.setImageDrawable(homeDrawable);
        back.setImageDrawable(replyDrawable);
    }

    private IconDrawable getEnableHomeIcon() {
        return IconifyUtil.getIconByColor(getContext(), "md-home", mHomeViewEnablelColor, ICON_SIZE);
    }

    private IconDrawable getDisableHomeIcon() {
        return IconifyUtil.getIconByColor(getContext(), "md-home", mHomeViewDisablelColor, ICON_SIZE);
    }

    private IconDrawable getEnableReplyIcon() {
        return IconifyUtil.getIconByColor(getContext(), "md-reply", mBackViewEnablelColor, ICON_SIZE);
    }

    private IconDrawable getDisableReplyIcon() {
        return IconifyUtil.getIconByColor(getContext(), "md-reply", mBackViewDisablelColor, ICON_SIZE);
    }

    public String getTitle() {
        final TextView titleText = (TextView) findViewById(R.id.text1);
        return (String) titleText.getText();
    }

    public void setTitle(String strTitle) {
        final TextView titleText = (TextView) findViewById(R.id.text1);
        titleText.setText(strTitle);
    }

    public void setTitleColor(int color) {
        final TextView titleText = (TextView) findViewById(R.id.text1);
        titleText.setTextColor(color);
    }

    public TextView getTitleView() {
        return (TextView) findViewById(R.id.text1);
    }

    public View getHomeView() {
        return findViewById(R.id.btnHome);
    }

    public View getBackView() {
        return findViewById(R.id.btnBack);
    }

    public void setBackgroundColor(int color) {
        final View rootView = findViewById(R.id.folder_bar_root);
        rootView.setBackgroundColor(color);
    }
}
