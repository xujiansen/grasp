package com.rooten.frame.page.bizpage;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.rooten.util.BaseLinearLayoutHelper;

public class ScrollPage extends BasePage {
    protected ScrollView mRootView = null;
    protected LinearLayout mLinearLayout = null;

    public ScrollPage(Context context) {
        super(context);
        createView();
    }

    private void createView() {
        mLinearLayout = BaseLinearLayoutHelper.createVertical(mContext);
        mRootView = new ScrollView(mContext);
        mRootView.addView(mLinearLayout);
    }

    @Override
    public View getView() {
        return mRootView;
    }

    protected void addView(View view, int width, int height) {
        BaseLinearLayoutHelper.addView(mLinearLayout, view, width, height);
    }

    protected void addView(View view, int width, int height, float weight) {
        BaseLinearLayoutHelper.addView(mLinearLayout, view, width, height, weight);
    }

    protected void addView(View view, LayoutParams lp) {
        BaseLinearLayoutHelper.addView(mLinearLayout, view, lp);
    }
}
