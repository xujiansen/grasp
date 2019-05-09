package com.rooten.frame;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Map;

import com.rooten.ctrl.widget.SegmentedGroup;

import lib.grasp.util.NumberUtil;
import lib.grasp.util.ScreenUtil;

public class MultiRadioTab extends SegmentedGroup implements MultiPageMgr.onRealizeMultiTab {
    private ArrayList<RadioButton> mBtnList = new ArrayList<>();

    public MultiRadioTab(Context context) {
        super(context);
        init();
    }

    public MultiRadioTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Color.WHITE);
        setOrientation(HORIZONTAL);

        int t = ScreenUtil.getValueByDpi(getContext(), 9);
        int b = ScreenUtil.getValueByDpi(getContext(), 9);
        int l = ScreenUtil.getValueByDpi(getContext(), 12);
        int r = ScreenUtil.getValueByDpi(getContext(), 12);
        setPadding(l, t, r, b);
    }

    public void addTab(String title) {
        int index = mBtnList.size() + 1;

        RadioButton btn = new RadioButton(getContext());
        btn.setId(NumberUtil.generateViewId());
        btn.setMinWidth(ScreenUtil.getValueByDpi(getContext(), 60));
        btn.setGravity(Gravity.CENTER);
        btn.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));

        int padd = ScreenUtil.getValueByDpi(getContext(), 5);
        btn.setPadding(padd, 0, padd, 0);
        btn.setTextSize(16);
        if (title != null) btn.setText(title);
        else btn.setText("标签" + index);

        int w = LayoutParams.WRAP_CONTENT;
        LayoutParams params = new LayoutParams(0, w);
        params.weight = 1;
        params.height = ScreenUtil.getValueByDpi(getContext(), 45);
        addView(btn, params);

        mBtnList.add(btn);

        checkFirst(); // 默认选中第一个
    }

    private void checkFirst() {
        if (mBtnList.size() == 0) return;

        RadioButton btn = mBtnList.get(0);
        if (btn.isChecked()) return;
        int id = btn.getId();
        check(id);
    }


    public void setMultiTabSelectedListener(final MultiPageMgr.onMultiTabSelectedListener l) {
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int pos = 0;
                for (RadioButton btn : mBtnList) {
                    int id = btn.getId();
                    if (id == checkedId) break;
                    pos++;
                }

                if (l != null) l.onTabSelected(pos);
            }
        });
    }

    @Override
    public int getTabCount() {
        return MultiPageMgr.NO_DEFINITION;
    }

    @Override
    public void addPage(int page, String title, Map<String, ImageView> mIconMap) {
        addTab(title);
    }


    @Override
    public View getTabView() {
        return this;
    }

    @Override
    public void onPageSelected(int page) {
        if (page < 0 || page > mBtnList.size() - 1) return;

        RadioButton btn = mBtnList.get(page);
        int id = btn.getId();
        check(id);
    }

    @Override
    public boolean hasIndicator() {
        return false;
    }
}
