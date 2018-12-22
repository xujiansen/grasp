package cn.com.rooten.frame;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.com.rooten.frame.BasePage.onVerifyAndSubmitListener;
import cn.com.rooten.util.LinearLayoutHelper;

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

        final int width = LinearLayoutHelper.MATCH_PARENT;
        final int height = LinearLayoutHelper.WRAP_CONTENT;

        mPage.setWorkflowListener(this);
        LayoutParams lp = new LayoutParams(width, height, 1);
        LinearLayoutHelper.addView(this, mPage.getView(), lp);
    }

    public BasePage getPage() {
        return mPage;
    }

    public void doPageVerifyAndSubmit() {
        if (mPage != null) {
            mPage.doVerifyData();
        }
    }

    public void doPageSavaData() {
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
