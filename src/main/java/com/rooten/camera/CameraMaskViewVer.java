package com.rooten.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public final class CameraMaskViewVer extends View {
    private final Paint mPaint;
    private final int mMaskColor = 0xA0000000;
    private float mRatio = 4 / 3;        // 高宽比
    private float mScale = 1.0f;

    public CameraMaskViewVer(Context context, AttributeSet attrs) {
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
            padHorizontal = (int) (width - height * mScale) / 2;
            padVertical = (int) (height - (height * mScale / mRatio)) / 2;
        } else {
            padHorizontal = (int) (width - (height * mScale * mRatio)) / 2;
            padVertical = (int) (height - height * mScale) / 2;
        }

        // 画左右不透明区域
        mPaint.setColor(mMaskColor);
        canvas.drawRect(0, 0, padHorizontal, width, mPaint);
        canvas.drawRect(padHorizontal, 0, width - padHorizontal, padVertical, mPaint);

        canvas.drawRect(width - padHorizontal, 0, width, height, mPaint);
        canvas.drawRect(padHorizontal, height - padVertical, width - padHorizontal, height, mPaint);
    }
}
