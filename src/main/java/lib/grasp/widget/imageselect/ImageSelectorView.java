package lib.grasp.widget.imageselect;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.frame.widget.RoundProgressBar;
import com.rooten.ActivityEx;
import com.rooten.AppHandler;
import com.rooten.interf.IHandler;
import com.rooten.help.filehttp.FileUploadMgr;
import com.rooten.help.filehttp.HttpUploadRequest;
import com.rooten.help.filehttp.HttpUtil;

import lib.grasp.R;
import lib.grasp.helper.imgpicker.ImagePickHelper;
import lib.grasp.util.FileUtil;
import lib.grasp.util.glide.GlideUtils;
import lib.grasp.util.NumberUtil;

import static com.rooten.help.filehttp.FileUploadMgr.UploadStatus_FALIURE;
import static com.rooten.help.filehttp.FileUploadMgr.UploadStatus_SUCCESS;

/*
        ImageSelectorView mImageSelectorView = mView.findViewById(R.id.imageSelector);
        mImageSelectorView.setAct(this);
        mImageSelectorView.setOnAllUploadedListener(this);
        if(mApp.getFileUploadMgr() == null) return;
        List<HttpUploadRequest> list = mImageSelectorView.getUploadEntity();
        if(list == null||list.size() == 0){
            onAllUploadedListener();
            return;
        }
        for(HttpUploadRequest request : list){
            mApp.getFileUploadMgr().addUploadWork(UPLOAD_ID, request);
        }
 */

/**
 * 选择图片上传(进度监听)
 */
public class ImageSelectorView extends LinearLayout implements HttpUtil.onHttpProgressListener, IHandler {

    private AppHandler mHandler = new AppHandler(this);
    /**
     * 最大选择的图片数量
     */
    private int mImageCount = 4;

    /**
     * 点击进行图片选择的按钮的默认图标
     */
    private int mImageToBeClick = -1;

    /**
     * 已选中的图片列表
     */
    private List<HttpUploadRequest> mSelected = new ArrayList<>();

    /**
     * 已选中的图片列表
     */
    private List<View> mSonViews = new ArrayList<>();

    private BaApp mApp;
    private ActivityEx mAct;

    public ImageSelectorView(Context context) {
        super(context);
        init();
    }

