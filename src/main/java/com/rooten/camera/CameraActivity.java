package com.rooten.camera;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lib.grasp.R;
import com.rooten.frame.ActivityEx;
import com.rooten.util.IconifyUtil;
import com.rooten.util.Utilities;
import lib.grasp.widget.MessageBoxGrasp;

@SuppressLint("NewApi")
public class CameraActivity extends ActivityEx implements Callback, View.OnClickListener, IHandler {
    // 相机参数名称
    public static final String ARG_MSGBOX_TITLE = "arg_title";
    public static final String ARG_IMAGE_MAX = "arg_max";
    public static final String ARG_IMAGE_PATH = "arg_path";
    public static final String ARG_IMAGE_SIZE = "arg_size";
    public static final String ARG_QUALITY_FILE = "arg_quality_file";
    public static final String ARG_DRAW_DATE = "arg_draw_date";
    public static final String ARG_PORTRAIT = "arg_portrait";
    public static final String ARG_ORIENTATION = "arg_orientation";
    public static final String ARG_SCALE = "arg_scale";
    public static final String ARG_SELF_SHOT = "arg_self_shot";
    public static final String ARG_MODE_PREVIEW = "arg_mode_preview";

    public static final String RETURN_IMAGES = "return_images";
    private static final int REQUEST_CODE = 1;

    public static final String DEFAULT_IMAGE_SIZE = "640x480";

    private int preDirection = 0;

    private String mTitle = "";
    private String mImagePath = "";
    private int mImgMax = 0;
    private String mImageSize = "";
    private int mFileQuality = 80;
    private boolean mDrawDate = false;
    private boolean mUseOrientation = true;
    private boolean mPortrait = false;
    private float mScale = 1.0f;
    private boolean mSelfShot = false;
    private boolean mModePreview = false;

    private OrientationListener mOrientationListener;
    private int mOrientation = 0;
    private boolean mFrontCamera = false;

    private ArrayList<String> mImages = new ArrayList<String>();

    private ImageView mViewSwitch;
    private ImageView mViewFlash;
    private ImageView mViewPicture;
    private RelativeLayout mBtnCapture;
    private SeekBar mBarZoom;
    private FocusRectangle mFocusRect;
    private CameraMaskView mMaskView;
    private CameraMaskViewVer mMaskViewVer;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private boolean mSupportFlash = false;
    private boolean mSupportZoom = false;
    private boolean mSupportFocus = false;
    private boolean mIsResume = false;
    private boolean mIsClickFocus = false;

    private boolean mPreviewRunning = false;
    private boolean mAutoFocus = false;

    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    private ZoomListener mZoomListener = new ZoomListener();
    private static final int ZOOM_STOPPED = 0;
    private static final int ZOOM_START = 1;
    private static final int ZOOM_STOPPING = 2;
    private static final int MAX_ZOOM_PROGRESS = 10 * 4;
    private int mZoomState = ZOOM_STOPPED;
    private boolean mSmoothZoomSupported = false;
    private int mZoomValue = 0;
    private int mZoomMax = 1;
    private int mTargetZoomValue;

    private boolean isScale = false;
    private int currentProgress = 0;
    private float middle = 0;

    private static final long FOCUS_TIME = 500;
    private static final long DELAY_TIME = 2000;
    private static final long ANIMATION_DURATION = 500;
    private final Animation mHideAnimation = new AlphaAnimation(1F, 0F);
    private final Animation mShowAnimation = new AlphaAnimation(0F, 1F);

    private Handler mHandler = null;
    private final int MSG_HIDEVIEW = 1;
    private final int MSG_FOCUS_SUC = 2;
    private final int MSG_FOCUS_ERR = 3;

    // 自定义按键
    public static final int KEYCODE_ZOOM_IN = 168;
    public static final int KEYCODE_ZOOM_OUT = 169;
    public static final int KEYCODE_CT20A4_ZOOM_IN = 140;
    public static final int KEYCODE_CT20A4_ZOOM_OUT = 141;
    private ImageView mCapture;
    private LinearLayout mViewLayoutSwitch;
    private LinearLayout mViewSwitchLayout;
    private LinearLayout mViewLayoutFlash;
    private LinearLayout mViewFlashLayout;
    private LinearLayout mViewPictureLayout;

