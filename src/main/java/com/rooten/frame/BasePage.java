package com.rooten.frame;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.rooten.BaApp;

import lib.grasp.util.ViewUtil;

public class BasePage implements IShowError {
    protected final Context mContext;
    protected final BaApp mApp;
    private onVerifyAndSubmitListener mListener = null;

    public BasePage(Context context) {
        mContext = context;
        mApp = (BaApp) mContext.getApplicationContext();
    }

    public View getView() {
        return null;
    }

    protected void initPage() {
    }

    protected Context getContext() {
        return mContext;
    }

    protected void initCtrl() {
    }

    public void loadData() {
    }

    protected void loadDefault() {

    }

    public void doVerifyData() {
        errorVerifyOrSubmit();
    }

    public void doSubmitData() {
        errorVerifyOrSubmit();
    }

    public void doSaveData() {
    }

    public void doResetData() {
    }

    public void doReleaseData() {
    }

    public void onDetachedView() {
    }

    public interface onVerifyAndSubmitListener {
        void onVerifyOrSubmitError();

        void onVerifyCompleted();

        void onSubmitCompleted();
    }

    public void setWorkflowListener(onVerifyAndSubmitListener l) {
        mListener = l;
    }

    @Override
    public void showError(final View view, final String errMsg) {
        errorVerifyOrSubmit();
        if (view != null) {
            ViewUtil.setFocusView(view);
        }

        ViewUtil.shakeView(getContext(), view);

        if (!TextUtils.isEmpty(errMsg)) {
//			MessageBoxGrasp.infoMsg(getContext(), errMsg);
            Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
        }
    }

    protected void errorVerifyOrSubmit() {
        if (mListener != null) {
            mListener.onVerifyOrSubmitError();
        }
    }

    protected void verifyCompleted() {
        if (mListener != null) {
            mListener.onVerifyCompleted();
        }
    }

    protected void submitCompleted() {
        if (mListener != null) {
            mListener.onSubmitCompleted();
        }
    }
}
