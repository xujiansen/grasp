package com.rooten.biz;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import lib.grasp.R;
import lib.grasp.util.AppUtil;

import com.rooten.BaApp;
import com.rooten.frame.page.bizpage.LinearPage;

class AppAboutPage extends LinearPage implements View.OnClickListener {
    private Activity    mAct;

    private View        mRlVersion;
    private View        mRlUpdate;
    private View        mRlCacheClear;
    private View        mRlProtocol;
    private TextView    mTvVersion;

    AppAboutPage(Context context, Activity activity) {
        super(context);
        mAct = activity;
        initPage();
        loadData();
    }

    @Override
    protected void initPage() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.app_about, null);
        addView(view, 1);

        mRlVersion      = view.findViewById(R.id.rl_version);
        mRlUpdate       = view.findViewById(R.id.rl_update);
        mRlCacheClear   = view.findViewById(R.id.rl_clear_cache);
        mRlProtocol     = view.findViewById(R.id.rl_protocol);
        mTvVersion      = (TextView)view.findViewById(R.id.tv_version);

        mRlVersion.setOnClickListener(this);
        mRlUpdate.setOnClickListener(this);
        mRlCacheClear.setOnClickListener(this);
        mRlProtocol.setOnClickListener(this);
    }

    @Override
    public void loadData() {
        mTvVersion.setText(AppUtil.getAppVersionName(BaApp.getApp()));
    }

    @Override
    public void doVerifyData() {
        verifyCompleted();
    }

    @Override
    public void doSubmitData() {
        submitCompleted();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_version) {
        } else if (i == R.id.rl_update) {
        } else if (i == R.id.rl_clear_cache) {
        } else if (i == R.id.rl_protocol) {
        }

    }
}
