package com.rooten.frame.page.pagemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Map;

import lib.grasp.R;
import lib.grasp.util.ScreenUtil;

import com.rooten.frame.page.PageIndicator;
import com.rooten.frame.page.bizpage.BasePage;
import com.rooten.frame.page.bizpage.BasePage.onVerifyAndSubmitListener;
import com.rooten.util.BaseLinearLayoutHelper;

public class MultiPageMgr extends LinearLayout implements onVerifyAndSubmitListener {
    private class Page {
        String mTitle;
        BasePage mPage;

        Page(String title, BasePage page) {
            mTitle = title;
            mPage = page;
        }

        public String getTitle() {
            return mTitle;
        }

        public BasePage getPage() {
            return mPage;
        }
    }

    private ArrayList<Page> mArrPages = new ArrayList<>();

    private ViewPager mViewPager;
    private PageAdapter mAdapter;
    private boolean mHasIndicator = true;
    private PageIndicator mIndicator;

    private int mCurPage;
    private int mWorkflowStep;

    private OnSubmitCompletedListener mListener;
    private onRealizeMultiTab mMultiTab;

    public static final int NO_DEFINITION = Integer.MAX_VALUE;

    private OnPageChangeListener mPageChangeListener;

    public MultiPageMgr(Context context) {
        super(context);
        mHasIndicator = false;
        init();
    }

    public MultiPageMgr(Context context, onRealizeMultiTab l) {
        super(context);
        mHasIndicator = l != null && l.hasIndicator();
        mMultiTab = l;
        init();
    }

