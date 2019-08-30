package com.rooten.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import com.rooten.Constant;
import lib.grasp.R;
import com.rooten.frame.IActivityResult;
import com.rooten.frame.IResultListener;

import lib.grasp.util.FileUtil;
import lib.grasp.util.PathUtil;
import lib.grasp.widget.MessageBoxGrasp;

public class TakeCamera {
    private IActivityResult mActivityResult;
    private Context mContext;

    public interface onCameraReturnListener {
        void onCamera(List<String> arrImgs);
    }

    public TakeCamera(IActivityResult activityResult, Context cxt) {
        mActivityResult = activityResult;
        mContext = cxt;
    }

    // 常用图片大小
    public final static String AVATAR_IMAGE_SIZE    = "100x100";
    public final static String PROTRAIT_IMAGE_SIZE  = "360x480";
    public final static String DEFAULT_IMAGE_SIZE   = "640x480";
    public final static String LARGER_IMAGE_SIZE    = "1920x1080";

    // 相机参数名称
    private static final String ARG_MSGBOX_TITLE = "arg_title";
    private static final String ARG_IMAGE_MAX = "arg_max";
    private static final String ARG_IMAGE_PATH = "arg_path";
    private static final String ARG_IMAGE_SIZE = "arg_size";
    private static final String ARG_QUALITY_FILE = "arg_quality_file";
    private static final String ARG_DRAW_DATE = "arg_draw_date";
    private static final String ARG_PORTRAIT = "arg_portrait";
    private static final String ARG_SELF_SHOT = "arg_self_shot";

    // 拍照返回参数名称
    private static final String RETURN_IMAGES = "return_images";

    private String mPicPath = PathUtil.PATH_CAMERA_TEMP;
    private String mPicSize = LARGER_IMAGE_SIZE;
    private int mMaxPics = 1;
    private boolean mDrawDate = false;
    private boolean mPortrait = false;
    private int mFileQuality = 90;
    private boolean mTakeFront = true;
    private ArrayList<String> mArrImgs = new ArrayList<String>();

    public void setMax(int max) {
        mMaxPics = max;
    }

    public void setPicPath(String picPath) {
        mPicPath = picPath;
    }

    public void onCapture(final onCameraReturnListener l) {
        if (!FileUtil.ensurePathExists(mPicPath)) {
            MessageBoxGrasp.infoMsg((Activity)mContext, "图片保存目录不存在！");
            return;
        }

        String title = mContext.getResources().getString(R.string.app_name);
        int nMaxPic = mMaxPics - mArrImgs.size();
        Intent intent = new Intent(mContext, CameraActivity.class);
        intent.putExtra(ARG_MSGBOX_TITLE, title);
        intent.putExtra(ARG_IMAGE_PATH, mPicPath);
        intent.putExtra(ARG_IMAGE_MAX, nMaxPic);
        intent.putExtra(ARG_IMAGE_SIZE, mPicSize);
        intent.putExtra(ARG_DRAW_DATE, mDrawDate);
        intent.putExtra(ARG_PORTRAIT, mPortrait);
        intent.putExtra(ARG_SELF_SHOT, mTakeFront);

        if (mFileQuality > 0) {
            intent.putExtra(ARG_QUALITY_FILE, mFileQuality);
        }
        try {
            mActivityResult.startForResult(intent, new IResultListener() {
                @Override
                public void onResult(int resultCode, Intent data) {
                    if (data == null) return;
                    ArrayList<String> list = data.getStringArrayListExtra(RETURN_IMAGES);
                    if (list == null || list.size() == 0) return;

                    if (l != null) {
                        l.onCamera(list);
                    }
                }
            });
        } catch (Exception e) {
            if (Constant.APP_DEBUG) System.out.println(e);
        }
    }
}
