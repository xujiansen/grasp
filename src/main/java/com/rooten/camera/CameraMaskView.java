package com.rooten.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public final class CameraMaskView extends View {
    private final Paint mPaint;
    private final int mMaskColor = 0xA0000000;
    private float mRatio = 4 / 3;        // 高宽比
    private float mScale = 1.0f;

    public CameraMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    public void setRatio(float ratio) {
        if (ratio <= 0.0f) {
            return;
        }
        mRatio = ratio;
        invalidate();
    }

    public void setScale(float scale) {
        if (scale <= 0.0f) {
            return;
        }
        mScale = scale;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        int padHorizontal = 0;
        int padVertical = 0;
        if (mRatio >= 1.0f) {
            padHorizontal = (int) (width - (height * mScale / mRatio)) / 2;
            padVertical = (int) (height - height * mScale) / 2;
        } else {
            padHorizontal = (int) (width - width * mScale) / 2;
            padVertical = (int) (height - (width * mScale * mRatio)) / 2;
        }

        // 画不透明区域
        mPaint.setColor(mMaskColor);
        canvas.drawRect(0, 0, width, padVertical, mPaint);
        canvas.drawRect(0, (height - padVertical), width, height, mPaint);
        canvas.drawRect(0, padVertical, padHorizontal, height - padVertical, mPaint);
        canvas.drawRect(width - padHorizontal, padVertical, width, height - padVertical, mPaint);
    }
}
