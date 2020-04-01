package com.rooten.frame.page.bizpage;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.rooten.util.BaseLinearLayoutHelper;

public class LinearPage extends BasePage {
    protected LinearLayout mRootView;

    public LinearPage(Context context) {
        super(context);
        createView();
    }

    private void createView() {
        mRootView = BaseLinearLayoutHelper.createVertical(getContext());
    }

    @Override
    public View getView() {
        return mRootView;
    }

    protected void addView(View view) {
        final int width = BaseLinearLayoutHelper.MATCH_PARENT;
        final int height = BaseLinearLayoutHelper.WRAP_CONTENT;
        BaseLinearLayoutHelper.addView(mRootView, view, width, height);
    }

    protected void addView(View view, float weight) {
        final int width = BaseLinearLayoutHelper.MATCH_PARENT;
        final int height = BaseLinearLayoutHelper.WRAP_CONTENT;
        BaseLinearLayoutHelper.addView(mRootView, view, width, height, weight);
    }
}
