package com.rooten.frame.tab;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import lib.grasp.util.ScreenUtil;

public class AppTabTextView extends AppCompatTextView {
    private int mMode = MODE_NUM_POP;

    public static final int MODE_RED_POP = 1;
    public static final int MODE_NUM_POP = 2;

    public AppTabTextView(Context context) {
        super(context);
    }

    public AppTabTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppTabTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mMode != MODE_RED_POP && mMode != MODE_NUM_POP) return;

        // 绘制监听
        getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // 移除
        getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
    }

    ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (mMode == MODE_RED_POP) showRedPop();
            if (mMode == MODE_NUM_POP) showNumPop();
        }
    };

    private void showRedPop() {
        setText("");

        int size = ScreenUtil.getValueByDpi(getContext(), 14);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = size;
        params.height = size;
        setLayoutParams(params);
    }

    private void showNumPop() {
        int w = getWidth();
        int h = getHeight();
        if (w >= h) return;

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = h;
        setLayoutParams(params);
    }
}
