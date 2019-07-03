package com.rooten.camera;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import lib.grasp.R;
import com.rooten.util.IconifyUtil;

import lib.grasp.util.FileUtil;
import lib.grasp.widget.MessageBoxGrasp;
import uk.co.senab.photoview.PhotoView;

public class CameraImage extends AppCompatActivity implements View.OnClickListener {
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_IMAGE_MAX = "arg_max";
    public static final String ARG_IMAGE_ARRAY = "arg_imgs";

    public static final String RET_IMAGE_ARRAY = "ret_imgs";
    public static final String RET_OPERATE = "ret_operate";

    private String mTitle = "";

    private ArrayList<String> mArrImgs = null;
    private int mMaxPics = 0;
    private int mCurrentPosition = 0;

    private ViewPager mPager;
    private PageAdapter mAdapter;
    private TextView mTextView;

    private ImageView mBtnCapture;
    private ImageView mBtnDel;
    private ImageView mBtnOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_image);

        mTitle = getIntent().getStringExtra(ARG_TITLE);
        mMaxPics = getIntent().getIntExtra(ARG_IMAGE_MAX, 0);
        mArrImgs = getIntent().getStringArrayListExtra(ARG_IMAGE_ARRAY);

        mPager = (HackyViewPager) findViewById(R.id.viewPager);
        mTextView = (TextView) findViewById(R.id.text1);
        mBtnCapture = (ImageView) findViewById(R.id.btn_capture);
        mBtnDel = (ImageView) findViewById(R.id.btn_del);
        mBtnOk = (ImageView) findViewById(R.id.btn_ok);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("图片预览");
        toolbar.setBackgroundResource(R.drawable.ic_zoom_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnCapture.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);

        IconifyUtil.setIconByColor(mBtnCapture, "mdi-camera", Color.WHITE, 25);
        IconifyUtil.setIconByColor(mBtnDel, "mdi-close-circle", Color.WHITE, 25);
        IconifyUtil.setIconByColor(mBtnOk, "mdi-checkbox-marked-circle", Color.WHITE, 25);

        mAdapter = new PageAdapter();
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int page) {
                mCurrentPosition = page;
                mTextView.setText(String.format("%d/%d", mCurrentPosition + 1, mArrImgs.size()));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mPager.setCurrentItem(0);
        updateButtonState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_capture) {
            doCapture();

        } else if (i == R.id.btn_del) {
            doDelete();

        } else if (i == R.id.btn_ok) {
            doOk();

        } else {
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doDelete() {
        if (mArrImgs.size() == 0) {
            return;
        }

        View.OnClickListener listenerOk = v -> {
            File file = new File(mArrImgs.get(mCurrentPosition));
            if (!file.delete()) return;

            mArrImgs.remove(mCurrentPosition);

            int count = mArrImgs.size();
            if (count == 0) {
                mAdapter.notifyDataSetChanged();
                mPager.setAdapter(null);
                doCapture();    // 转入拍照
                return;
            } else {
                if (mCurrentPosition == count) {
                    mCurrentPosition--;
                }
            }

            mAdapter.notifyDataSetChanged();
            mPager.setAdapter(null);
            mPager.setAdapter(mAdapter);
            mPager.setCurrentItem(mCurrentPosition);
            updateButtonState();
        };
        MessageBoxGrasp.infoMsg(this, mTitle, "您确定要删除当前图片？", true, listenerOk);
    }

    private void doCapture() {
        Intent ret = new Intent();
        ret.putStringArrayListExtra(RET_IMAGE_ARRAY, mArrImgs);
        setResult(RESULT_OK, ret);
        finish();
    }

    private void doOk() {
        Intent ret = new Intent();
        ret.putStringArrayListExtra(RET_IMAGE_ARRAY, mArrImgs);
        ret.putExtra(RET_OPERATE, 0);
        setResult(RESULT_CANCELED, ret);
        finish();
    }

    private void doCancel() {
        Intent ret = new Intent();
        ret.putStringArrayListExtra(RET_IMAGE_ARRAY, mArrImgs);
        ret.putExtra(RET_OPERATE, 1);
        setResult(RESULT_CANCELED, ret);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mArrImgs.size() == 0) {
            doCancel();
            return;
        }

        View.OnClickListener listenerOk = v -> {
            for (String img : mArrImgs) {
                FileUtil.delFile(img);
            }
            mArrImgs.clear();
            mAdapter.notifyDataSetChanged();
            doCancel();
        };
        MessageBoxGrasp.infoMsg(this, mTitle, "您确定要放弃所拍图片？", true, listenerOk);
    }

    private void updateButtonState() {
        if (mArrImgs == null && mArrImgs.size() == 0) {
            return;
        }

        final int count = mArrImgs.size();
        mBtnCapture.setEnabled(count < mMaxPics);
        mBtnDel.setEnabled(count > 0);

        mTextView.setText(String.format("%d/%d", mCurrentPosition + 1, mArrImgs.size()));
    }

    private class PageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mArrImgs.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
//			if (object instanceof ImageView)
//			{
//				ImageView img = (ImageView) object;
//				img.setBackgroundDrawable(null);
//				img = null;
//			}

            if (object instanceof PhotoView) {
                PhotoView pv = (PhotoView) object;
                pv.setBackgroundDrawable(null);
                pv = null;
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Bitmap bmp = null;
            try {
                File file = new File(mArrImgs.get(position));
                Uri imageUri = Uri.fromFile(file);

                bmp = ImageLoad.loadFromUri(CameraImage.this, imageUri.toString(), 1024, 1024);

            } catch (IOException e) {
            }

//			ImageView img = new ImageView(CameraImage.this);
//			img.setScaleType(ScaleType.FIT_CENTER);
//			img.setOnClickListener(CameraImage.this);
//			img.setImageBitmap(bmp);
//			((ViewPager) container).addView(img);
//			return img;

            PhotoView pv = new PhotoView(CameraImage.this);
            pv.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            pv.setImageBitmap(bmp);
            ((ViewPager) container).addView(pv);
            return pv;
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
}
