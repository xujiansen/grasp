package com.rooten.ctrl.image;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.grasp.R;
import com.rooten.frame.AppActivity;
import com.rooten.frame.IResultListener;
import com.rooten.frame.RecycleViewPage;

import lib.grasp.util.FileUtil;
import lib.grasp.util.GlideUtils;

public class ImageManagerActivity extends AppActivity {
    private ImagePathAdapter mAdapter;
    private List<ImagePathItem> mData = new ArrayList<>();

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTitle("图片夹列表");

        ImagePathPage page = new ImagePathPage(this);
        installView(page.getView(), 1);

        // 初始化所有文件夹的数据
        getLoaderManager().initLoader(0, null, loaderCallback);
    }

    @Override
    protected void initActionMenu(MenuItem item) {
        item.setVisible(false);
    }

    private class ImagePathPage extends RecycleViewPage {
        ImagePathPage(Context context) {
            super(context);
            initPage();
        }

        @Override
        protected void initPage() {
            mAdapter = new ImagePathAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class ImagePathHolder extends RecycleViewPage.BaseHolder {
        private ImageView mIcon;
        private TextView mFolderName;
        private TextView mFolder;

        ImagePathHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.image_path_item_icon);
            mFolderName = (TextView) itemView.findViewById(R.id.image_path_item_folder_name);
            mFolder = (TextView) itemView.findViewById(R.id.image_path_item_folder);
        }

        @Override
        public void loadData(int position) {
            final ImagePathItem item = mData.get(position);
//            ImageUtil.displayImage(item.cover, mIcon);
            GlideUtils.getInstance().LoadContextBitmap(ImageManagerActivity.this, item.cover, mIcon);
            mFolderName.setText(item.getInfo());
            mFolder.setText(item.folder);

            itemView.setBackgroundResource(R.drawable.list_selector_background_white);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(item);
                }
            });
        }

        private void onItemClick(ImagePathItem item) {
            Bundle extras = new Bundle();
            Intent i = getIntent();
            if (i != null && i.getExtras() != null) {
                extras.putAll(i.getExtras());
            }

            Intent intent = new Intent(getBaseContext(), ImageGridActivity.class);
            intent.putStringArrayListExtra("data", item.arrImg);
            intent.putExtras(extras);
            startForResult(intent, new IResultListener() {
                @Override
                public void onResult(int resultCode, Intent data) {
                    if (resultCode != RESULT_OK) return;

                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        }
    }

    private class ImagePathAdapter extends RecycleViewPage.BaseAdapter {
        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        protected RecycleViewPage.BaseHolder createViewHolder() {
            View itemView = View.inflate(getBaseContext(), R.layout.image_path_item, null);
            return new ImagePathHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecycleViewPage.BaseHolder holder, int position) {
            holder.loadData(position);
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        private Map<String, ImagePathItem> mMap = new HashMap<>();

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String order = MediaStore.Images.Media.DATE_MODIFIED + " desc";
            return new CursorLoader(getBaseContext(), uri, null, null, null, order);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
            if (result == null) return;

            for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext())  // 使用游标获取结果
            {
                int uri_index = result.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA);
                String uri = result.getString(uri_index);
                File file = new File(uri);
                String parent = file.getParent();

                // 图片路径校验
                if (parent.equals("/storage")) continue;
                if (!FileUtil.fileExists(uri)) continue;
                if (!file.isFile()) continue;

                // 图片长度校验
                long len = file.length();
                if (len == 0) continue;

                if (mMap.containsKey(parent)) {
                    ImagePathItem item = mMap.get(parent);
                    if (item.arrImg.contains(uri)) continue;
                    item.arrImg.add(uri);
                } else {
                    String folderName = getFolderName(parent);
                    if (TextUtils.isEmpty(folderName)) continue;

                    ImagePathItem item = new ImagePathItem();
                    item.cover = uri;
                    item.folder = parent;
                    item.folderName = folderName;
                    item.arrImg.add(uri);
                    mMap.put(parent, item);
                }
            }

            mData.clear();
            for (Map.Entry<String, ImagePathItem> entry : mMap.entrySet()) {
                ImagePathItem item = entry.getValue();
                mData.add(item);
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private String getFolderName(String folder) {
        try {
            int index = folder.lastIndexOf("/");
            return folder.substring(index + 1, folder.length());
        } catch (Exception e) {
            return "";
        }
    }

    private class ImagePathItem {
        String cover = "";   // 封面
        String folder = "";   // 文件夹路径
        String folderName = "";   // 文件夹名称

        ArrayList<String> arrImg = new ArrayList<>();

        String getInfo() {
            return folderName + "(" + arrImg.size() + ")";
        }
    }
}