    public MultiPageMgr(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(getResources().getColor(R.color.window_color));

        mViewPager = new ViewPager(getContext());
        mViewPager.setPageMargin(ScreenUtil.getValueByDpi(getContext(), 1));
        mViewPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(R.color.shape_line)));

        ViewPager.LayoutParams lp = new ViewPager.LayoutParams();
        lp.width = ViewPager.LayoutParams.MATCH_PARENT;
        lp.height = ViewPager.LayoutParams.WRAP_CONTENT;

        mAdapter = new PageAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int page) {
                if(mPageChangeListener != null) mPageChangeListener.onPageSelected(page);
                mCurPage = page;
                updateIndicator();
                if (mMultiTab != null) mMultiTab.onPageSelected(page);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mIndicator = new PageIndicator(getContext());
        mIndicator.setIndicatorListener(pos -> {
            if (pos == mCurPage) {
                return;
            }

            if (pos == -1) {
                switchPage();
                return;
            }

            showPage(pos);
        });

        final int width = BaseLinearLayoutHelper.MATCH_PARENT;
        final int height = BaseLinearLayoutHelper.WRAP_CONTENT;

        View lineTop = new View(getContext());
        lineTop.setBackgroundColor(getResources().getColor(R.color.shape_line));

        View lineBottom = new View(getContext());
        lineBottom.setBackgroundColor(getResources().getColor(R.color.shape_line));

        View tabView = mMultiTab == null ? null : mMultiTab.getTabView();
        if (tabView != null) {
            BaseLinearLayoutHelper.addView(this, tabView, width, height);
        }

        BaseLinearLayoutHelper.addView(this, lineTop, width, ScreenUtil.getValueByDpi(getContext(), 1));
        BaseLinearLayoutHelper.addView(this, mViewPager, width, height, 1);

        if (mHasIndicator) {
            BaseLinearLayoutHelper.addView(this, lineBottom, width, ScreenUtil.getValueByDpi(getContext(), 1));
            BaseLinearLayoutHelper.addView(this, mIndicator, width, height);
        }
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public int addPage(BasePage page, String title, Map<String, ImageView> mIconMap) {
        if (mMultiTab != null) {
            int curTabSize = mArrPages.size();
            int tabCount = mMultiTab.getTabCount();
            if (curTabSize >= tabCount) {
                return -1;
            }
            mMultiTab.addPage(curTabSize, title, mIconMap);
        }

        if (mViewPager == null || page == null) {
            return -1;
        }

        page.setWorkflowListener(this);

        Page newPage = new Page(title, page);
        mArrPages.add(newPage);

        mAdapter.notifyDataSetChanged();
        updateIndicator();
        return (mArrPages.size() - 1);
    }

    public int getCurPage() {
        return mCurPage;
    }

    private void updateIndicator() {
        if (!mHasIndicator) return;
        if (mArrPages.size() <= 1) {
            if (mIndicator.getVisibility() != View.GONE) {
                mIndicator.setVisibility(View.GONE);
            }
            return;
        }

        if (mIndicator.getVisibility() != View.VISIBLE) {
            mIndicator.setVisibility(View.VISIBLE);
        }
        mIndicator.setPageIndicator(mArrPages.size(), mCurPage);
    }

    public void showPage(int nPage) {
        if (mViewPager == null) return;

        int nCountPages = mArrPages.size();
        if (nPage < 0 || nPage >= nCountPages) {
            return;
        }

        mViewPager.setCurrentItem(nPage);
        updateIndicator();
        if (mMultiTab != null) mMultiTab.onPageSelected(nPage);
    }

    public BasePage getPage(int nPage) {
        int nCountPages = mArrPages.size();
        if (nPage < 0 || nPage >= nCountPages) {
            return null;
        }

        return mArrPages.get(nPage).getPage();
    }

    public void doPageVerifyAndSubmit() {
        mWorkflowStep = 0;
        getPage(mWorkflowStep).doVerifyData();
    }

    public void doPageSavaData() {
        for (Page page : mArrPages) {
            page.getPage().doSaveData();
        }
    }

    public void doPageResetData() {
        for (Page page : mArrPages) {
            if (page == null) continue;
            page.getPage().doResetData();
        }
    }

    public void doPageReleaseData() {
        for (Page page : mArrPages) {
            if (page == null) continue;
            page.getPage().doReleaseData();
        }
    }

    @Override
    public void onVerifyOrSubmitError() {
        if (mListener != null) {
            mListener.onSubmitError();
        }
        showPage(mWorkflowStep);
    }

    @Override
    public void onVerifyCompleted() {
        mWorkflowStep++;
        if (mWorkflowStep < mArrPages.size()) {
            getPage(mWorkflowStep).doVerifyData();
        } else {
            mWorkflowStep = 0;
            getPage(mWorkflowStep).doSubmitData();
        }
    }

    @Override
    public void onSubmitCompleted() {
        mWorkflowStep++;
        if (mWorkflowStep < mArrPages.size()) {
            getPage(mWorkflowStep).doSubmitData();
        } else {
            if (mListener != null) {
                mListener.onSubmitCompleted();
            }
        }
    }

    public interface OnSubmitCompletedListener {
        void onSubmitCompleted();

        void onSubmitError();
    }

    public interface onRealizeMultiTab {
        int getTabCount();

        void addPage(int page, String title, Map<String, ImageView> mIconMap);

        View getTabView();

        void onPageSelected(int page);

        boolean hasIndicator();
    }

    public interface onMultiTabSelectedListener {
        void onTabSelected(int page);
    }

    public void setOnSubmitCompletedListener(OnSubmitCompletedListener l) {
        mListener = l;
    }

    private class PageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mArrPages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mArrPages.get(position).getTitle();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View myView = mArrPages.get(position).getPage().getView();
            container.addView(myView);
            return myView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container) {
        }
    }

    private void switchPage() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AlertDialog);
        builder.setTitle("选择页面");
        builder.setNegativeButton("关闭", null);
        builder.setInverseBackgroundForced(true);
        int size = mArrPages.size();
        CharSequence[] arr = new CharSequence[size];
        int p = 0;
        for (Page page : mArrPages) {
            arr[p] = page.getTitle();
            p++;
        }
        builder.setSingleChoiceItems(arr, mCurPage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == mCurPage) {
                    return;
                }

                if (dialog != null) dialog.dismiss();

                mViewPager.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                mViewPager.playSoundEffect(SoundEffectConstants.CLICK);
                mViewPager.setCurrentItem(which);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void setPageChangeListener(OnPageChangeListener mPageChangeListener) {
        this.mPageChangeListener = mPageChangeListener;
    }
}
