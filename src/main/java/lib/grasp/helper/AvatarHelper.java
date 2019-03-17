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
import java.util.UUID;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.frame.ActivityEx;
import com.rooten.frame.IResultListener;
import com.rooten.util.Utilities;
import lib.grasp.util.FileUtil;

import static android.app.Activity.RESULT_OK;

/**
 * 选择图片-裁剪图片-存储图片-压缩图片-存储图片-上传图片-显示图片
 */
public class AvatarHelper {
    private ActivityEx mAct;
    private BaApp mApp;
    private CompressImageTask.onCompressListener onCompressListener;
    /** 是否需要裁切 */
    private boolean isToCrop;

    public static final String ARG_DEST_PATH = AvatarHelper.class.getSimpleName() + "ARG_DEST_PATH";

    public AvatarHelper(BaApp mApp, ActivityEx mAct, boolean isToCrop, CompressImageTask.onCompressListener onCompressListener) {
        this.mAct = mAct;
        this.mApp = mApp;
        this.isToCrop = isToCrop;
        this.onCompressListener = onCompressListener;
    }

    /**
     * 选择图片
     */
    public void doSelectPic() {
        Intent intent = new Intent(mAct, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);   // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);      // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE); // 选择模式

        mAct.startForResult(intent, new IResultListener() {
            @Override
            public void onResult(int resultCode, Intent data) {
                if (resultCode != RESULT_OK) return;
                String avatarURL = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT).get(0);
                if (isToCrop) {
                    cropRawPhoto(Uri.fromFile(new File(avatarURL)));
                } else {
                    doCompressImage(avatarURL);
                }
            }
        });
    }

    /**
     * 裁剪原始的图片,返回bitmap
     */
    public void cropRawPhoto(Uri uri) {
        final Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", Utilities.getValueByDpi(mAct, 160));
        intent.putExtra("outputY", Utilities.getValueByDpi(mAct, 160));
//        intent.putExtra("return-data", true);      //原本的裁剪方式

        //uritempFile为Uri类变量，实例化uritempFile，转化为uri方式解决问题
        final Uri mUritempFile = Uri.parse("file://" + "/" + mApp.getUserData().getCropAvatarPath() + "/" + UUID.randomUUID() + Constant.SUFFIX_AVATAR_NAME);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        mAct.startForResult(intent, new IResultListener() {
            @Override
            public void onResult(int resultCode, Intent data) {
                if (resultCode != RESULT_OK) return;
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(mAct.getContentResolver().openInputStream(mUritempFile));
                    String picCropPath = saveCropImage(bitmap, mApp.getUserData().getCropAvatarPath()); // 将Bitmap储存并返回路径
                    doCompressImage(picCropPath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 压缩图片
     */
    private void doCompressImage(String picCropPath) {
        CompressImageTask.onCompressListener listener = new CompressImageTask.onCompressListener() {
            @Override
            public void onCompressFinished(String picCompressPath, String base64Coder) {
                if (TextUtils.isEmpty(picCompressPath)) {
                    Toast.makeText(mAct, "图片压缩失败, 请重试", Toast.LENGTH_SHORT).show();
                    if(onCompressListener != null) onCompressListener.onCompressFinished(picCompressPath, "");
                    return;
                }
                if(onCompressListener != null) onCompressListener.onCompressFinished(picCompressPath, base64Coder);
            }
        };

        CompressImageTask task = new CompressImageTask(mApp, mAct, true, picCropPath);
        task.setListener(listener);
        task.executeOnExecutor(mApp.AppThreadPool);
    }

    /**
     * bitmap存入file,并返回路径
     */
    private String saveCropImage(Bitmap photo, String savePath) {
        FileUtil.ensurePathExists(savePath);
        File avatar = new File(savePath, UUID.randomUUID().toString() + Constant.SUFFIX_AVATAR_NAME);
        FileOutputStream out = null;            //打开输出流 将图片数据填入文件中
        try {
            out = new FileOutputStream(avatar);
            photo.compress(Bitmap.CompressFormat.PNG, 100, out);
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return avatar.getAbsolutePath();
    }
}
