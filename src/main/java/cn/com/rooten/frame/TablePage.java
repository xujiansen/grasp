package cn.com.rooten.frame;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;

import lib.grasp.R;
import cn.com.rooten.util.LinearLayoutHelper;
import cn.com.rooten.util.TableLayoutHelper;
import cn.com.rooten.util.Utilities;

public class TablePage extends ScrollPage {
    protected TableLayout mTableLayout = null;

    public TablePage(Context context) {
        super(context);
        mTableLayout = TableLayoutHelper.createLayout(getContext());

        int padd = Utilities.getValueByDpi(getContext(), 9);
        mTableLayout.setPadding(padd / 3, 0, padd, padd);

        LayoutParams lp = new LayoutParams(LinearLayoutHelper.MATCH_PARENT, LinearLayoutHelper.WRAP_CONTENT);
        lp.leftMargin = Utilities.getValueByDpi(getContext(), 3);
        lp.rightMargin = Utilities.getValueByDpi(getContext(), 2);
        lp.topMargin = lp.bottomMargin = Utilities.getValueByDpi(getContext(), 1);
        addView(mTableLayout, lp);
    }

    protected View addTableRow(final View view) {
        return TableLayoutHelper.addTableRow(mTableLayout, view);
    }

    protected View addTableRow(final View view, int padding) {
        return TableLayoutHelper.addTableRow(mTableLayout, view, padding);
    }

    protected View addTableRow(final View[] arrView) {
        return TableLayoutHelper.addTableRow(mTableLayout, arrView);
    }

    protected View addTableRow(final View leftView, final View rightView) {
        return TableLayoutHelper.addTableRow(mTableLayout, leftView, rightView);
    }

    protected View addTableRow(final String title, final View rightView) {
        return TableLayoutHelper.addTableRow(mTableLayout, title, rightView);
    }

    protected View addTableRow(final String title, final View rightView, int color) {
        TextView textView = TableLayoutHelper.createTextView(getContext());
        textView.setText(title == null ? "" : title);
        textView.setTextColor(color);
        return TableLayoutHelper.addTableRow(mTableLayout, textView, rightView);
    }

    protected View addTableRow(final String title, final View rightView, int sp, int color) {
        TextView textView = TableLayoutHelper.createTextView(getContext());
        textView.setText(title == null ? "" : title);
        textView.setTextColor(color);
        textView.setTextSize(sp);
        return TableLayoutHelper.addTableRow(mTableLayout, textView, rightView);
    }

    protected void createLine() {
        addTableRow(newLine());
    }

    protected void createLine(TableLayout layout) {
        TableLayoutHelper.addTableRow(layout, newLine());
    }

    private LinearLayout newLine() {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView line = new ImageView(mContext);
        line.setBackgroundResource(R.color.line);
        ;
        line.setScaleType(ScaleType.FIT_XY);

        int m = LinearLayoutHelper.MATCH_PARENT;
        LayoutParams param = new LayoutParams(m, Utilities.getValueByDpi(mContext, 1));
        param.topMargin = Utilities.getValueByDpi(mContext, 9);
        param.bottomMargin = Utilities.getValueByDpi(mContext, 12);
        layout.addView(line, param);

        return layout;
    }
}
