package lib.grasp.helper;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import lib.grasp.R;
import lib.grasp.util.ScreenUtil;
import lib.grasp.widget.LastInputEditText;

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
        view.setTextColor(Color.GRAY);
        view.setText(text);
        view.setGravity(Gravity.CENTER_VERTICAL| Gravity.LEFT);
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

    /** 添加左键右值+箭头行(是否显示向右箭头/是否可编辑) */
    public static TextView addKeyValueArrowLine(Context ctx, View.OnClickListener listener, LinearLayout ll, String title, String tag, boolean isShowArrow) {
        return addKeyValueArrowLine(ctx, isShowArrow ? listener : null, ll, title, "", "", tag, isShowArrow);
    }

    /** 添加左键右值+箭头行(右值, 右hint, 是否显示向右箭头/是否可编辑) */
    public static TextView addKeyValueArrowLine(Context ctx, View.OnClickListener listener, LinearLayout ll, String title, String value, String hint, String tag, boolean isShowArrow) {
        if(ll == null || TextUtils.isEmpty(title)) return null;
        View view = View.inflate(ctx, R.layout.key_value_arrow_line, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvValue = view.findViewById(R.id.value);
        tvTitle.setText(title);
        tvValue.setText(value);
        tvValue.setHint(hint);
        if(!isShowArrow) {
            tvValue.setTextColor(Color.GRAY);
            tvValue.setCompoundDrawables(null, null, null, null);
        }
        view.setTag(tag);
        view.setOnClickListener(listener);
        ll.addView(view, MATCH_PARENT, WRAP_CONTENT);
        return tvValue;
    }

    /** 添加是否行 */
    public static Switch addSwitcheInfoLine(Context ctx, CompoundButton.OnCheckedChangeListener listener, LinearLayout ll, String title, boolean value, String tag) {
        if(ll == null || TextUtils.isEmpty(title)) return null;
        View view = View.inflate(ctx, R.layout.switcher_line, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.title);
        Switch switcher = (Switch)view.findViewById(R.id.switcher);
        tvTitle.setText(title);
        switcher.setOnCheckedChangeListener(null);
        switcher.setChecked(value);
        switcher.setTag(tag);
        if(listener != null) switcher.setOnCheckedChangeListener(listener);
        ll.addView(view, MATCH_PARENT, ScreenUtil.getValueByDpi(ctx, 50));
        return switcher;
    }

    /** 添加左键右图片行(不显示箭头) */
    public static View addKeyImgLine(Context ctx, View.OnClickListener listener, LinearLayout ll, String title, int value, String tag) {
        return addKeyImgArrowLine(ctx, listener, ll, title, value, tag, ScreenUtil.getValueByDpi(ctx, 40), false);
    }

    /** 添加左键右图片+箭头行 */
    public static View addKeyImgArrowLine(Context ctx, View.OnClickListener listener, LinearLayout ll, String title, int value, String tag, int height, boolean isShowArrow) {
        if(ll == null || TextUtils.isEmpty(title)) return ll;
        View view = View.inflate(ctx, R.layout.key_value_img_arrow_line, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.title);
        ImageView ivValue = (ImageView)view.findViewById(R.id.iv_value);
        ImageView ivArrow = (ImageView)view.findViewById(R.id.iv_arrow);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.ll_ivcontainer);
        tvTitle.setText(title);
        ivArrow.setVisibility(isShowArrow ? View.VISIBLE : View.GONE);
        Glide.with(ctx).load(value).into(ivValue);
        ivValue.setClickable(false);
        ivArrow.setClickable(false);
        container.setTag(tag);
        container.setOnClickListener(listener);
        view.setTag(tag);
        view.setOnClickListener(isShowArrow ? listener : null);
        ll.addView(view, MATCH_PARENT, ScreenUtil.getValueByDpi(ctx, height));
        return ivValue;
    }

    /** 添加EditView(单)行 */
    public static EditText addEditLine(Context ctx, LinearLayout ll, String title, String value, String hint, boolean editable) {
        if(ll == null || TextUtils.isEmpty(title)) return null;
        View view = View.inflate(ctx, R.layout.editview_line, null);
        TextView tvTitle = (TextView)view.findViewById(R.id.title);
        LastInputEditText tvValue = (LastInputEditText)view.findViewById(R.id.value);
        tvTitle.setText(title);
        tvValue.setHint(hint);
        tvValue.setEnabled(editable);
        if(!editable) tvValue.setTextColor(Color.GRAY);
        if(!TextUtils.isEmpty(value)) tvValue.setText(value);
        ll.addView(view, MATCH_PARENT, ScreenUtil.getValueByDpi(ctx, 50));
        return tvValue;
    }
}
