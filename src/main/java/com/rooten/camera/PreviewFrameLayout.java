
package com.rooten.camera;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import lib.grasp.R;

public class PreviewFrameLayout extends ViewGroup {
    private static final int MIN_HORIZONTAL_MARGIN = 10; // 10dp

    private int mMarginRight = 0;

    public interface OnSizeChangedListener {
        public void onSizeChanged();
    }

    private double mAspectRatio = 4.0 / 3.0;
    private FrameLayout mFrame;
    private OnSizeChangedListener mSizeListener;
    private DisplayMetrics mMetrics = new DisplayMetrics();

    public PreviewFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener listener) {
        mSizeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFrame = (FrameLayout) findViewById(R.id.frame);
        if (mFrame == null) {
            throw new IllegalStateException("must provide child with id as \"frame\"");
        }
    }

    public void setAspectRatio(double ratio) {
        if (ratio <= 0.0) throw new IllegalArgumentException();

        if (mAspectRatio != ratio) {
            mAspectRatio = ratio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int gripperWidth = 0;

        int frameWidth = getMeasuredWidth() - (int) Math.max(
                gripperWidth, MIN_HORIZONTAL_MARGIN * mMetrics.density);
        int frameHeight = getMeasuredHeight();

        FrameLayout f = mFrame;

        int horizontalPadding = f.getPaddingLeft() + f.getPaddingRight();
        int verticalPadding = f.getPaddingBottom() + f.getPaddingTop();

        int previewWidth = frameWidth - horizontalPadding;
        int previewHeight = frameHeight - verticalPadding;

        // resize frame and preview for aspect ratio
        if (previewWidth > previewHeight * mAspectRatio) {
            previewWidth = (int) (previewHeight * mAspectRatio + .5);
        } else {
            previewHeight = (int) (previewWidth / mAspectRatio + .5);
        }
        frameWidth = previewWidth + horizontalPadding;
        frameHeight = previewHeight + verticalPadding;


        measureChild(mFrame,
                MeasureSpec.makeMeasureSpec(frameWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(frameHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int frameWidth = mFrame.getMeasuredWidth();
        int frameHeight = mFrame.getMeasuredHeight();

        int leftSpace = ((r - l) - frameWidth) / 2;
        int topSpace = ((b - t) - frameHeight) / 2;

        int gripperWidth = 0;

        int left = 0;

        if (leftSpace > 0 && leftSpace < mMarginRight) {
            left = mMarginRight - leftSpace;
        }

        myLayoutChild(mFrame, Math.max(l + leftSpace, l + gripperWidth) - left,
                t + topSpace, frameWidth, frameHeight);


        if (mSizeListener != null) {
            mSizeListener.onSizeChanged();
        }
    }

    private static void myLayoutChild(View child, int l, int t, int w, int h) {
        child.layout(l, t, l + w, t + h);
    }

    public void setMarginRight(int marginRight) {
        mMarginRight = marginRight;
    }
}

