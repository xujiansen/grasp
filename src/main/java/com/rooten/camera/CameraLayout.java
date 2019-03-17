package com.rooten.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CameraLayout extends LinearLayout {
    public CameraLayout(Context context) {
        super(context);
    }

    public CameraLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        layoutChild(heightSpec);
        super.onMeasure(widthSpec, heightSpec);
    }

    private void layoutChild(int heightSpec) {
        int count = getChildCount();
        if (count == 0) return;

        int height = MeasureSpec.getSize(heightSpec) / count;
        if (height == 0) return;

        int sizedChileCount = 0;
        int fixedHeight = 0;
        float weight = 0.0f;
        for (int i = 0; i < count; ++i) {
            View view = getChildAt(i);
            if (view == null) {
                continue;
            }

            LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params.weight > 0.0f) {
                sizedChileCount++;
                weight += params.weight;
                continue;
            }
            view.measure(0, 0);
            fixedHeight += view.getMeasuredHeight();
        }

        if (sizedChileCount > 0) {
            float h = fixedHeight / weight;
            for (int i = 0; i < count; ++i) {
                View view = getChildAt(i);
                if (view == null) {
                    continue;
                }
                LayoutParams params = (LayoutParams) view.getLayoutParams();
                if (params.weight == 0.0f) {
                    continue;
                }

                params.height = (int) (h * params.weight) - params.topMargin - params.bottomMargin;
                view.setLayoutParams(params);
            }
        }

//		for (int i=0; i<count;++i)
//		{
//			View view = getChildAt(i);
//			if (view == null)
//			{
//				continue;
//			}
//			LayoutParams params = (LayoutParams)view.getLayoutParams();		
//			params.height = height - params.topMargin - params.bottomMargin;
//			view.setLayoutParams(params);
//		}
    }
}
