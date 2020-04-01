package com.rooten.frame.page.pagemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.rooten.frame.page.bizpage.BasePage;
import com.rooten.frame.page.bizpage.BasePage.onVerifyAndSubmitListener;
import com.rooten.interf.OnSubmitCompletedListener;
import com.rooten.util.BaseLinearLayoutHelper;

public class SinglePageMgr extends LinearLayout implements onVerifyAndSubmitListener {
    private BasePage mPage;
    private OnSubmitCompletedListener mListener;

    public SinglePageMgr(Context context) {
        super(context);
        init();
    }

    public SinglePageMgr(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setPage(BasePage viewPage, String pageTitle) {
        if (viewPage == null) return;
        mPage = viewPage;

        final int width = BaseLinearLayoutHelper.MATCH_PARENT;
        final int height = BaseLinearLayoutHelper.WRAP_CONTENT;

        mPage.setWorkflowListener(this);
        LayoutParams lp = new LayoutParams(width, height, 1);
        BaseLinearLayoutHelper.addView(this, mPage.getView(), lp);
    }

    public BasePage getPage() {
        return mPage;
    }

    public void doPageVerifyAndSubmit() {
        if (mPage != null) {
            mPage.doVerifyData();
        }
    }

    public void doPageSaveData() {
        if (mPage != null) {
            mPage.doSaveData();
        }
    }

    public void doPageResetData() {
        if (mPage != null) {
            mPage.doResetData();
        }
    }

    public void doPageReleaseData() {
        if (mPage != null) {
            mPage.doReleaseData();
        }
    }

    @Override
    public void onVerifyOrSubmitError() {
    }

    @Override
    public void onVerifyCompleted() {
        if (mPage != null) {
            mPage.doSubmitData();
        }
    }

    @Override
    public void onSubmitCompleted() {
        if (mListener != null) {
            mListener.onSubmitCompleted();
        }
    }

    public void setOnSubmitCompletedListener(OnSubmitCompletedListener l) {
        mListener = l;
    }
}