    public ImageSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reInitView();
    }

    /**
     * 初始化上传实体
     */
    private void init() {
        initView();
    }

    /**
     * 初始化几个字View
     */
    private void initView() {
        for (int i = 0; i < mImageCount; i++) {
            View son = initItemView(null);
            mSonViews.add(son);
            addView(son);
        }
    }

    public void reInitView() {
        for (View son : mSonViews) {
            initItemView(son);
            son.setVisibility(GONE);
        }

        for (HttpUploadRequest entity : mSelected) {
            View sonView = mSonViews.get(mSelected.indexOf(entity));
            setClickListener(sonView, mSelected.indexOf(entity));
            resetSingleViewWithStatus(sonView, entity, -1);
        }

        if (mSelected.size() >= mImageCount) return;
        View sonView = mSonViews.get(mSelected.size());
        ImageView iv = sonView.findViewById(R.id.iv);  // 图片
        IconTextView itv = sonView.findViewById(R.id.icon); // 叉号
        RoundProgressBar progressBar = sonView.findViewById(R.id.progressbar);  // 进度
        IconTextView itvResult = sonView.findViewById(R.id.tv_result);  // 结果文字提示

        sonView.setVisibility(VISIBLE);
        iv.setImageDrawable(null);
        itv.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        itvResult.setVisibility(GONE);
        setClickListener(sonView, mSelected.size());
    }

    /**
     * 初始化子view的大小参数(长宽一样)
     */
    private View initItemView(View view) {
        int widthOfSons = getMeasuredWidth() / (Math.max(mImageCount, 4));
        if (view == null) view = View.inflate(getContext(), R.layout.img_select_item, null);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) params = new LayoutParams(widthOfSons, widthOfSons);
        else {
            params.height = widthOfSons;
            params.width = widthOfSons;
        }
        view.setLayoutParams(params);
        return view;
    }

    private void setClickListener(View view, final int index) {
        if (view == null) return;
        if (index < 0) return;
        final ImageView iv = view.findViewById(R.id.iv);
        IconTextView itv = view.findViewById(R.id.icon);

        // 点击选择图片
        view.setOnClickListener(v -> new ImagePickHelper(mAct)
                .doSingleSelect(true, true, (picCompressPath, base64Coder) -> {     // 点击选择图片---回调
                    if (TextUtils.isEmpty(picCompressPath)) return;
                    if (mSelected.size() <= index) {  // 新选择
                        HashMap<String, String> header = new HashMap<>();
                        header.put("token", mApp.getUserData().mToken);
                        HttpUploadRequest request = HttpUploadRequest.createDefaultReq(UUID.randomUUID().toString(), Constant.UPLOAD_PIC, new File(picCompressPath), header, new HashMap<>(), this);
                        mSelected.add(request);
                    } else {                           // 改旧的
                        mSelected.get(index).uploadFile = new File(picCompressPath);
                        mSelected.get(index).loadStatus = 0;
                    }
                    reInitView();
                }));

        itv.setOnClickListener(v -> {   // 点击删除
            if (mSelected.size() <= index) return;
            mSelected.remove(index);
            reInitView();
        });
    }

    public void setAct(ActivityEx mAct) {
        this.mAct = mAct;
        mApp = (BaApp) mAct.getApplicationContext();
    }

    public List<HttpUploadRequest> getUploadEntity() {
        return mSelected;
    }

    @Override
    public boolean isQuit() {
        return false;
    }

    @Override
    public void onProgress(String requestID, String url, long curSize, long allLen) {
        refreshLoadStatus(requestID, curSize, allLen);

        if (allLen != UploadStatus_SUCCESS && allLen != UploadStatus_FALIURE) return;

        HttpUploadRequest targetEntity = null;
        for (HttpUploadRequest entity : mSelected) {
            if (TextUtils.equals(entity.reqId, requestID)) {
                targetEntity = entity;
                break;
            }
        }
        if (targetEntity == null) return;
        targetEntity.resInfo = "下载成功";
    }

    public void refreshLoadStatus(String requestID, long curSize, long allLen) {
        Bundle bundle = new Bundle();
        bundle.putString("uuid", requestID);
        bundle.putLong("curSize", curSize);
        bundle.putLong("allLen", allLen);
        Message msg = new Message();
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 检查是否已经全部上传
     */
    private boolean checkAllUpLoaded() {
        for (HttpUploadRequest entity : mSelected) {
            if (entity.loadStatus != UploadStatus_SUCCESS) return false;
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        String uuid = com.rooten.util.Util.getString(bundle, "uuid");
        long curSize = com.rooten.util.Util.getLong(bundle, "curSize");
        long allLen = com.rooten.util.Util.getLong(bundle, "allLen");

        HttpUploadRequest targetEntity = null;
        for (HttpUploadRequest entity : mSelected) {
            if (TextUtils.equals(entity.reqId, uuid)) {
                targetEntity = entity;
                break;
            }
        }
        if (targetEntity == null) return true;
        View sonView = mSonViews.get(mSelected.indexOf(targetEntity));
        if (sonView == null) return true;

        if (allLen == UploadStatus_SUCCESS) {           // 下载成功
            targetEntity.loadStatus = UploadStatus_SUCCESS;
            resetSingleViewWithStatus(sonView, targetEntity, -1);

            if (checkAllUpLoaded()) {   // 所有的都上传成功
                if (this.mListener != null) this.mListener.onAllUploadedListener();
            }
        } else if (allLen == UploadStatus_FALIURE) {    // 下载失败
            targetEntity.loadStatus = UploadStatus_FALIURE;
            resetSingleViewWithStatus(sonView, targetEntity, 0);
        } else if (allLen > 0) {
            targetEntity.loadStatus = curSize;
            int progress = NumberUtil.getProgress(curSize, allLen);
            resetSingleViewWithStatus(sonView, targetEntity, progress);
        }
        return true;
    }

    public interface OnAllUploadedListener {
        // 全部上传成功回调
        void onAllUploadedListener();
    }

    private OnAllUploadedListener mListener;

    public void setOnAllUploadedListener(OnAllUploadedListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 获取服务器返回的所有信息(返回NULL,代表没有全部上传成功)
     */
    public List<UploadPicRes> getAllRequest() {
        List<UploadPicRes> list = new ArrayList<>();
        for (HttpUploadRequest entity : mSelected) {
            if (entity.loadStatus != FileUploadMgr.UploadStatus_SUCCESS) continue;
            try {
                UploadPicRes res = new Gson().fromJson(entity.resInfo, UploadPicRes.class);
                list.add(res);
            } catch (Exception e) {
            }
        }
        return list;
    }


    /**
     * 按不同状态显示数据
     */
    private void resetSingleViewWithStatus(View sonView, HttpUploadRequest entity, int progress) {
        if (sonView == null || entity == null) return;

        ImageView iv = sonView.findViewById(R.id.iv);  // 图片
        IconTextView itv = sonView.findViewById(R.id.icon); // 叉号
        RoundProgressBar progressBar = sonView.findViewById(R.id.progressbar);  // 进度
        IconTextView itvResult = sonView.findViewById(R.id.tv_result);  // 结果文字提示

        sonView.setVisibility(VISIBLE);

        if (!FileUtil.isFileExists(entity.uploadFile.getAbsolutePath())) { // 未选择图片
            iv.setAlpha(1f);
            iv.setImageDrawable(null);
            itv.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            itvResult.setVisibility(GONE);
        } else if (entity.loadStatus == 0) {    // 已选择图片,未上传
            iv.setAlpha(1f);
            iv.setVisibility(VISIBLE);
            itv.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            itvResult.setVisibility(GONE);
            GlideUtils.getInstance().LoadContextBitmap(getContext(), entity.uploadFile.getAbsolutePath(), iv);
        } else if (entity.loadStatus > 0) {    // 传输中
            iv.setAlpha(0.5f);
            iv.setVisibility(VISIBLE);
            itv.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);
            itvResult.setVisibility(GONE);
            if (progress >= 0) progressBar.setProgress(progress);
            GlideUtils.getInstance().LoadContextBitmap(getContext(), entity.uploadFile.getAbsolutePath(), iv);
        } else if (entity.loadStatus == UploadStatus_SUCCESS) {   // 上传成功
            iv.setAlpha(0.5f);
            iv.setVisibility(VISIBLE);
            itv.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            itvResult.setVisibility(VISIBLE);
            itvResult.setText("{md-check}");
            itvResult.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (entity.loadStatus == UploadStatus_FALIURE) {   // 上传失败
            iv.setAlpha(0.5f);
            iv.setVisibility(VISIBLE);
            itv.setVisibility(GONE);
            progressBar.setVisibility(GONE);
            itvResult.setVisibility(VISIBLE);
            itvResult.setText("{md-autorenew}");
            itvResult.setTextColor(getResources().getColor(R.color.red));
        }
    }
}
