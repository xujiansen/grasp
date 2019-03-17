package com.rooten.sys;

import android.os.Bundle;
import android.view.MenuItem;

import com.rooten.frame.AppActivity;
import com.rooten.frame.SinglePageMgr;
import lib.grasp.R;

public class AppAbout extends AppActivity {
    private SinglePageMgr mPageMgr = null;
    private AppAboutPage mPage = null;

    @Override
    public void initView(Bundle savedInstanceState) {
        mPageMgr = new SinglePageMgr(this);
        mPage = new AppAboutPage(this, this);
        mPageMgr.setPage(mPage, "关于");

        installView(mPageMgr, 0);

        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setTitle("关于");
    }

    @Override
    protected void initActionMenu(MenuItem item) {
        item.setVisible(false);
    }
}
