
package com.rooten.camera;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import lib.grasp.R;

public class FocusRectangle extends View {
    private Drawable mDraw;
    private Paint mPaint;
    private ValueAnimator mAnimator;
    private ValueAnimator mVisibleAnimator;

    private float mSize = 1.0f;
    private int time = 500;

    private float mDown_x = 0.0f, mDown_y = 0.0f;

    public FocusRectangle(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setDither(false);
    }

    private void setDrawable(int resId) {
        mDraw = getResources().getDrawable(resId);
        invalidate();
    }

    public void showStart(boolean hasAnimator) {
        setDrawable(R.drawable.camera_focusing);
        if (!hasAnimator) return;

        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }

        if (mVisibleAnimator != null) {
            mVisibleAnimator.cancel();
            mVisibleAnimator = null;
        }

        startAnimation(1f, 2f);
    }

    public void showSuccess() {
        setDrawable(R.drawable.camera_focused);
    }

    public void showFail() {
        setDrawable(R.drawable.camera_focus_failed);
        startAnimation(2f, 1f);
        mAnimator.addListener(new FocusAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSize = 1.0f;
                invalidate();
            }
        });
    }

    public void clear() {
        mDraw = null;
        invalidate();
    }

    public void reset() {
        mSize = 1.0f;

        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }

        if (mVisibleAnimator != null) {
            mVisibleAnimator.cancel();
            mVisibleAnimator = null;
        }

        showSuccess();
        invalidate();
    }

    public void move(float down_x, float down_y) {
        mDown_x = down_x;
        mDown_y = down_y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDraw == null) return;

        int width = (int) (mDraw.getIntrinsicWidth() * mSize);
        int height = (int) (mDraw.getIntrinsicHeight() * mSize);
        int len = Math.max(width, height);
        int xPos = (getWidth() - len) / 2;
        int yPos = (getHeight() - len) / 2;

        if (mDown_x != 0.0f && mDown_y != 0.0f) {
            Rect r = new Rect();
            getGlobalVisibleRect(r);

            if (!r.contains((int) mDown_x, (int) mDown_y)) return;

            if (mSize == 1.0f) {
                if (mDown_x < len / 2 + r.left) mDown_x = len / 2 + r.left;
                if (mDown_y < len / 2) mDown_y = len / 2;
                if (mDown_x > getWidth() - len / 2) mDown_x = getWidth() - len / 2;
                if (mDown_y > getHeight() - len / 2) mDown_y = getHeight() - len / 2;
            }

            int left = (int) (mDown_x - len / 2) - r.left;
            int top = (int) (mDown_y - len / 2);
            int right = (int) (mDown_x + len / 2) - r.left;
            int bottom = (int) (mDown_y + len / 2);

            mDraw.setBounds(left, top, right, bottom);
        } else {
            mDraw.setBounds(xPos, yPos, xPos + len, yPos + len);
        }

        mDraw.draw(canvas);
    }

    public void takePicSuccess() {
        mDown_x = 0;
        mDown_y = 0;

        showSuccess();
        setVisibility(VISIBLE);
    }

    @Override
    public void setVisibility(final int visibility) {
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            ValueAnimator animator = startAnimation(2f, 1f);
            animator.addListener(new FocusAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibilityAfterEnd(visibility);
                }
            });

            animator.addListener(new FocusAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSize = 1.0f;
                    invalidate();
                }
            });

            mVisibleAnimator = animator;
        } else {
            super.setVisibility(visibility);
        }
    }

    public void setVisibilityAfterEnd(int v) {
        super.setVisibility(v);
    }

    private ValueAnimator startAnimation(float start, float end) {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }

        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(time);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.start();

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object obj = animation.getAnimatedValue();
                if (!(obj instanceof Float)) return;
                mSize = (Float) obj;
                invalidate();
            }
        });

        return mAnimator;
    }

    private class FocusAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
