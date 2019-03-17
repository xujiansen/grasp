package cn.com.rooten.ctrl.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.com.rooten.Constant;
import cn.com.rooten.util.Util;
import lib.grasp.R;
import cn.com.rooten.ctrl.widget.HackyViewPager;
import cn.com.rooten.frame.AppActivity;
import cn.com.rooten.frame.LinearPage;
import cn.com.rooten.frame.SinglePageMgr;
import cn.com.rooten.util.IconifyUtil;
import cn.com.rooten.util.Utilities;
import lib.grasp.util.GlideUtils;
import uk.co.senab.photoview.PhotoView;

public class ImagePreviewActivity extends AppActivity {
    private TextView mText;
    private MenuItem mItem;
    private Button mBtnSend;
    private HackyViewPager mViewPager;
    private ArrayList<Map<String, String>> mArrUris = new ArrayList<>();
    private ArrayList<String> mSelectedUri = new ArrayList<>();

    private final String SELECTED_ICON = "ion-ios-checkmark-outline";
    private final String UNSELECTED_ICON = "ion-ios-circle-outline";

    @Override
    public void initView(Bundle savedInstanceState) {
        setTitle("图片预览");

        SinglePageMgr pageMgr = new SinglePageMgr(this);
        ImagePreviewPage page = new ImagePreviewPage(this);
        pageMgr.setPage(page, "");

        installView(pageMgr, 1);
    }

    @Override
    protected void initActionMenu(MenuItem item) {
        mItem = item;
        setItemIcon(SELECTED_ICON);
    }

    private void setItemIcon(String icon) {
        IconDrawable d = IconifyUtil.getIcon(this, icon);
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        mItem.setIcon(d);
    }

    @Override
    protected void onOK() {
        if (mViewPager == null) return;

        int pos = mViewPager.getCurrentItem();
        Map<String, String> map = mArrUris.get(pos);
        boolean selected = Util.getBoolean(map.get("selected"));
        if (selected) {
            selected = false;
            setItemIcon(UNSELECTED_ICON);
        } else {
            selected = true;
            setItemIcon(SELECTED_ICON);
        }
        map.put("selected", String.valueOf(selected));
        initSelectedUri();
    }

    private void initSelectedUri() {
        mSelectedUri.clear();
        for (Map<String, String> map : mArrUris) {
            String uri = map.get("uri");
            boolean selected = Util.getBoolean(map.get("selected"));
            if (selected) mSelectedUri.add(uri);
        }

        int size = mSelectedUri.size();
        if (size == 0) {
            mBtnSend.setEnabled(false);
            mBtnSend.setText("确定");
        } else {
            mBtnSend.setEnabled(true);
            mBtnSend.setText("确定(" + size + ")");
        }
    }

    private class ImagePreviewPage extends LinearPage {
        public ImagePreviewPage(Context context) {
            super(context);
            initPage();
            initCtrl();
            loadData();
        }

        @Override
        protected void initPage() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.image_preview, null, false);
            addView(view, 1);

            mViewPager = (HackyViewPager) view.findViewById(R.id.image_preview_viewpager);
            mViewPager.setBackgroundColor(Color.BLACK);

            mText = (TextView) view.findViewById(R.id.image_preview_page);

            mBtnSend = (Button) view.findViewById(R.id.image_preview_send);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("data", mSelectedUri);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        protected void initCtrl() {
            mViewPager.addOnPageChangeListener(new HackyViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int page) {
                    page = page + 1;
                    mText.setText(page + "/" + mArrUris.size());

                    Map<String, String> map = mArrUris.get(page - 1);
                    boolean selected = Util.getBoolean(map.get("selected"));
                    setItemIcon(selected ? SELECTED_ICON : UNSELECTED_ICON);
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
            if (data == null || data.size() == 0) return;

            for (String uri : data) {
                Map<String, String> map = new HashMap<>();
                map.put("uri", uri);
                map.put("selected", "true");
                mArrUris.add(map);
            }
            ImagePageAdapter pageAdapter = new ImagePageAdapter();
            mViewPager.setAdapter(pageAdapter);

            mText.setText("1/" + mArrUris.size());
            initSelectedUri();
        }
    }

    private class ImagePageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mArrUris.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Map<String, String> map = mArrUris.get(position);
            if (mViewPager.getCurrentItem() == position && mItem != null) {
                boolean selected = Util.getBoolean(map.get("selected"));
                setItemIcon(selected ? SELECTED_ICON : UNSELECTED_ICON);
            }

            int padTopAndBottom = Utilities.getValueByDpi(getBaseContext(), 5);
            PhotoView view = new PhotoView(ImagePreviewActivity.this);
            view.setPadding(0, padTopAndBottom, 0, padTopAndBottom);
            container.addView(view);

            String uri = map.get("uri");
//            ImageUtil.displayLargeImage(uri, view);
            GlideUtils.getInstance().LoadContextBitmap(ImagePreviewActivity.this, uri, view);

            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}