    private void playMedia(int id) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, id);
        mediaPlayer.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        if (!loadArgs()) {
            finish();
            return;
        }

        Utilities.ensurePathExists(mImagePath);

        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        mFocusRect = (FocusRectangle) findViewById(R.id.camera_focus_rectangle);
        mViewSwitch = (ImageView) findViewById(R.id.imageView_switch);
        mViewLayoutSwitch = (LinearLayout) findViewById(R.id.imageView_layout_switch);
        mViewSwitchLayout = (LinearLayout) findViewById(R.id.imageView_switch_layout);
        mViewFlash = (ImageView) findViewById(R.id.imageView_flash);
        mViewLayoutFlash = (LinearLayout) findViewById(R.id.imageView_layout_flash);
        mViewFlashLayout = (LinearLayout) findViewById(R.id.imageView_flash_layout);
        mBtnCapture = (RelativeLayout) findViewById(R.id.btn_capture);
        mCapture = (ImageView) findViewById(R.id.capture_icon);
        mViewPicture = (ImageView) findViewById(R.id.imageView_picture);
        mViewPictureLayout = (LinearLayout) findViewById(R.id.imageView_picture_layout);
        mBarZoom = (VerticalSeekBar) findViewById(R.id.seekBar_zoom);
        mMaskView = (CameraMaskView) findViewById(R.id.camera_mask);
        mMaskViewVer = (CameraMaskViewVer) findViewById(R.id.camera_mask_ver);

        if (mSurfaceView != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        mViewSwitch.setOnClickListener(this);
        mViewFlash.setOnClickListener(this);
        mBtnCapture.setOnClickListener(this);
        mViewPicture.setOnClickListener(this);
        mViewPicture.setVisibility(View.INVISIBLE);
        mViewPictureLayout.setVisibility(View.INVISIBLE);

        IconifyUtil.setIconByColor(mViewPicture, "mdi-checkbox-marked-circle", Color.WHITE, 25);

//		mBarZoom.setVisibility(View.INVISIBLE);
        mBarZoom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mSupportZoom) return;

                mHandler.removeMessages(MSG_HIDEVIEW);

                mZoomValue = progress * mZoomMax / MAX_ZOOM_PROGRESS / 4;
                Parameters param = mCamera.getParameters();
                param.setZoom(mZoomValue);
                mCamera.setParameters(param);

                mHandler.sendEmptyMessageDelayed(MSG_HIDEVIEW, DELAY_TIME);
            }
        });

        // 屏幕翻转
        if (mUseOrientation) {
            mOrientationListener = new OrientationListener(this);
            mOrientationListener.enable();
        }

        if (mSelfShot) {
            mViewSwitch.setVisibility(View.VISIBLE);
            mViewSwitchLayout.setVisibility(View.VISIBLE);
            mViewLayoutSwitch.setVisibility(View.VISIBLE);
        }

        if (mPortrait) {
            mMaskView.setVisibility(View.VISIBLE);
            int index = mImageSize.indexOf('x');
            if (index != -1) {
                int width = Integer.parseInt(mImageSize.substring(0, index));
                int height = Integer.parseInt(mImageSize.substring(index + 1));
                mMaskView.setRatio((float) (height) / (float) (width));
                mMaskViewVer.setRatio((float) (height) / (float) (width));
                mMaskView.setScale(mScale);
                mMaskViewVer.setScale(mScale);
            }
        }

        mHandler = new AppHandler(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_HIDEVIEW:
                mHandler.removeMessages(MSG_HIDEVIEW);
                break;

            case MSG_FOCUS_SUC: {
                if (!mIsClickFocus) {
                    mCamera.takePicture(null, null, mPictureCallback);
                    playMedia(R.raw.camera_click);
                } else {
                    mAutoFocus = false;
                    mFocusRect.showSuccess();
                    mFocusRect.setVisibility(View.INVISIBLE);
                }
                break;
            }

            case MSG_FOCUS_ERR: {
                mAutoFocus = false;
                mCamera.cancelAutoFocus();
                mFocusRect.setVisibilityAfterEnd(View.INVISIBLE);
                break;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                // 校验当前是否可见
                if (mFocusRect.getVisibility() == View.VISIBLE) break;

                mFocusRect.reset();
                mFocusRect.setVisibility(View.VISIBLE);
                mFocusRect.move(event.getRawX(), event.getRawY());
                mCamera.autoFocus(null);
                mIsClickFocus = true;
                doCapture();
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
                currentProgress = mBarZoom.getProgress();
                middle = spacing(event);
                isScale = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isScale) {
                    float moveAfter = spacing(event);
                    if (Math.abs(moveAfter - middle) < 10f) {
                        middle = moveAfter;
                        break;
                    }
                    int max = mBarZoom.getMax();
                    //放大
                    if (moveAfter > middle) {
                        currentProgress += 1;
                    } else {
                        currentProgress -= 1;
                    }

                    if (currentProgress >= max) {
                        currentProgress = max;
                    }

                    if (currentProgress <= 0) {
                        currentProgress = 0;
                    }

                    mBarZoom.setProgress(currentProgress);
                    middle = moveAfter;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isScale = false;
                break;
        }

        return super.onTouchEvent(event);
    }


    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOrientationListener != null) {
            mOrientationListener.enable();
        }
        mIsResume = true;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFocusRect.setVisibility(View.VISIBLE);
                mFocusRect.move(0, 0);
                mCamera.autoFocus(null);
                mIsClickFocus = true;
                doCapture();
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationListener != null) {
            mOrientationListener.disable();
            mOrientationListener = null;
        }
    }

    private boolean loadArgs() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(ARG_MSGBOX_TITLE);
        mImgMax = intent.getIntExtra(ARG_IMAGE_MAX, 0);
        mImageSize = intent.getStringExtra(ARG_IMAGE_SIZE);
        mImagePath = intent.getStringExtra(ARG_IMAGE_PATH);
        mFileQuality = intent.getIntExtra(ARG_QUALITY_FILE, 80);
        mDrawDate = intent.getBooleanExtra(ARG_DRAW_DATE, false);
        mPortrait = intent.getBooleanExtra(ARG_PORTRAIT, false);
        mScale = intent.getFloatExtra(ARG_SCALE, 1.0f);
        mSelfShot = intent.getBooleanExtra(ARG_SELF_SHOT, false);
        mModePreview = intent.getBooleanExtra(ARG_MODE_PREVIEW, false);
        mUseOrientation = intent.getBooleanExtra(ARG_ORIENTATION, true);

        if (mTitle == null || mTitle.length() == 0) {
            mTitle = "警务拍照";
        }

        if (mImageSize == null || mImageSize.length() == 0 || !isSizeValid(mImageSize)) {
            mImageSize = DEFAULT_IMAGE_SIZE;
            mPortrait = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (Camera.getNumberOfCameras() < 2) {
                mSelfShot = false;
            }
        } else {
            mSelfShot = false;
        }

        if (mPortrait) {
            mSelfShot = false;
        }

        if (mImagePath == null || !Utilities.fileExists(mImagePath)) {
            return false;
        }
        return true;
    }

    public boolean isSizeValid(String pictureSize) {
        int index = pictureSize.indexOf('x');
        if (index == -1) return false;
        int width = Integer.parseInt(pictureSize.substring(0, index));
        int height = Integer.parseInt(pictureSize.substring(index + 1));
        if (width <= 0 || height <= 0)
            return false;

        return true;
    }

    private void updateState() {
        if (mImages.size() == 0) {
            mViewPicture.setVisibility(View.INVISIBLE);
            mViewPictureLayout.setVisibility(View.INVISIBLE);
        } else {
            mViewPicture.setVisibility(View.VISIBLE);
            mViewPictureLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mImages.size() == 0) {
            super.onBackPressed();
            return;
        }

        View.OnClickListener listenerOk = v -> {
            for (String img : mImages) {
                Utilities.delFile(img);
            }
            mImages.clear();
            finish();
        };
        MessageBoxGrasp.infoMsg(this, mTitle, "您确定要放弃所拍图片？", true, listenerOk);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.imageView_switch) {
            switchCamera();

        } else if (i == R.id.imageView_flash) {
            selectFlashMode();

        } else if (i == R.id.imageView_picture) {
            switch2Picture();

        } else if (i == R.id.btn_capture) {
            mFocusRect.setVisibility(View.INVISIBLE);
            mIsClickFocus = false;
            doCapture();

        }
    }

    private void switchCamera() {
        if (!mSelfShot || mAutoFocus) {
            return;
        }

        closeCamera();

        mFrontCamera = !mFrontCamera;
        openCamera();
    }

    private void selectFlashMode() {
        if (mAutoFocus) return;

        final CameraFlashSetting mSettingMenu = CameraFlashSetting.createMenu(this);
        final ArrayList<String> modes = new ArrayList<String>();
        final ArrayList<Integer> res = new ArrayList<Integer>();

        List<String> flashModes = mCamera.getParameters().getSupportedFlashModes();
        if (isSupported(Parameters.FLASH_MODE_AUTO, flashModes)) {
            modes.add(Parameters.FLASH_MODE_AUTO);
            res.add(R.drawable.camera_mode_flash_auto_nor);
            mSettingMenu.addItem(getResources().getDrawable(R.drawable.camera_mode_flash_auto_nor), "自动");
        }

        if (isSupported(Parameters.FLASH_MODE_ON, flashModes)) {
            modes.add(Parameters.FLASH_MODE_ON);
            res.add(R.drawable.camera_mode_flash_on_nor);
            mSettingMenu.addItem(getResources().getDrawable(R.drawable.camera_mode_flash_on_nor), "开");
        }

        if (isSupported(Parameters.FLASH_MODE_OFF, flashModes)) {
            modes.add(Parameters.FLASH_MODE_OFF);
            res.add(R.drawable.camera_mode_flash_off_nor);
            mSettingMenu.addItem(getResources().getDrawable(R.drawable.camera_mode_flash_off_nor), "关");
        }

        mSettingMenu.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mFocusRect.setVisibility(View.VISIBLE);
                mFocusRect.move(0, 0);
                mCamera.autoFocus(null);
                mIsClickFocus = true;
                doCapture();
            }
        });

        mSettingMenu.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSettingMenu.dismiss();
                mViewFlash.setImageResource(res.get(position));

                mFocusRect.setVisibility(View.VISIBLE);
                mFocusRect.move(0, 0);
                mCamera.autoFocus(null);
                mIsClickFocus = true;
                doCapture();

                Parameters param = mCamera.getParameters();
                param.setFlashMode(modes.get(position));
                mCamera.setParameters(param);
            }
        });

        mFocusRect.setVisibility(View.INVISIBLE);
        mSettingMenu.showMenu(findViewById(R.id.camera_preview));
    }

    private void switch2Picture() {
        if (mAutoFocus) return;

        stopPreview();

        if (!mModePreview) {
            Intent ret = new Intent();
            ret.putStringArrayListExtra(RETURN_IMAGES, mImages);
            setResult(RESULT_OK, ret);
            finish();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, CameraImage.class);
            intent.putExtra(CameraImage.ARG_TITLE, mTitle);
            intent.putExtra(CameraImage.ARG_IMAGE_MAX, mImgMax);
            intent.putStringArrayListExtra(CameraImage.ARG_IMAGE_ARRAY, mImages);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            ArrayList<String> arr = data.getStringArrayListExtra(CameraImage.RET_IMAGE_ARRAY);
            if (resultCode == RESULT_OK)    // 继续拍照
            {
                mImages.clear();
                mImages.addAll(arr);
                updateState();
                return;
            }

            if (resultCode == RESULT_CANCELED) {
                final int imgCount = arr.size();
                if (imgCount > 0) {
                    Intent ret = new Intent();
                    ret.putStringArrayListExtra(RETURN_IMAGES, arr);
                    setResult(RESULT_OK, ret);
                } else {
                    setResult(RESULT_OK);
                }

                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doCapture() {
        if (mImages.size() == mImgMax) {
            return;
        }

        if (mAutoFocus) return;
        mAutoFocus = true;

        if (mIsClickFocus) {
            if (mSupportFocus) {
                mFocusRect.showStart(true);
                mCamera.autoFocus(mAutoFocusCallback);
            }
        } else {
            playMedia(R.raw.autofocus_ok);
            mFocusRect.showStart(true);
            mHandler.sendEmptyMessageDelayed(MSG_FOCUS_SUC, FOCUS_TIME);
        }
    }

    public boolean setCameraPictureSize(Parameters param, String pictureSize) {
        int index = pictureSize.indexOf('x');
        if (index == -1) return false;
        int width = Integer.parseInt(pictureSize.substring(0, index));
        int height = Integer.parseInt(pictureSize.substring(index + 1));

        List<Size> supported = param.getSupportedPictureSizes();
        for (Size size : supported) {
            if ((size.width == width && size.height == height)) {
                param.setPictureSize(size.width, size.height);
                return true;
            }
        }

        // 没有找到给定的照片分辨率，则找一个最接近的
        Collections.sort(supported, new Comparator<Size>() {
            public int compare(Size s0, Size s1) {
                if (s0.width != s1.width) {
                    return s0.width - s1.width;
                } else {
                    return s0.height - s1.height;
                }
            }
        });
        for (Size size : supported) {
            if ((size.width == width && size.height == height) || size.width > width) {
                param.setPictureSize(size.width, size.height);
                return true;
            }
        }

        Size last = supported.get(supported.size() - 1);
        param.setPictureSize(last.width, last.height);
        return false;
    }

    private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void startPreview() {
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }

        Parameters param = mCamera.getParameters();
        setCameraPictureSize(param, mPortrait ? DEFAULT_IMAGE_SIZE : mImageSize);

        Size size = param.getPictureSize();
        PreviewFrameLayout frameLayout = (PreviewFrameLayout) findViewById(R.id.frame_layout);
        double ratio = (double) size.width / size.height;
        if (!(ratio > 1.7f && ratio < 1.8f)) {
            View view = View.inflate(this, R.layout.camera_control, null);
            view.measure(0, 0);
            frameLayout.setMarginRight(view.getMeasuredWidth());
        }
        frameLayout.setAspectRatio((double) size.width / size.height);

        List<Size> sizes = param.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, (double) size.width / size.height);
        if (optimalSize != null) {
            param.setPreviewSize(optimalSize.width, optimalSize.height);
        }

        String whiteBalance = Parameters.WHITE_BALANCE_AUTO;
        if (isSupported(whiteBalance, param.getSupportedWhiteBalance())) {
            param.setWhiteBalance(whiteBalance);
        }

        param.setJpegQuality(100);
        mCamera.setParameters(param);
        mCamera.startPreview();

//        if (!mIsClickFocus)
//        {
//            mFocusRect.setVisibility(View.VISIBLE);
//            mFocusRect.showStart(false);
//        }

        mPreviewRunning = true;
    }

    private void stopPreview() {
        if (mPreviewRunning) {
            mCamera.stopPreview();
            mFocusRect.setVisibility(View.INVISIBLE);
            mPreviewRunning = false;
        }
    }

    private static boolean isSupported(String value, List<String> supported) {
        return supported != null && supported.indexOf(value) >= 0;
    }

    private void openCamera() {
        if (mCamera == null) {
            if (!mFrontCamera) {
                mCamera = Camera.open();
            } else {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }

            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parameters param = mCamera.getParameters();
            List<String> flashModes = param.getSupportedFlashModes();
            mSupportFlash = !mFrontCamera && (flashModes != null && flashModes.size() > 1);
            mSupportZoom = param.isZoomSupported();

            String autoFocus = Parameters.FOCUS_MODE_AUTO;
            mSupportFocus = isSupported(autoFocus, param.getSupportedFocusModes());

            if (!mSupportFlash || mFrontCamera) {
                mViewFlash.setVisibility(View.GONE);
                mViewFlashLayout.setVisibility(View.GONE);
                mViewLayoutFlash.setVisibility(View.GONE);
            } else {
                mViewFlash.setVisibility(View.VISIBLE);
                mViewFlashLayout.setVisibility(View.VISIBLE);
                mViewLayoutFlash.setVisibility(View.VISIBLE);
                param.setFlashMode(Parameters.FLASH_MODE_OFF);
                if (isSupported(Parameters.FLASH_MODE_AUTO, flashModes)) {
                    mViewFlash.setImageResource(R.drawable.camera_mode_flash_auto_nor);
                    param.setFlashMode(Parameters.FLASH_MODE_AUTO);
                } else if (isSupported(Parameters.FLASH_MODE_ON, flashModes)) {
                    mViewFlash.setImageResource(R.drawable.camera_mode_flash_on_nor);
                    param.setFlashMode(Parameters.FLASH_MODE_ON);
                }
            }

            if (mSupportZoom) {
                mCamera.setZoomChangeListener(mZoomListener);
                mZoomMax = param.getMaxZoom();
                mSmoothZoomSupported = param.isSmoothZoomSupported();

                mBarZoom.setMax(MAX_ZOOM_PROGRESS);
                mBarZoom.setProgress(0);
            }

            mCamera.setParameters(param);

            startPreview();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void closeCamera() {
        if (mCamera != null) {
            stopPreview();
            mCamera.release();
            mCamera = null;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_FOCUS:    // 防止误按，所以禁用对焦处理
                return true;

            case KeyEvent.KEYCODE_CAMERA:
                mFocusRect.setVisibility(View.VISIBLE);
                mIsClickFocus = false;
                doCapture();
                return true;

            case KEYCODE_ZOOM_OUT:
            case KEYCODE_CT20A4_ZOOM_OUT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                if (!mSupportZoom) break;

                int progress = mBarZoom.getProgress() - 1;
                if (progress >= 4) {
                    if (progress % 4 == 0) progress -= 4;
                    else progress = progress - progress % 4 - 4;
                } else {
                    progress = 0;
                }

                mBarZoom.setProgress(Math.max(0, progress));

                showView(mBarZoom);
                mHandler.sendEmptyMessageDelayed(MSG_HIDEVIEW, DELAY_TIME);
                return true;
            }

            case KEYCODE_ZOOM_IN:
            case KEYCODE_CT20A4_ZOOM_IN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_VOLUME_UP: {
                if (!mSupportZoom) break;

                int progress = mBarZoom.getProgress() + 1;
                if (progress >= 4) {
                    if (progress % 4 == 0) progress += 4;
                    else progress = progress + (8 - progress % 4);
                } else {
                    progress = 4;
                }

                mBarZoom.setProgress(Math.min(mBarZoom.getMax(), progress));

                showView(mBarZoom);
                mHandler.sendEmptyMessageDelayed(MSG_HIDEVIEW, DELAY_TIME);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        Animation a = mShowAnimation;
        a.setDuration(ANIMATION_DURATION);
        view.startAnimation(a);
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        if (view.getVisibility() == View.GONE) {
            return;
        }

        Animation a = mHideAnimation;
        a.setDuration(ANIMATION_DURATION);
        view.startAnimation(a);
        view.setVisibility(View.GONE);
    }

    private final class ZoomListener implements Camera.OnZoomChangeListener {
        public void onZoomChange(int value, boolean stopped, Camera camera) {
            if (mSmoothZoomSupported) {
                mZoomValue = value;
                if (mTargetZoomValue != value && mZoomState != ZOOM_STOPPED) {
                    mTargetZoomValue = value;
                    if (mZoomState == ZOOM_START) {
                        mZoomState = ZOOM_STOPPING;
                        mCamera.stopSmoothZoom();
                    }
                } else if (mZoomState == ZOOM_STOPPED && mTargetZoomValue != value) {
                    mTargetZoomValue = value;
                    mCamera.startSmoothZoom(value);
                    mZoomState = ZOOM_START;
                }
            }
        }
    }

    private final class AutoFocusCallback implements Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
            if (focused) {
                playMedia(R.raw.camera_focus);
                mFocusRect.showSuccess();
                mHandler.sendEmptyMessageDelayed(MSG_FOCUS_SUC, FOCUS_TIME);
            } else {
                playMedia(R.raw.autofocus_error);
                mFocusRect.showFail();
                mHandler.sendEmptyMessageDelayed(MSG_FOCUS_ERR, FOCUS_TIME);
            }
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] imageData, Camera c) {
            long dateTaken = System.currentTimeMillis();
            String dateTime = DateFormat.format("yyyy-MM-dd kk.mm.ss", dateTaken).toString();
            String fileName = mImagePath + dateTime + ".jpg";
            if (saveDataToFile(fileName, imageData)) {
                mImages.add(fileName);
                updateState();
                if (mImages.size() == mImgMax) {
                    mAutoFocus = false;
                    switch2Picture();
                    return;
                }
            }

            Bitmap src = getBitmap(imageData);
            onTakePic(src);
        }


        private Bitmap getBitmap(byte[] data) {
            if (data == null || data.length == 0)
                return null;

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        private Bitmap adjustBitmap(Bitmap src) {
            final int index = mImageSize.indexOf('x');
            if (mPortrait || index == -1) {
                return src;
            }

            final int width = Integer.parseInt(mImageSize.substring(0, index));
            final int bmpWidth = src.getWidth();
            float scale = ((float) width) / bmpWidth;
            if (scale == 1.0f) return src;

            if (scale > 1.0f) scale = 1.0f;
            Bitmap newBmp = Utilities.scaleBitmap(src, scale);
            return newBmp;
        }

        private Bitmap drawDate(Bitmap src) {
            if (!mDrawDate) return src;

            long dateTaken = System.currentTimeMillis();
            final String date = DateFormat.format("yyyy/MM/dd kk:mm", dateTaken).toString();

            TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.YELLOW);
            textPaint.setTextSize(16);
            final float textWidth = textPaint.measureText(date);

            final int width = src.getWidth();
            final int height = src.getHeight();
            Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
            Canvas canvas = new Canvas(newBmp);
            canvas.drawBitmap(src, 0, 0, null);
            src.recycle();

            final int offset = 30;
            canvas.drawText(date, width - textWidth - offset, height - offset, textPaint);
            canvas.save();
//            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            canvas = null;
            textPaint = null;
            return newBmp;
        }

        private Bitmap RotateBitmap(Bitmap src) {
            float degrees = 0;
            switch (mOrientation) {
                case 0:
                    degrees = 90;
                    break;
                case 90:
                    degrees = 180;
                    break;
                case 180:
                    degrees = 270;
                    break;
                case 270:
                    degrees = 360;
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }

        private Bitmap getPortraitBitmap(Bitmap bmp) {
            int index = mImageSize.indexOf('x');
            if (index == -1) return bmp;
            int width = Integer.parseInt(mImageSize.substring(0, index));
            int height = Integer.parseInt(mImageSize.substring(index + 1));

            int imgWidth = bmp.getWidth();
            int imgHeight = bmp.getHeight();

            int newWidth = 0;
            int newHeight = 0;

            float ratio = ((float) height / (float) width);
            if (imgWidth > imgHeight) {
                if (imgHeight / ratio <= imgWidth) {
                    newWidth = (int) (imgHeight * mScale / ratio);
                    newHeight = (int) (imgHeight * mScale);
                } else {
                    newWidth = (int) (imgWidth * mScale);
                    newHeight = (int) (newWidth * ratio);
                }
            } else {
                if (imgWidth / ratio <= imgHeight) {
                    newWidth = (int) (imgWidth * mScale / ratio);
                    newHeight = (int) (imgWidth * mScale);
                } else {
                    newWidth = (int) (imgWidth * mScale);
                    newHeight = (int) (newWidth * ratio);
                }
            }

            Bitmap tempBmp = Bitmap.createBitmap(newWidth, newHeight, Config.RGB_565);
            Canvas canvas = new Canvas(tempBmp);

            int padLeft = 0, padTop = 0;
            Rect srcRect = null;
            padLeft = (imgWidth - newWidth) / 2;
            padTop = (imgHeight - newHeight) / 2;
            srcRect = new Rect(padLeft, padTop, padLeft + newWidth, padTop + newHeight);

            RectF desRect = new RectF(0, 0, newWidth, newHeight);
            canvas.drawBitmap(bmp, srcRect, desRect, null);
            bmp.recycle();

            float scale = ((float) width) / newWidth;
            if (scale == 1.0f) {
                return tempBmp;
            }

            if (scale > 1.0f) scale = 1.0f;
            Bitmap newBmp = Utilities.scaleBitmap(tempBmp, scale);
            tempBmp.recycle();
            return newBmp;
        }

        public boolean saveDataToFile(String fileName, byte[] data) {
            try {
                Bitmap src = getBitmap(data);
                if (src == null) return false;

                Bitmap tmpBmp = adjustBitmap(src);
                if (tmpBmp != src) // 不是同一个内存对象
                {
                    src.recycle();
                }

                // 方向翻转
                if (mUseOrientation) {
                    Bitmap tmp = RotateBitmap(tmpBmp);
                    if (tmp != tmpBmp) {
                        tmpBmp.recycle();
                        tmpBmp = tmp;
                    }
                }

                // 人像切割
                if (mPortrait) {
                    Bitmap tmp = getPortraitBitmap(tmpBmp);
                    if (tmp != tmpBmp) {
                        tmpBmp.recycle();
                        tmpBmp = tmp;
                    }
                }

                Bitmap newBmp = drawDate(tmpBmp);
                FileOutputStream fos = new FileOutputStream(fileName);
                newBmp.compress(Bitmap.CompressFormat.JPEG, mFileQuality, fos);
                fos.close();
                newBmp.recycle();
                tmpBmp.recycle();
            } catch (IOException e) {
                return false;
            }
            return true;
        }
    };

    class OrientationListener extends OrientationEventListener {
        public OrientationListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) {
                return;
            }

            int currentDirect = 0;

            if (orientation >= 235 && orientation < 305) {
                currentDirect = 0;
            } else if (orientation > 325 || orientation < 35) {
                currentDirect = 1;
            } else if ((orientation >= 55 && orientation < 125)) {
                currentDirect = 2;
            } else if (orientation >= 155 && orientation < 215) {
                currentDirect = 3;
            } else {
                currentDirect = preDirection;
            }

            if (currentDirect != preDirection) {
                float start = 0;
                float end = 0;
                if (preDirection == 0 && currentDirect == 3) {
                    start = 0;
                    end = 90;
                } else if (preDirection == 3 && currentDirect == 0) {
                    start = -270;
                    end = -360;
                } else {
                    start = -preDirection * 90;
                    end = -currentDirect * 90;
                }

                Clockwise(mViewFlash, start, end);
                Clockwise(mViewPicture, start, end);
                Clockwise(mViewSwitch, start, end);
                Clockwise(mCapture, start, end);
            }

            preDirection = currentDirect;

            mOrientation = ((orientation + 45) / 90 * 90) % 360;
            if (mPortrait) {
                if (((mOrientation / 90) % 2) == 1) {
                    mMaskView.setVisibility(View.VISIBLE);
                    mMaskViewVer.setVisibility(View.INVISIBLE);
                } else {
                    mMaskView.setVisibility(View.INVISIBLE);
                    mMaskViewVer.setVisibility(View.VISIBLE);
                }
            }

            if (mSelfShot && mFrontCamera) {
                if (mOrientation == 0) {
                    mOrientation = 180;
                } else if (mOrientation == 180) {
                    mOrientation = 0;
                }
            }
        }
    }

    private void Clockwise(View view, float start, float end) {
        ObjectAnimator
                .ofFloat(view, "rotation", start, end)
                .setDuration(500)
                .start();
    }

    private void onTakePic(Bitmap src) {
        startPreview();
        mAutoFocus = false;

        // 显示最后拍摄的照片
        final ImageView imageView = (ImageView) findViewById(R.id.camera_image);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(src);

        AnimatorSet animatorSet = new AnimatorSet();//组合动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0f);

        animatorSet.setDuration(1600);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(scaleX).with(scaleY);//两个动画同时开始
        animatorSet.start();

        animatorSet.addListener(new CameraAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.btn_capture).setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.btn_capture).setEnabled(true);
                imageView.setVisibility(View.GONE);
            }
        });
    }

    private class CameraAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }
}
