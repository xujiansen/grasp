package lib.grasp.helper;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

import com.rooten.Constant;
import com.rooten.util.Utilities;
import lib.grasp.R;

/**
 * 1. 默认显示最后一页
 * 2. 维护与当前页的distance(单位差值)
 */
public class ViewPagerHelper {
    private Context         mContext;
    private ViewGroup       mView;
    private ViewPager       mViewPager;
    private List<View>      mDatas      = new ArrayList<>();
    private PageAdapter     mAdapter    = new PageAdapter();

    public ViewPagerHelper(Context context, OnDateSelectListener mListener, ViewGroup view, ViewPager viewPager) {
        this.mContext = context;
        this.mListener = mListener;
        this.mView = view;
        this.mViewPager = viewPager;
        init();
    }

    public void init() {
        if(mViewPager == null){
            mViewPager = new ViewPager(mContext);
        }
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageMargin(Utilities.getValueByDpi(mContext, 1));
        mViewPager.setPageMarginDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.shape_line)));

//        AppBarLayout.LayoutParams lp = new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT);
//        mViewPager.setLayoutParams(lp);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /** 本次滑动最终显示的页面标志 */
            @Override
            public void onPageSelected(int position) {
                if(mListener == null) return;
                mListener.showRenderedView(getPositionView(position, false), position + 1 - Constant.MAX_DISTANCE);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mListener == null) return;
                mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (mListener == null) return;
        for (int i = 0; i < Constant.MAX_PAGE_COUNT; i++) {
            mDatas.add(mListener.getDefaultPage());
        }
        mAdapter.notifyDataSetChanged();
        int last = Constant.MAX_DISTANCE - 1;
        mViewPager.setCurrentItem(last);        // 默认显示最后一位
    }


    class PageAdapter extends PagerAdapter {
        @Override
        public int getCount() {                                                                 //获得size
            return Constant.MAX_DISTANCE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object)                       //销毁Item
        {
            view.removeView(getPositionView(position, false));
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position)                                //实例化Item
        {
            View child = getPositionView(position, true);
            view.addView(child, 0);
//            view.setTag(position);
            if (mListener != null)
                mListener.renderViewWithCertainUnitData(child, position + 1 - Constant.MAX_DISTANCE);
            return child;
        }
    }

    public void jumpTo(int distance) {
        int index = Constant.MAX_DISTANCE + distance - 1;
        if (index < 0) index = 0;
        if (index >= Constant.MAX_DISTANCE) index = Constant.MAX_DISTANCE - 1;
        mViewPager.setCurrentItem(index);
    }

    /** 按当前index返回取余后的view */
    private View getPositionView(int position, boolean isRemoveParent) {
        if (mDatas == null) return mListener.getDefaultPage();
        int left = (position % Constant.MAX_PAGE_COUNT);
        View child = mDatas.get(left);
        if (!isRemoveParent) return child;

        ViewParent parent = child.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup childParent = (ViewGroup) parent;
            childParent.removeView(child);
        }

        return child;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public interface OnDateSelectListener {

        /** 获取默认页面,无数据 */
        View getDefaultPage();

        /** 渲染目标页 */
        void renderViewWithCertainUnitData(View view, int distance);

        /** 显示目标页之后 */
        void showRenderedView(View view, int distance);

        /** 拖动监听 */
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
    }

    private OnDateSelectListener mListener;
}
