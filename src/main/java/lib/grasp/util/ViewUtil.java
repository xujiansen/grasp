package lib.grasp.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import lib.grasp.R;

/**
 * Created by GaQu_Dev on 2018/10/31.
 */
public class ViewUtil {

    /** 摇晃View */
    public static void shakeView(Context context, final View view) {
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_x));
    }

    /** 使长宽一直 */
    public static void makeRound(View view){
        if(view == null) return;
        view.measure(0,0);
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();
        view.setMinimumHeight(Math.max(w, h));
        view.setMinimumWidth(Math.max(w, h));
    }

    /** 获取焦点 */
    public static boolean setFocusView(final View view) {
        if (view != null) {
            return view.requestFocus();
        }
        return false;
    }
}
