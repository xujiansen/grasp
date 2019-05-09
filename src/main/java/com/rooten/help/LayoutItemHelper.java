package com.rooten.help;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import lib.grasp.R;
import lib.grasp.util.ScreenUtil;

import com.rooten.ctrl.widget.RoundImageView;
import com.rooten.ctrl.tab.AppTabTextView;
import com.rooten.util.IconifyUtil;
import com.rooten.util.LinearLayoutHelper;

/**
 * 带间隔的线性布局项
 */
public class LayoutItemHelper {
    public static final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;
    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;

    public static final int TAG_TEXT_NAME = 10001;
    public static final int TAG_TEXT_VALUE = 10002;
    public static final int TAG_ICON_CALL = 10003;
    public static final int TAG_ICON_SMS = 10004;
    public static final int TAG_REPLY_HFR = 10005;
    public static final int TAG_REPLY_TEXT = 10006;
    public static final int TAG_REPLY_BHFR = 10007;
    public static final int TAG_REPLY_HFNR = 10008;
    public static final int TAG_FUNCTION_ICON = 10009;
    public static final int TAG_FUNCTION_NAME = 10010;
    public static final int TAG_FUNCTION_NOTI = 10011;
    public static final int TAG_HEAD_FRAME = 10012;
    public static final int TAG_FUNCTION_HEAD = 10013;
    public static final int TAG_SWITCH_TITLE = 10014;
    public static final int TAG_SWITCH_VIEW = 10015;
    public static final int TAG_CALL_HEAD = 10016;
    public static final int TAG_CALL_NAME = 10017;
    public static final int TAG_CALL_DESC = 10018;
    public static final int TAG_MULTI_USER_HEAD = 10019;
    public static final int TAG_MULTI_USER_TAG = 10020;
    public static final int TAG_TOPIC_SELECT_IMG = 10021;
    public static final int TAG_LAYOUT_IMAGE = 10022;
    public static final int TAG_FILE_BOTTOM_INFO = 10023;
    public static final int TAG_FILE_BOTTOM_BTN = 10024;

