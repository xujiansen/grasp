package lib.grasp.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * 禁止下拉菜单的view
 */
public class ProhibitViewGroup extends ViewGroup {

    public ProhibitViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
