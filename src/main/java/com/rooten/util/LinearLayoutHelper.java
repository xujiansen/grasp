package com.rooten.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import lib.grasp.util.ScreenUtil;

public final class LinearLayoutHelper {
    public static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    private LinearLayoutHelper() {
    }

    public static LinearLayout createHorizontal(Context ctx) {
        if (ctx == null) return null;

        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        return ll;
    }

    public static LinearLayout createVertical(Context ctx) {
        if (ctx == null) return null;

        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        return ll;
    }

    public static void addView(final LinearLayout ll, final View view) {
        if (ll != null && view != null) {
            ll.addView(view);
        }
    }

    public static void addView(final LinearLayout ll, final View view, LayoutParams lp) {
        if (ll != null && view != null && lp != null) {
            ll.addView(view, lp);
        }
    }

    public static void addView(final LinearLayout ll, final View view, int weight) {
        if (ll != null && view != null) {
            addView(ll, view, MATCH_PARENT, WRAP_CONTENT, weight);
        }
    }

    public static void addView(final LinearLayout ll, final View view, int width, int height) {
        if (ll != null && view != null) {
            ll.addView(view, new LayoutParams(width, height));
        }
    }

    public static void addView(final LinearLayout ll, final View view, int width, int height, float weight) {
        if (ll != null && view != null) {
            ll.addView(view, new LayoutParams(width, height, weight));
        }
    }

    /** 添加透明行 */
    public static void addTransportSpace(Context ctx, LinearLayout ll, int height) {
        if(ll == null) return;
        View view = new View(ctx);
        ll.addView(view, MATCH_PARENT, ScreenUtil.getValueByDpi(ctx, height));
    }

    /** 添加文字透明行 */
    public static void addTitleSpace(Context ctx, LinearLayout ll, String text, int height) {
        if(ll == null) return;
        TextView view = new TextView(ctx);
        view.setText(text);
        view.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        view.setPadding(ScreenUtil.getValueByDpi(ctx, 8), 0, ScreenUtil.getValueByDpi(ctx, 8), 0);
        ll.addView(view, MATCH_PARENT, ScreenUtil.getValueByDpi(ctx, height));
    }

    /** 添加分割线 */
    public static void addDivider(Context ctx, LinearLayout ll) {
        if(ll == null) return;
        View view = new View(ctx);
        view.setBackgroundColor(Color.LTGRAY);
        ll.addView(view, MATCH_PARENT, 1);
    }

    /** 添加分割线 */
    public static void addDividerWithMargin(Context ctx, LinearLayout ll) {
        if(ll == null) return;
        View view = new View(ctx);
        view.setBackgroundColor(Color.LTGRAY);
//        view.setPadding(ScreenUtil.getValueByDpi(ctx, 5), 0, ScreenUtil.getValueByDpi(ctx, 5), 0);
        LayoutParams lp = new LayoutParams(MATCH_PARENT, 1);
        lp.setMargins(ScreenUtil.getValueByDpi(ctx, 5), 0, ScreenUtil.getValueByDpi(ctx, 5), 0);
        ll.addView(view, lp);
    }

}
