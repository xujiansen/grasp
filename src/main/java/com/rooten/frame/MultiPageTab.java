package com.rooten.frame;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import lib.grasp.R;

public class MultiPageTab extends TabLayout implements MultiPageMgr.onRealizeMultiTab {
    public MultiPageTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MultiPageTab(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public MultiPageTab(Context context) {
        super(context); //
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setTabTextColors(Color.WHITE, getResources().getColor(R.color.tabStripColor));
        changeIndicatorColor(getResources().getColor(R.color.tabStripColor));
    }

    public void changeIndicatorSize(int tabTextSize) {
        try {
            Field mTabStrip = getClass().getSuperclass().getDeclaredField("mTabStrip");
            mTabStrip.setAccessible(true);
            Object object = mTabStrip.get(this);

            if (!(object instanceof LinearLayout)) return;

            LinearLayout tabStripLayout = (LinearLayout) object;
            int tabCount = tabStripLayout.getChildCount();

            for (int i = 0; i < tabCount; i++) {
                LinearLayout child = (LinearLayout) tabStripLayout.getChildAt(i);
                int count = child.getChildCount();
                for (int j = 0; j < count; j++) {
                    View v = child.getChildAt(j);
                    if (!(v instanceof TextView)) continue;

                    TextView textView = (TextView) v;
                    textView.setTextSize(tabTextSize);
                }
            }
        } catch (Exception e) {
        }
    }

    public void changeIndicatorColor(int color) {
        try {
            Field mTabStrip = getClass().getSuperclass().getDeclaredField("mTabStrip");
            mTabStrip.setAccessible(true);
            Object object = mTabStrip.get(this);

            Method setSelectedIndicatorColor = object.getClass().getDeclaredMethod("setSelectedIndicatorColor", int.class);
            setSelectedIndicatorColor.setAccessible(true);
            setSelectedIndicatorColor.invoke(object, color);
        } catch (Exception e) {
        }
    }

    public void addTab(String title, Map<String, ImageView> mIconMap) {
        TabLayout.Tab tab = newTab();

        tab.setText(title);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.gaqu_tablayout_tab, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_tab_title) ;
        textView.setText(title);
        tab.setCustomView(view);

        ImageView icon = (ImageView) view.findViewById(R.id.iv_tab_red);
        if(mIconMap != null)mIconMap.put(title, icon);

        addTab(tab);

        LinearLayout ll = (LinearLayout) getChildAt(0);
        int childCount = ll.getChildCount();
        int tabWidth = 0;
        for (int i = 0; i < childCount; i++) {
            View child = ll.getChildAt(i);
            if (child == null) continue;
            child.measure(0, 0);
            tabWidth += child.getMeasuredWidth();
        }

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        setTabMode(tabWidth > screenWidth ? MODE_SCROLLABLE : MODE_FIXED);
        changeIndicatorSize(16);
    }

    public void setCurrentTab(int index) {
        int tabCount = super.getTabCount();
        if (index < 0 || index > tabCount - 1) return;
        getTabAt(index).select();
        getTabAt(index).getCustomView().setSelected(true);
    }

    public void setMultiTabSelectedListener(final MultiPageMgr.onMultiTabSelectedListener l) {
        setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                if (l != null) l.onTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    @Override
    public int getTabCount() {
        return MultiPageMgr.NO_DEFINITION;
    }

    @Override
    public void addPage(int page, String title, Map<String, ImageView> mIconMap) {
        addTab(title, mIconMap);
    }

    @Override
    public View getTabView() {
        return this;
    }

    @Override
    public void onPageSelected(int page) {
        setCurrentTab(page);
    }

    @Override
    public boolean hasIndicator() {
        return false;
    }
}
