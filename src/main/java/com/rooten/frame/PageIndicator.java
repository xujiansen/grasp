package com.rooten.frame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import lib.grasp.R;

public class PageIndicator extends View {
    private int mPages = 0;
    private int mCurPage = 0;
    private Drawable mImgNormal = null;
    private Drawable mImgFocus = null;
    private int mHeight;
    private int mPressed = -1;
    OnIndicatorClickListener mListener = null;

    public interface OnIndicatorClickListener {
        void onClick(int pos);
    }

    public void setIndicatorListener(OnIndicatorClickListener l) {
        mListener = l;
    }

    public PageIndicator(Context context) {
        super(context);
        init();
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mImgNormal = getContext().getResources().getDrawable(R.drawable.ic_indicator_normal);
        mImgFocus = getContext().getResources().getDrawable(R.drawable.ic_indicator_focus);

        mHeight = mImgFocus.getIntrinsicHeight();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    playSoundEffect(SoundEffectConstants.CLICK);

                    mListener.onClick(mPressed);
                }
            }
        });
    }

    public void setPageIndicator(int nPageCount, int nCurPage) {
        if (nPageCount < 0 || nCurPage < 0 || nCurPage >= nPageCount) return;

        mPages = nPageCount;
        mCurPage = nCurPage;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        if (mPages == 0) return;

        int nWidth = MeasureSpec.getSize(getWidth());
        int imgWidth = mImgNormal.getIntrinsicWidth();
        int offsetX = (nWidth - (imgWidth * mPages)) / 2;
        for (int i = 0; i < mPages; i++) {
            if (i == mCurPage) {
                drawIcon(canvas, mImgFocus, offsetX);
            } else {
                drawIcon(canvas, mImgNormal, offsetX);
            }
            offsetX += imgWidth;
        }
    }

    protected void drawIcon(Canvas canvas, Drawable img, int x) {
        int nHeight = MeasureSpec.getSize(getHeight());
        int imgHeight = img.getIntrinsicHeight();
        int imgWidth = img.getIntrinsicWidth();
        if (nHeight >= imgHeight) {
            int offsetY = (nHeight - imgHeight) / 2;
            img.setBounds(x, offsetY, x + imgWidth, offsetY + imgHeight);
        } else {
            img.setBounds(x, 0, x + imgWidth, nHeight);
        }
        img.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int nWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(nWidth, measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        int result = specSize;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY: {
                result = mHeight;
                break;
            }
        }
        return result;
    }

    public int getIndicatorHeight() {
        return mHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPages == 0) {
            return super.onTouchEvent(event);
        }

        float touchPosX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                mPressed = -1;
                int nWidth = MeasureSpec.getSize(getWidth());
                int imgWidth = mImgNormal.getIntrinsicWidth();
                int offsetX = (nWidth - (imgWidth * mPages)) / 2;
                for (int i = 0; i < mPages; i++) {
                    if (offsetX <= touchPosX && touchPosX <= (offsetX + imgWidth)) {
                        mPressed = i;
                        break;
                    }
                    offsetX += imgWidth;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }
}
