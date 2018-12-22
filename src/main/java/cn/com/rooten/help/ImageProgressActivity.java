package cn.com.rooten.help;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.minidev.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.rooten.util.Util;
import lib.grasp.R;
import cn.com.rooten.ctrl.widget.RoundProgressBar;
import cn.com.rooten.frame.ActivityEx;
import cn.com.rooten.util.Utilities;
import lib.grasp.util.GlideUtils;
import uk.co.senab.photoview.PhotoView;


public class ImageProgressActivity extends ActivityEx implements
        View.OnClickListener, ViewPager.OnPageChangeListener, BroadReceiverImpl.onBroadReceiverListener {
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TextView mPageView;

    // 定义进行预览时候的状态
    public static final int STATE_DOWNING = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_FAILURE = 3;

    // 适配器
    private ImagePageAdapter mAdapter;

    // 广播的实现类
    private BroadReceiverImpl mLocalBroad;

    // 下载的图片的Json数组
    private JSONArray mDownFileArr = new JSONArray();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.image_progress);

        initWidget();
        initToolbar();
        initLocalBroad();
        initRunParams();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 注销广播的注册
        mApp.getLocalBroadMgr().unRegisterReceiver(mLocalBroad);
    }

    private void initWidget() {
        mToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.image_progress_viewpager);
        mPageView = (TextView) findViewById(R.id.image_progress_page);

        mViewPager.addOnPageChangeListener(this);
        mViewPager.setBackgroundColor(Color.BLACK);
        mPageView.setBackgroundResource(R.drawable.ic_zoom_page_bg);
    }

    private void initToolbar() {
        mToolbar.setTitle("图片浏览");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setBackgroundResource(R.drawable.ic_zoom_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().show();
    }

    private void initLocalBroad() {
        mLocalBroad = new BroadReceiverImpl(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocalBroadMgr.ACTION_BROAD_FILE_DOWNLOAD_PROGRESS);    // 文件下载进度广播
        mApp.getLocalBroadMgr().registerReceiver(mLocalBroad, filter);
    }

    private void initRunParams() {
        // 初始化适配器
        mAdapter = new ImagePageAdapter();
        mViewPager.setAdapter(mAdapter);
    }

    private void loadData() {
        String curMsgId = getIntent().getStringExtra("curMsgId");
        Serializable data = getIntent().getSerializableExtra("data");
        if (!(data instanceof ArrayList)) return;
        mDownFileArr.addAll((ArrayList) data);

        // 再次检测文件路径的存在情况，防止进入该页面的时间内，文件下载完成
        for (Object itemObj : mDownFileArr) {
            if (!(itemObj instanceof HashMap)) continue;

            HashMap obj = (HashMap) itemObj;
            String savePath = Util.getString(obj, "savePath");
            if (!Utilities.fileExists(savePath)) continue;

            obj.put("progress", 100);
            obj.put("state", STATE_SUCCESS);
        }

        mAdapter.notifyDataSetChanged();

        // 获取每个页面的数据
        HashMap<String, Object> itemData = findItemDataByMsgId(curMsgId);
        if (itemData == null) return;

        // 获取该数据项的位置
        int page = mDownFileArr.indexOf(itemData);
        if (page < 0) return;

        // 设置也标签
        String text = (page + 1) + "/" + mDownFileArr.size();
        mPageView.setText(text);
        mViewPager.setCurrentItem(page);
    }

    /**
     * 接收广播
     */
    @Override
    public void onReceive(String action, Intent intent) {
        if (!action.equals(LocalBroadMgr.ACTION_BROAD_FILE_DOWNLOAD_PROGRESS)) return;

        final int DEFAULT_PROGRESS = -9;
        Bundle data = intent.getExtras();
        if (data == null) return;

        int progress = data.getInt("progress", DEFAULT_PROGRESS);
        String saveFile = data.getString("saveFile", "");
        String msgId = data.getString("msgId", "");

        // 校验数据的正确性
        if (Utilities.isEmpty(saveFile) || Utilities.isEmpty(msgId) || progress == DEFAULT_PROGRESS)
            return;

        // 刷新页面
        updatePageByMsgId(msgId, progress);
    }

    private void updatePageByMsgId(String msgId, int progress) {
        // 获取每个页面的数据
        HashMap<String, Object> itemData = findItemDataByMsgId(msgId);
        if (itemData == null) return;

        // 获取该数据项的位置
        int page = mDownFileArr.indexOf(itemData);
        if (page < 0) return;

        if (progress >= 100) {
            itemData.put("state", ImageProgressActivity.STATE_SUCCESS);
        } else if (progress < 0) {
            itemData.put("state", ImageProgressActivity.STATE_FAILURE);
        } else {
            itemData.put("state", ImageProgressActivity.STATE_DOWNING);
            itemData.put("progress", progress);
        }

        // 刷新页面
        View itemView = mViewPager.findViewWithTag(page);
        if (itemView == null) return;
        mAdapter.updatePageItem(itemView, itemData);
    }

    private HashMap<String, Object> findItemDataByMsgId(String msgId) {
        if (Utilities.isEmpty(msgId)) return null;

        for (Object itemObj : mDownFileArr) {
            if (!(itemObj instanceof HashMap)) continue;

            HashMap obj = (HashMap) itemObj;
            String msgIdInObj = Util.getString(obj, "msgId");
            if (msgId.equals(msgIdInObj)) {
                return obj;
            }
        }
        return null;
    }

    private class ImagePageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mDownFileArr.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = View.inflate(getBaseContext(), R.layout.image_progress_load, null);
            container.addView(v);
            v.setTag(position);

            // 初始化页面
            Object itemObj = mDownFileArr.get(position);
            if (itemObj instanceof HashMap) {
                updatePageItem(v, (HashMap) itemObj);
            }
            return v;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void updatePageItem(View itemView, HashMap itemData) {
            if (itemData == null) return;

            RoundProgressBar progressBar = (RoundProgressBar) itemView.findViewById(R.id.image_progress_load_bar);
            PhotoView view = (PhotoView) itemView.findViewById(R.id.image_progress_load_imageview);

            String savePath = Util.getString(itemData, "savePath");
            int state = Util.getInt(itemData, "state", -1);
            if (Utilities.isEmpty(savePath) || state == -1) return;

            if (state == STATE_SUCCESS) {
                progressBar.setVisibility(View.GONE);
//                ImageUtil.displayLargeImage(savePath, view);
                GlideUtils.getInstance().LoadContextBitmap(ImageProgressActivity.this, savePath, view);
            } else if (state == STATE_FAILURE) {
                progressBar.setVisibility(View.GONE);
                view.setImageResource(R.drawable.ic_image_faliure_large);
            } else {
                // 下载的时候默认进度，并显示进度条，因为只能同时下载三张图片，肯定会有其余图片在等待下载
                int progress = Util.getInt(itemData, "progress", 1);
                progressBar.setProgress(progress);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onPageSelected(int page) {
        page = page + 1;
        String text = page + "/" + mDownFileArr.size();
        mPageView.setText(text);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
