package com.rooten.help;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.rooten.util.Util;
import lib.grasp.R;
import com.rooten.ctrl.widget.HackyViewPager;
import com.rooten.frame.AppActivity;
import com.rooten.frame.LinearPage;
import com.rooten.util.IconifyUtil;

import lib.grasp.util.GlideUtils;
import lib.grasp.util.ScreenUtil;
import lib.grasp.widget.MessageBoxGrasp;
import uk.co.senab.photoview.PhotoView;

/**
 * 图片的预览和编辑
 */
public class ImagePreviewAndEditActivity extends AppActivity {
    private TextView mText;
    private HackyViewPager mViewPager;

    // 数据源
    private ArrayList<String> mData = new ArrayList<>();

    @Override
    public void initView(Bundle savedInstanceState) {
        setTitle("图片编辑");

        // 设置页面背景色
        setPageBgColor(Color.BLACK);

        // 设置toolbar的背景
        mToolbar.setBackgroundResource(R.drawable.ic_zoom_title);

        ImagePreviewAndEditPage page = new ImagePreviewAndEditPage(this);
        installView(page.getView(), 1);
    }

    @Override
    protected void initActionMenu(MenuItem item) {
        final String DELETE_ICON = "md-delete";
        IconifyUtil.setMenuItemIcon(this, item, Color.WHITE, DELETE_ICON);
    }

    @Override
    protected void onOK() {
        View.OnClickListener ok = v -> {
            int curPage = mViewPager.getCurrentItem();
            mData.remove(curPage);

            if (mData.size() == 0) {
                onBack();
                return;
            }

            // 重新设置适配器
            ImagePageAdapter adapter = new ImagePageAdapter();
            mViewPager.setAdapter(null);
            mViewPager.setAdapter(adapter);

            // 显示之前被删除的位置
            if (curPage > mData.size() - 1) {
                curPage = mData.size() - 1;
            }

            // 设置选中页面
            mViewPager.setCurrentItem(curPage);

            // 设置选中位置信息
            int page = curPage + 1;
            mText.setText(page + "/" + mData.size());
        };
        MessageBoxGrasp.infoMsg(this, "提示", "您确定删除当前的图片吗？", true, ok);
    }

    @Override
    protected void onBack() {
        Intent data = new Intent();
        data.putStringArrayListExtra("data", mData);

        setResult(RESULT_OK, data);
        finish();
    }

    private class ImagePreviewAndEditPage extends LinearPage {
        ImagePreviewAndEditPage(Context context) {
            super(context);
            initPage();
            initCtrl();
            loadData();
        }

        @Override
        protected void initPage() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.image_preview_and_edit, null, false);
            addView(view, 1);

            mViewPager = (HackyViewPager) view.findViewById(R.id.image_preview_and_edit_viewpager);
            mViewPager.setBackgroundColor(Color.BLACK);

            mText = (TextView) view.findViewById(R.id.image_preview_and_edit_page);
        }

        @Override
        protected void initCtrl() {
            mViewPager.addOnPageChangeListener(new HackyViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int page) {
                    page = page + 1;
                    mText.setText(page + "/" + mData.size());
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        @Override
        public void loadData() {
            ArrayList<String> data = getIntent().getStringArrayListExtra("data");
            String curClickImage = Util.getString(getIntent(), "clickImage");
            if (data == null || data.size() == 0) return;

            // 添加图片数据
            mData.addAll(data);

            // 设置适配器
            ImagePageAdapter pageAdapter = new ImagePageAdapter();
            mViewPager.setAdapter(pageAdapter);

            // 设置默认值
            mText.setText("1/" + mData.size());

            int pos = mData.indexOf(curClickImage);
            if (pos < 0) return;
            mViewPager.setCurrentItem(pos);

            // 设置选中位置信息
            int page = pos + 1;
            mText.setText(page + "/" + mData.size());
        }
    }

    private class ImagePageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int padTopAndBottom = ScreenUtil.getValueByDpi(getBaseContext(), 5);
            PhotoView view = new PhotoView(ImagePreviewAndEditActivity.this);
            view.setPadding(0, padTopAndBottom, 0, padTopAndBottom);
            container.addView(view);

            String uri = mData.get(position);
//            ImageUtil.displayLargeImage(uri, view);
            GlideUtils.getInstance().LoadContextBitmap(ImagePreviewAndEditActivity.this, uri, view);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
