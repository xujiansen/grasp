package lib.grasp.helper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;


import com.multi.image.selector.MultiImageSelectorActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.frame.ActivityEx;

import lib.grasp.util.FileUtil;
import lib.grasp.util.ScreenUtil;

import static android.app.Activity.RESULT_OK;

/**
 * 选择图片-裁剪图片-存储图片-压缩图片-存储图片-上传图片-显示图片
 */
public class ImagePickHelper {
    private ActivityEx mAct;
    private BaApp mApp;
    private ImageSingleSelectListener mSingleListener;
    private ImageMultiSelectListener mMultiListener;

    public static final String TAG = ImagePickHelper.class.getSimpleName();
    public static final String ARG_DEST_PATH = TAG + "ARG_DEST_PATH";

    public ImagePickHelper(ActivityEx mAct) {
        this.mAct = mAct;
        this.mApp = (BaApp) mAct.getApplication();
    }

    /**
     * 选择图片(一张, 可选是否压缩, 是否裁切, 是否产生BASE64)
     */
    public void doSingleSelect(boolean isToCrop, boolean isCompress, ImageSingleSelectListener mListener) {
        this.mSingleListener = mListener;

        Intent intent = new Intent(mAct, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);    // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);      // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE); // 选择模式

        mAct.startForResult(intent, (resultCode, data) -> {
            if (resultCode != RESULT_OK) return;
            String picURL = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT).get(0);
            if (isToCrop)               cropRawPhoto(Uri.fromFile(new File(picURL)), isCompress);
            else if (isCompress)        doCompressImage(picURL);
            else if (mSingleListener != null) mSingleListener.onSelected(picURL, "");
        });
    }

    /**
     * 选择图片(多张, 不能压缩,裁切)
     */
    public void doMultiSelect(int max, ImageMultiSelectListener mListener) {
        this.mMultiListener = mListener;
        Intent intent = new Intent(mAct, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, max);        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI); // 选择模式

        mAct.startForResult(intent, (resultCode, data) -> {
            if (resultCode != RESULT_OK) return;
            ArrayList<String> picURLs = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (mMultiListener != null) mMultiListener.onSelected(picURLs);
        });
    }

    /**
     * 裁剪原始的图片,返回bitmap
     */
    private void cropRawPhoto(Uri uri, boolean isCompress) {
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", ScreenUtil.getValueByDpi(mAct, 160));
        intent.putExtra("outputY", ScreenUtil.getValueByDpi(mAct, 160));
//        intent.putExtra("return-data", true);      //原本的裁剪方式

        // mUriTempFile为Uri类变量，实例化uritempFile，转化为uri方式解决问题
        final Uri mUriTempFile = Uri.parse("file://" + "/" + mApp.getUserData().getCropAvatarPath() + "/" + UUID.randomUUID() + Constant.SUFFIX_AVATAR_NAME);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriTempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        mAct.startForResult(intent, (resultCode, data) -> {
            if (resultCode != RESULT_OK) return;
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(mAct.getContentResolver().openInputStream(mUriTempFile));
                String picCropPath = saveCropImage(bitmap, mApp.getUserData().getCropAvatarPath()); // 将Bitmap储存并返回路径
                if (isCompress) {
                    doCompressImage(picCropPath);
                }
                else{
                    if(mSingleListener != null) mSingleListener.onSelected(picCropPath, "");
                }
            } catch (FileNotFoundException e) {
                System.out.println(TAG + ":" + e);
            }
        });
    }

    /**
     * 压缩图片
     */
    private void doCompressImage(String picCropPath) {
        ImageSingleSelectListener listener = (picCompressPath, base64Coder) -> {
            if (TextUtils.isEmpty(picCompressPath)) {
                Toast.makeText(mAct, "图片压缩失败, 请重试", Toast.LENGTH_SHORT).show();
                if(mSingleListener != null) mSingleListener.onSelected(picCompressPath, "");
                return;
            }
            if(mSingleListener != null) mSingleListener.onSelected(picCompressPath, base64Coder);
        };

        CompressImageTask task = new CompressImageTask(mApp, mAct, true, picCropPath);
        task.setListener(listener);
        task.executeOnExecutor(mApp.mAppThreadPool);
    }

    /**
     * bitmap存入file,并返回路径
     */
    private String saveCropImage(Bitmap photo, String savePath) {
        FileUtil.ensurePathExists(savePath);
        File avatar = new File(savePath, UUID.randomUUID().toString() + Constant.SUFFIX_AVATAR_NAME);
        FileOutputStream out;            //打开输出流 将图片数据填入文件中
        try {
            out = new FileOutputStream(avatar);
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                System.out.println(TAG + ":" + e);
            }
        } catch (FileNotFoundException e) {
            System.out.println(TAG + ":" + e);
        }
        return avatar.getAbsolutePath();
    }
}