    /**
     * 创建间隔
     */
    public static void addSpace(LinearLayout ll, int spaceHeight) {
        if (ll == null) return;

        Context context = ll.getContext();
        TextView textView = new TextView(context);
        int height = ScreenUtil.getValueByDpi(context, spaceHeight);
        textView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, height);
        ll.addView(textView, params);
    }

    /**
     * 创建间隔
     */
    public static void addSpace(LinearLayout ll) {
        if (ll == null) return;

        Context context = ll.getContext();
        TextView textView = new TextView(context);
        int height = ScreenUtil.getValueByDpi(context, 18);
        textView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, height);
        ll.addView(textView, params);
    }

    /**
     * 创建横着的线
     */
    public static void addHorizontalLine(LinearLayout ll) {
        if (ll == null) return;

        ImageView line = new ImageView(ll.getContext());
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        line.setImageResource(R.drawable.ic_divider_list);
        LinearLayoutHelper.addView(ll, line, MATCH_PARENT, 1);
    }

    /**
     * 创建竖着的线
     */
    public static void addVerticalLine(LinearLayout ll) {
        if (ll == null) return;

        ImageView line = new ImageView(ll.getContext());
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        line.setImageResource(R.drawable.ic_divider_list);
        LinearLayoutHelper.addView(ll, line, 1, MATCH_PARENT);
    }

    /**
     * 创建横着的weight视图
     */
    public static void addHorizontalWeightSpace(LinearLayout ll, int weight) {
        if (ll == null) return;

        TextView space = new TextView(ll.getContext());
        space.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        params.weight = weight;
        ll.addView(space, params);
    }

    /**
     * 创建竖着的weight视图
     */
    public static void addVerticalWeightSpace(LinearLayout ll, int weight) {
        if (ll == null) return;

        TextView space = new TextView(ll.getContext());
        space.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, 0);
        params.weight = weight;
        ll.addView(space, params);
    }

    /**
     * 创建一个普通的项-name+value
     */
    public static LinearLayout addNormalItem(String tag, LinearLayout ll, String title, String text) {
        if (ll == null) return null;
        Context context = ll.getContext();
        Resources res = context.getResources();

        LinearLayout layout = createLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setClickable(true);

        // 设置内边距和最小高度
        int pad = ScreenUtil.getValueByDpi(context, 9);
        layout.setPadding(pad, 0, pad, 0);
        int height = (int) res.getDimension(R.dimen.linear_item_minheight);
        layout.setMinimumHeight(height);

        pad = ScreenUtil.getValueByDpi(context, 16);
        TextView titleView = new TextView(context);
        titleView.setPadding(0, 0, pad, 0);
        titleView.setTextSize(res.getDimension(R.dimen.text_name));
        titleView.setTextColor(res.getColorStateList(R.color.gray));
        titleView.setText(title);

        pad = ScreenUtil.getValueByDpi(context, 5);
        TextView textView = new TextView(context);
        textView.setTextSize(res.getDimension(R.dimen.text_value));
        textView.setTextColor(res.getColorStateList(R.color.black));
        textView.setText(text);
        textView.setGravity(Gravity.RIGHT);
        textView.setPadding(pad, pad, 0, pad);

        layout.setTag(tag);
        titleView.setTag(tag + "_" + TAG_TEXT_NAME);
        textView.setTag(tag + "_" + TAG_TEXT_VALUE);

        LinearLayoutHelper.addView(layout, titleView, WRAP_CONTENT, WRAP_CONTENT);
        LinearLayoutHelper.addView(layout, textView, 0, WRAP_CONTENT, 1);
        LinearLayoutHelper.addView(ll, layout, MATCH_PARENT, WRAP_CONTENT);

        return layout;
    }

    /**
     * 创建一个普通的项-name+电话+信息+value
     */
    public static LinearLayout addCallAndSmsItem(String tag, LinearLayout ll, String title, String text) {
        if (ll == null) return null;
        Context context = ll.getContext();

        LinearLayout layout = addNormalItem(tag, ll, title, text);
        LinearLayout callLayout = addTelItem(context, "md-call", Color.parseColor("#4FBF9C"));
        LinearLayout smsLayout = addTelItem(context, "md-email", Color.parseColor("#FD9F01"));

        callLayout.setVisibility(View.GONE);
        smsLayout.setVisibility(View.GONE);

        callLayout.setTag(tag + "_" + TAG_ICON_CALL);
        smsLayout.setTag(tag + "_" + TAG_ICON_SMS);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
        layout.addView(callLayout, 1, params);
        layout.addView(smsLayout, 2, params);

        return layout;
    }

    /**
     * 创建一个线性的布局平分父布局：icon+text
     */
    public static LinearLayout addLinearItem(LinearLayout ll, int minHeight, String text, String icon, int textColor) {
        if (ll == null) return null;

        Context context = ll.getContext();
        LinearLayout layout = createLayout(context);
        layout.setGravity(Gravity.CENTER);
        layout.setMinimumHeight(minHeight);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setClickable(true);

        IconTextView iconView = new IconTextView(context);
        iconView.setText("{" + icon + "}");
        iconView.setTextColor(textColor);
        iconView.setTextSize(24);

        int pad = ScreenUtil.getValueByDpi(context, 6);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTextColor(textColor);
        textView.setPadding(pad, 0, 0, 0);

        layout.addView(iconView, WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(textView, WRAP_CONTENT, WRAP_CONTENT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        params.weight = 1;
        ll.addView(layout, params);

        return layout;
    }

    private static LinearLayout addTelItem(Context context, String icon, int color) {
        LinearLayout layout = createRipperLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        int pad = ScreenUtil.getValueByDpi(context, 16);
        IconTextView iconText = new IconTextView(context);
        iconText.setText("{" + icon + "}");
        iconText.setTextColor(color);
        iconText.setTextSize(24);
        iconText.setPadding(pad, 0, pad, 0);

        layout.addView(iconText);
        return layout;
    }

    /**
     * 创建一个线性的布局平分父布局：icon+text
     */
    public static LinearLayout addSlidingMenuItem(LinearLayout ll, String tag, String icon, int iconColor, final String text, View.OnClickListener l) {
        if (ll == null) return null;

        Context context = ll.getContext();
        Resources res = context.getResources();

        LinearLayout layout = createLayout(context);
        layout.setBackgroundResource(R.drawable.list_selector_background);

        layout.setTag(tag);
        layout.setOnClickListener(l);

        int pad_left = ScreenUtil.getValueByDpi(context, 18);
        int pad_right = ScreenUtil.getValueByDpi(context, 9);
        int pad_top = ScreenUtil.getValueByDpi(context, 16);
        int pad_bottom = ScreenUtil.getValueByDpi(context, 16);
        layout.setPadding(pad_left, pad_top, pad_right, pad_bottom);

        IconTextView iconText = new IconTextView(context);
        iconText.setMinWidth(ScreenUtil.getValueByDpi(context, 36));
        iconText.setGravity(Gravity.CENTER);
        iconText.setText("{" + icon + "}");
        iconText.setTextColor(iconColor);
        iconText.setTextSize(28);

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(res.getDimension(R.dimen.home_app_sliding_menu_text));
        textView.setTextColor(Color.DKGRAY);
        int padLeft = ScreenUtil.getValueByDpi(context, 6);
        textView.setPadding(padLeft, 0, 0, 0);

        layout.addView(iconText);
        layout.addView(textView);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        ll.addView(layout, params);

        return layout;
    }

    /**
     * 创建一个线性的布局：icon+num+头像+text
     */
    public static LinearLayout addNotiAndHeadItem(LinearLayout ll, String tag, int iconRes, String value) {
        if (ll == null) return null;
        Context context = ll.getContext();
        Resources res = context.getResources();
        int itemMinHeight = (int) res.getDimension(R.dimen.listItem_minHeight);

        int pad1 = ScreenUtil.getValueByDpi(context, 12);
        int pad2 = ScreenUtil.getValueByDpi(context, 6);
        LinearLayout layout = createLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(pad1, pad2, pad1, pad2);
        layout.setMinimumHeight(itemMinHeight);
        layout.setClickable(true);
        layout.setTag(tag);

        int iconLen = ScreenUtil.getValueByDpi(context, 22);
        ImageView icon = new ImageView(context);
        icon.setImageResource(iconRes);
        icon.setTag(tag + "_" + TAG_FUNCTION_ICON);

        int pad = ScreenUtil.getValueByDpi(context, 5);
        TextView viewText = new TextView(context);
        viewText.setTextSize(res.getDimension(R.dimen.home_app_fragment_title));
        viewText.setTextColor(Color.BLACK);
        viewText.setText(value);
        viewText.setPadding(pad, 0, pad, 0);
        viewText.setTag(tag + "_" + TAG_FUNCTION_NAME);

        float textSize = res.getDimension(R.dimen.home_app_fragment_num);
        AppTabTextView num = new AppTabTextView(context);
        num.setBackgroundResource(R.drawable.pop_text_normal_bg);
        num.setMode(AppTabTextView.MODE_NUM_POP);
        num.setGravity(Gravity.CENTER);
        num.setTextColor(Color.WHITE);
        num.setTextSize(textSize);
        num.setTag(tag + "_" + TAG_FUNCTION_NOTI);

        TextView space = new TextView(context);

        ImageView bz = new ImageView(context);
        IconifyUtil.setRightArrow(bz);

        LinearLayoutHelper.addView(layout, icon, iconLen, iconLen);
        LinearLayoutHelper.addView(layout, viewText, WRAP_CONTENT, WRAP_CONTENT);
        LinearLayoutHelper.addView(layout, num, WRAP_CONTENT, WRAP_CONTENT);
        LinearLayoutHelper.addView(layout, space, 0, WRAP_CONTENT, 1);

        int len = ScreenUtil.getValueByDpi(context, 52);
        addPointImageItem(layout, tag, len);

        LinearLayoutHelper.addView(layout, bz, WRAP_CONTENT, WRAP_CONTENT);
        LinearLayoutHelper.addView(ll, layout, MATCH_PARENT, WRAP_CONTENT);

        return layout;
    }

    public static FrameLayout addPointImageItem(LinearLayout ll, String tag, int len) {
        if (ll == null) return null;
        Context context = ll.getContext();
        FrameLayout layout = new FrameLayout(context);
        layout.setTag(tag + "_" + TAG_HEAD_FRAME);

        int pad = ScreenUtil.getValueByDpi(context, 5);
        LinearLayout imgLayout = new LinearLayout(context);
        imgLayout.setOrientation(LinearLayout.VERTICAL);
        imgLayout.setPadding(pad, pad, pad, pad);

        int headLen = ScreenUtil.getValueByDpi(context, 36);
        RoundImageView imageView = new RoundImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_user_head_default);
        imageView.setTag(tag + "_" + TAG_FUNCTION_HEAD);
        imgLayout.addView(imageView, headLen, headLen);

        AppTabTextView redPop = new AppTabTextView(context);
        redPop.setBackgroundResource(R.drawable.pop_text_normal_bg);
        redPop.setMode(AppTabTextView.MODE_RED_POP);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(imgLayout, params);

        params = new FrameLayout.LayoutParams(pad, pad);
        params.gravity = Gravity.RIGHT;
        layout.addView(redPop, params);

        ll.addView(layout, WRAP_CONTENT, WRAP_CONTENT);

        return layout;
    }

    public static FrameLayout addMultiUserHeadItem(LinearLayout ll, String tag, int len) {
        if (ll == null) return null;
        Context context = ll.getContext();

        int pad = ScreenUtil.getValueByDpi(context, 3);
        FrameLayout layout = new FrameLayout(context);
        layout.setPadding(pad, pad, pad, pad);
        layout.setTag(tag);

        RoundImageView imageView = new RoundImageView(context);
        imageView.setImageResource(R.drawable.ic_user_head_default);
        imageView.setTag(tag + "_" + TAG_MULTI_USER_HEAD);

        IconTextView iconText = new IconTextView(context);
        iconText.setGravity(Gravity.CENTER);
        iconText.setTag(tag + "_" + TAG_MULTI_USER_TAG);
        iconText.setTextSize(24);

        layout.addView(imageView, len, len);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        layout.addView(iconText, params);

        ll.addView(layout, WRAP_CONTENT, WRAP_CONTENT);

        return layout;
    }

    /**
     * 创建一个回复的布局
     */
    public static LinearLayout addFunctionReplyLayout(LinearLayout ll, Object tag) {
        if (ll == null) return null;
        Context context = ll.getContext();

        LinearLayout layout = LinearLayoutHelper.createHorizontal(context);
        layout.setTag(tag);

        TextView hfrView = new TextView(context);
        hfrView.setTag(tag + "_" + TAG_REPLY_HFR);
        hfrView.setTextSize(12);
        hfrView.setTextColor(Color.parseColor("#3498db"));

        TextView replyView = new TextView(context);
        replyView.setTag(tag + "_" + TAG_REPLY_TEXT);
        replyView.setTextSize(12);
        replyView.setTextColor(Color.BLACK);

        TextView bhfrView = new TextView(context);
        bhfrView.setTag(tag + "_" + TAG_REPLY_BHFR);
        bhfrView.setTextSize(12);
        bhfrView.setTextColor(Color.parseColor("#3498db"));

        LinearLayoutHelper.addView(layout, hfrView);
        LinearLayoutHelper.addView(layout, replyView);
        LinearLayoutHelper.addView(layout, bhfrView);
        LinearLayoutHelper.addView(ll, layout, MATCH_PARENT, WRAP_CONTENT);

        return layout;
    }

    /**
     * IM设置页面切换的布局
     */
    public static LinearLayout addSwitchItem(LinearLayout ll, String tag, String text) {
        if (ll == null) return null;
        Context context = ll.getContext();
        Resources res = context.getResources();

        int pad = ScreenUtil.getValueByDpi(context, 9);
        LinearLayout layout = createLayout(context);
        layout.setMinimumHeight(ScreenUtil.getValueByDpi(context, 60));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(pad, 0, pad, 0);
        layout.setTag(tag);

        TextView textView = new TextView(context);
        textView.setTextSize(res.getDimension(R.dimen.home_app_sliding_menu_setting_name));
        textView.setTextColor(res.getColorStateList(R.color.gray));
        textView.setTag(tag + "_" + TAG_SWITCH_TITLE);
        textView.setText(text);

        SwitchCompat btn = new SwitchCompat(context);
        btn.setTag(tag + "_" + TAG_SWITCH_VIEW);

        LinearLayoutHelper.addView(layout, textView, 0, WRAP_CONTENT, 1);
        LinearLayoutHelper.addView(layout, btn, WRAP_CONTENT, WRAP_CONTENT);
        LinearLayoutHelper.addView(ll, layout, MATCH_PARENT, WRAP_CONTENT);

        return layout;
    }

    /**
     * 创建一个线性的布局：name+value+arrow
     */
    public static LinearLayout addNormalRigntArrowItem(LinearLayout ll, String tag, String name, String value) {
        if (ll == null) return null;
        Context context = ll.getContext();

        LinearLayout itemView = addNormalItem(tag, ll, name, value);

        ImageView arrowView = new ImageView(context);
        IconifyUtil.setRightArrow(arrowView);
        itemView.addView(arrowView, 2);

        return itemView;
    }

    public static TextView addSettingHead(LinearLayout ll, String text) {
        if (ll == null) return null;
        Context context = ll.getContext();
        Resources res = context.getResources();

        int padTopAndBottom = ScreenUtil.getValueByDpi(context, 8);
        int padLeft = ScreenUtil.getValueByDpi(context, 5);
        TextView textView = new TextView(context);
        textView.setPadding(padLeft, padTopAndBottom, 0, padTopAndBottom);
        textView.setBackgroundColor(res.getColor(R.color.window_color));
        textView.setTextColor(res.getColor(R.color.gray));
        textView.setTextSize(res.getDimension(R.dimen.home_app_sliding_menu_setting_head));
        TextPaint paint = textView.getPaint();
        paint.setFakeBoldText(true);
        textView.setText(text);

        LinearLayoutHelper.addView(ll, textView, MATCH_PARENT, WRAP_CONTENT);

        return textView;
    }

    /**
     * 创建一个线性布局，头像在上辅以姓名
     */
    public static LinearLayout addMultiCallPeopleItem(LinearLayout ll, String tag, String name, String desc) {
        if (ll == null) return null;
        Context context = ll.getContext();
        Resources res = context.getResources();

        LinearLayout layout = LinearLayoutHelper.createVertical(context);
        layout.setGravity(Gravity.CENTER);
        layout.setTag(tag);

        RoundImageView headView = new RoundImageView(context);
        headView.setImageResource(R.drawable.ic_user_head_default);
        headView.setType(RoundImageView.TYPE_CIRCLE);
        headView.setTag(tag + "_" + TAG_CALL_HEAD);

        TextView nameView = new TextView(context);
        nameView.setTextColor(Color.WHITE);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameView.setText(name);
        nameView.setTag(tag + "_" + TAG_CALL_NAME);

        TextView descView = new TextView(context);
        descView.setTextColor(Color.WHITE);
        descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        descView.setText(desc);
        descView.setTag(tag + "_" + TAG_CALL_DESC);

        int len = res.getDisplayMetrics().widthPixels / 4;
        int w = LinearLayoutHelper.WRAP_CONTENT;
        layout.addView(headView, len, len);
        layout.addView(nameView, w, w);
        layout.addView(descView, w, w);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, w);
        params.weight = 1;
        ll.addView(layout, params);

        return layout;
    }

    public static LinearLayout addTopicSelectView(LinearLayout ll, String tag, int len) {
        if (ll == null) return null;
        Context context = ll.getContext();

        LinearLayout linearLayout = createRipperLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setBackgroundResource(R.drawable.dotted_line);
        linearLayout.setTag(tag);

        int pad = ScreenUtil.getValueByDpi(context, 16);
        ImageView imageView = new ImageView(context);
        imageView.setPadding(pad, pad, pad, pad);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setTag(tag + "_" + TAG_TOPIC_SELECT_IMG);

        linearLayout.addView(imageView, MATCH_PARENT, MATCH_PARENT);

        LinearLayout frame = createLayout(context);
        frame.setGravity(Gravity.CENTER);
        frame.addView(linearLayout, len, len);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        p.weight = 1;
        ll.addView(frame, p);

        return linearLayout;
    }

    public static LinearLayout addLayoutImageItem(LinearLayout ll, String tag, int len) {
        if (ll == null) return null;
        Context context = ll.getContext();

        int pad = ScreenUtil.getValueByDpi(context, 1);
        LinearLayout linearLayout = createRipperLayout(context);
        linearLayout.setBackgroundColor(Color.parseColor("#c9c9c9"));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(pad, pad, pad, pad);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setTag(tag);

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setTag(tag + "_" + TAG_LAYOUT_IMAGE);

        linearLayout.addView(imageView, MATCH_PARENT, MATCH_PARENT);

        LinearLayout frame = createLayout(context);
        frame.setGravity(Gravity.CENTER);
        frame.addView(linearLayout, len, len);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, WRAP_CONTENT);
        p.weight = 1;
        ll.addView(frame, p);

        return linearLayout;
    }

    public static LinearLayout createFileSelectBottom(Context context) {
        Resources res = context.getResources();

        LinearLayout ll = LinearLayoutHelper.createVertical(context);

        ImageView line = new ImageView(context);
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        line.setImageResource(R.drawable.ic_divider_horizontal_bright_opaque);

        LinearLayout layout = LinearLayoutHelper.createHorizontal(context);
        int pad = ScreenUtil.getValueByDpi(context, 6);
        layout.setPadding(pad, 0, pad, 0);
        layout.setMinimumHeight((int) res.getDimension(R.dimen.page_bottom_tab_height));
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setBackgroundColor(Color.WHITE);

        TextView info = new TextView(context);
        info.setTextColor(Color.BLACK);
        info.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        layout.addView(info);

        TextView space = new TextView(context);
        space.setVisibility(View.INVISIBLE);
        int w = LinearLayoutHelper.WRAP_CONTENT;
        int m = LinearLayoutHelper.MATCH_PARENT;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, w);
        params.weight = 1;
        layout.addView(space, params);

        Button btnSend = (Button) LayoutInflater.from(context).inflate(R.layout.appcompat_button, null);
        btnSend.setTextSize(16);
        btnSend.setEnabled(false);
        layout.addView(btnSend);

        info.setTag(TAG_FILE_BOTTOM_INFO);
        btnSend.setTag(TAG_FILE_BOTTOM_BTN);

        params = new LinearLayout.LayoutParams(m, w);
        ll.addView(line, params);
        ll.addView(layout, params);

        return ll;
    }

    private static LinearLayout createLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setBackgroundResource(R.drawable.list_selector_background_white);
        return layout;
    }

    private static LinearLayout createRipperLayout(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LinearLayout layout = new LinearLayout(context);
            layout.setBackgroundResource(R.drawable.list_selector_background_white);
            return layout;
        } else {
            LinearLayout layout = new LinearLayout(context);
            layout.setBackgroundResource(R.drawable.list_selector_background_white);
            return layout;
//            return new LinearLayout(context, null, 0, R.style.LinearItem_selectableItemBackgroundBorderless);
        }
    }

    public static LinearLayout createEditLine(Context context) {
        final EditText editText = new EditText(context);
        Resources res = context.getResources();
        int height = ScreenUtil.getValueByDpi(context, 30);
        int margin = ScreenUtil.getValueByDpi(context, 8);

        LinearLayout ll = LinearLayoutHelper.createVertical(context);
        ll.setBackgroundColor(res.getColor(R.color.window_color));

        RelativeLayout rlInner = new RelativeLayout(context);

        Drawable fdj = ContextCompat.getDrawable(context, R.drawable.ic_toolbar_search_fdj);
        fdj.setBounds(0, 0, 60, 60);

        final ImageView btnDel = new ImageView(context);
        btnDel.setId(R.id.btn_clear);
        btnDel.setScaleType(ImageView.ScaleType.CENTER);
        btnDel.setImageResource(R.drawable.ic_toolbar_search_clear);
        btnDel.setClickable(true);
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        btnDel.setBackground(drawable);
        typedArray.recycle();
        btnDel.setVisibility(View.GONE);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp2.rightMargin = 5;
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp2.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        editText.setHint("请输入筛选条件");
        editText.setTextSize(14);
        editText.setMinHeight(height);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setCompoundDrawables(fdj, null, null, null);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) btnDel.setVisibility(View.VISIBLE);
                else btnDel.setVisibility(View.GONE);
            }
        });
        editText.setBackground(null);
        editText.setPadding(0, 8, 0, 0);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp1.addRule(RelativeLayout.LEFT_OF, R.id.btn_clear);
        lp1.addRule(RelativeLayout.RIGHT_OF, R.id.btn_search);
        lp1.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        lp1.leftMargin = 10;

        int strokeWidth = 1;    // 3dp 边框宽度
        int roundRadius = 12;   // 8dp 圆角半径
        int strokeColor = Color.parseColor("#DFDFE0");  // 边框颜色
        int fillColor = Color.parseColor("#eaeaea");  // 内部填充颜色
        GradientDrawable gd = new GradientDrawable();   // 创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        rlInner.setBackground(gd);

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params1.setMargins(margin, margin, margin, margin);
        rlInner.setLayoutParams(params1);
        rlInner.addView(btnDel, lp2);
        rlInner.addView(editText, lp1);

        ImageView line = new ImageView(context);
        line.setScaleType(ImageView.ScaleType.FIT_XY);
        line.setImageResource(R.drawable.ic_divider_horizontal_bright_opaque);

        ll.addView(rlInner);
        ll.addView(line, MATCH_PARENT, WRAP_CONTENT);

        return ll;
    }
}
