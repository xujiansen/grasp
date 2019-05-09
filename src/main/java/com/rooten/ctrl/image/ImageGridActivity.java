package com.rooten.ctrl.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.rooten.util.Util;
import lib.grasp.R;
import com.rooten.ctrl.widget.SwipeRefreshLayout;
import com.rooten.frame.AppActivity;
import com.rooten.frame.IResultListener;

import lib.grasp.util.FileUtil;
import lib.grasp.util.GlideUtils;
import lib.grasp.util.TimeDateUtil;

public class ImageGridActivity extends AppActivity implements SwipeRefreshLayout.OnLoadListener, View.OnClickListener {
    private int MAX_NUM = 20;
    private int PAGE_SIZE = 20;
    private int SPAN_COUNT = 3;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageGridAdapter mAdapter;
    private Button mBtnPreview;
    private Button mBtnSend;
    private ArrayList<String> mData = new ArrayList<>();
    private ArrayList<String> mSelected = new ArrayList<>();

    private int mCurPage = 1;
    private int mLen = 0;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle("图片列表");

        View v = View.inflate(this, R.layout.image_grid, null);
        installView(v, 1);

        initParams();   // 初始化参数
        initWidget(v);  // 加载视图
        doQuery();      // 加载数据
        updateButton(); // 初始化按钮状态
    }

    @Override
    protected void initActionMenu(MenuItem item) {
        item.setVisible(false);
    }

    private void initParams() {
        mLen = getResources().getDisplayMetrics().widthPixels / 3;

        MAX_NUM = getIntent().getIntExtra("max_num", 20);
        ArrayList<String> image = getIntent().getStringArrayListExtra("selected_data");
        if (image != null) mSelected.addAll(image);
    }

    private void initWidget(View v) {
        initRefreshLayout(v);
        initRecyclerView(v);

        mBtnPreview = (Button) v.findViewById(R.id.image_grid_btn_preview);
        mBtnPreview.setOnClickListener(this);
        mBtnPreview.setEnabled(false);

        mBtnSend = (Button) v.findViewById(R.id.image_grid_btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnSend.setEnabled(false);

        mAdapter = new ImageGridAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRefreshLayout(View v) {
        mRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.image_grid_refresh);
        mRefreshLayout.setBottomColor(android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
        mRefreshLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_END);
        mRefreshLayout.setOnLoadListener(this);
    }

    private void initRecyclerView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.image_grid_recycle);
        GridLayoutManager gridManager = new GridLayoutManager(this, SPAN_COUNT);
        gridManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridManager);
    }

    @Override
    public void onLoad() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 停止动画效果
                mRefreshLayout.setLoading(false);

                doQuery();
            }
        }, 100);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.image_grid_btn_preview) {
            Intent intent = new Intent(getBaseContext(), ImagePreviewActivity.class);
            intent.putStringArrayListExtra("data", mSelected);
            startForResult(intent, new IResultListener() {
                @Override
                public void onResult(int resultCode, Intent data) {
                    if (resultCode != RESULT_OK) return;
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        } else if (i == R.id.image_grid_btn_send) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("data", mSelected);
            setResult(RESULT_OK, intent);
            finish();
        } else {
        }
    }

    private void updateButton() {
        int count = mSelected.size();
        if (count == 0) {
            mBtnSend.setText("确定");
            mBtnSend.setEnabled(false);
            mBtnPreview.setText("预览");
            mBtnPreview.setEnabled(false);
        } else {
            mBtnSend.setText("确定(" + count + ")");
            mBtnSend.setEnabled(true);
            mBtnPreview.setText("预览(" + count + ")");
            mBtnPreview.setEnabled(true);
        }
    }

    private void doQuery() {
        ArrayList<String> data = getIntent().getStringArrayListExtra("data");
        if (data == null || data.size() == 0) return;

        ArrayList<String> temp = new ArrayList<>();

        int start = (mCurPage - 1) * PAGE_SIZE;
        if (start < 0) return;

        for (int i = start; i < start + PAGE_SIZE; i++) {
            String filepath = Util.getString(data, i);
            if (TextUtils.isEmpty(filepath)) continue;
            if (!FileUtil.fileExists(filepath)) continue;

            File f = new File(filepath);
            if (!f.isFile()) continue;
            temp.add(filepath);
        }

        if (temp.size() == 0) return;
        mData.addAll(temp);
        temp.clear();

        int end = mData.size() - 1;
        mAdapter.notifyItemRangeChanged(start, end);
        mCurPage++;
    }

    private class ImageGridHolder extends RecyclerView.ViewHolder {
        ImageGridHolder(View itemView) {
            super(itemView);
        }

        public void loadData(final int position) {
            final View view = itemView;

            ImageView icon = (ImageView) view.findViewById(R.id.image_grid_item_icon);
            ViewGroup.LayoutParams params = icon.getLayoutParams();
            params.width = mLen;
            params.height = mLen;
            icon.setLayoutParams(params);

            final String path = mData.get(position);
//            ImageUtil.displayImage(path, icon);
            GlideUtils.getInstance().LoadContextBitmap(ImageGridActivity.this, path, icon);

            ImageView tag = (ImageView) view.findViewById(R.id.image_grid_item_tag);
            if (mSelected.contains(path)) tag.setVisibility(View.VISIBLE);
            else tag.setVisibility(View.GONE);

            TextView sizeView = (TextView) view.findViewById(R.id.image_grid_item_size);
            sizeView.setText(FileUtil.getFileSize(itemView.getContext(), path));

            File f = new File(path);
            long lastTime = f.lastModified();
            TextView timeView = (TextView) view.findViewById(R.id.image_grid_item_time);
            timeView.setText(TimeDateUtil.getDateTime(new Date(lastTime)));

            FrameLayout all = (FrameLayout) view.findViewById(R.id.image_grid_item_all);
            all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(path, view);
                }
            });
        }

        void onItemClick(String path, View view) {
            if (!FileUtil.fileExists(path)) {
                Toast.makeText(ImageGridActivity.this, "该文件不存在请重新选择！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mSelected.contains(path)) {
                mSelected.remove(path);
            } else {
                int size = mSelected.size();
                if (size == MAX_NUM) {
                    Toast.makeText(getBaseContext(), "最多只能预览" + MAX_NUM + "张图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                mSelected.add(path);
            }

            ImageView tag = (ImageView) view.findViewById(R.id.image_grid_item_tag);
            if (mSelected.contains(path)) tag.setVisibility(View.VISIBLE);
            else tag.setVisibility(View.GONE);

            updateButton();
        }
    }

    private class ImageGridAdapter extends RecyclerView.Adapter<ImageGridHolder> {
        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ImageGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = View.inflate(getBaseContext(), R.layout.image_grid_item, null);

            int w = RecyclerView.LayoutParams.WRAP_CONTENT;
            int m = RecyclerView.LayoutParams.MATCH_PARENT;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(m, w);
            itemView.setLayoutParams(params);

            return new ImageGridHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ImageGridHolder holder, int position) {
            holder.loadData(position);
        }
    }
}
