package com.rooten.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.rooten.BaApp;
import com.rooten.Constant;

import lib.grasp.util.ScreenUtil;

public final class TableLayoutHelper {
    private static final int MATCH_PARENT = LinearLayout.LayoutParams.FILL_PARENT;
    private static final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

    private TableLayoutHelper() {
    }

    public static TableLayout createLayout(Context ctx) {
        if (ctx == null) return null;
        return new TableLayout(ctx);
    }

    public static View addTableRow(final TableLayout tl, final View view) {
        if (tl != null || view != null) {
            LayoutParams lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (Constant.PAGE_USER_BORDER) {
                BaApp app = (BaApp) tl.getContext().getApplicationContext();
                int padding = Constant.PAGE_PADDING;
                lp.leftMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
                lp.rightMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
            }
            lp.rightMargin = ScreenUtil.getValueByDpi(tl.getContext(), 5);
            tl.addView(view, lp);
        }
        return view;
    }

    public static View addTableRow(final TableLayout tl, final View view, int padding) {
        if (tl != null || view != null) {
            LayoutParams lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            lp.leftMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
            lp.rightMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
            tl.addView(view, lp);
        }
        return view;
    }

    public static View addTableRow(final TableLayout tl, final View[] arrView) {
        if (tl != null || arrView != null) {
            TableRow tr = new TableRow(tl.getContext());
            for (int n = 0; n < arrView.length; ++n) {
                LayoutParams lp = new LayoutParams(0, WRAP_CONTENT, 1);
                if (Constant.PAGE_USER_BORDER) {
                    BaApp app = (BaApp) tl.getContext().getApplicationContext();
                    int padding = Constant.PAGE_PADDING;
                    lp.leftMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
                    lp.rightMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
                }
                lp.rightMargin = ScreenUtil.getValueByDpi(tr.getContext(), 5);
                tr.addView(arrView[n], lp);
            }
            tl.addView(tr, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            return tr;
        }
        return null;
    }


    public static View addTableRow(final TableLayout tl, final View leftView, final View rightView) {
        if (tl != null && leftView != null && rightView != null) {
            TableRow tr = new TableRow(tl.getContext());
            LayoutParams lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            if (Constant.PAGE_USER_BORDER) {
                BaApp app = (BaApp) tl.getContext().getApplicationContext();
                int padding = Constant.PAGE_PADDING;
                lp.leftMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
                lp.rightMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
            }
            lp.rightMargin = ScreenUtil.getValueByDpi(tr.getContext(), 5);
            lp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            tr.addView(leftView, lp);

            lp = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1);
            if (Constant.PAGE_USER_BORDER) {
                BaApp app = (BaApp) tl.getContext().getApplicationContext();
                int padding = Constant.PAGE_PADDING;
                lp.rightMargin = ScreenUtil.getValueByDpi(tl.getContext(), padding);
            }
            tr.addView(rightView, lp);
            tl.addView(tr, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            return tr;
        }
        return null;
    }

    public static View addTableRow(final TableLayout tl, final String title, final View rightView) {
        if (tl != null && rightView != null) {
            TextView textView = createTextView(tl.getContext(), title);
            return addTableRow(tl, textView, rightView);
        }
        return null;
    }

    public static View addTableRow(final TableLayout tl, final String title, final View rightView, int color) {
        if (tl != null && rightView != null) {
            TextView textView = createTextView(tl.getContext(), title);
            textView.setTextColor(color);
            return addTableRow(tl, textView, rightView);
        }
        return null;
    }

    public static TextView createTextView(final Context ctx, final String text) {
        TextView textView = createTextView(ctx);
        textView.setText(text == null ? "" : text);
        return textView;
    }

    public static TextView createTextView(final Context ctx) {
        TextView textView = new TextView(ctx);
        textView.setTextAppearance(ctx, android.R.style.TextAppearance_Medium);
        textView.setTextSize(20);
        textView.setGravity(Gravity.LEFT);
        textView.setTextColor(Color.parseColor("#666666"));
        return textView;
    }
}
