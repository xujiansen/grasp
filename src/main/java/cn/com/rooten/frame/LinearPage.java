package cn.com.rooten.frame;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import cn.com.rooten.util.LinearLayoutHelper;

public class LinearPage extends BasePage {
    protected LinearLayout mRootView;

    public LinearPage(Context context) {
        super(context);
        createView();
    }

    private void createView() {
        mRootView = LinearLayoutHelper.createVertical(getContext());
    }

    @Override
    public View getView() {
        return mRootView;
    }

    protected void addView(View view) {
        final int width = LinearLayoutHelper.MATCH_PARENT;
        final int height = LinearLayoutHelper.WRAP_CONTENT;
        LinearLayoutHelper.addView(mRootView, view, width, height);
    }

    protected void addView(View view, float weight) {
        final int width = LinearLayoutHelper.MATCH_PARENT;
        final int height = LinearLayoutHelper.WRAP_CONTENT;
        LinearLayoutHelper.addView(mRootView, view, width, height, weight);
    }
}
