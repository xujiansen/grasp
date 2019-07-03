package lib.grasp.widget.imagepreview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import lib.grasp.R;
import lib.grasp.util.BarUtils;

/**
 * Created by JS_grasp on 2019/1/23.
 */
public class ImagePreviewPop extends PopupWindow
//        implements PhotoViewAttacher.OnPhotoTapListener
{

    private Context             mCtx;
    private View                mView;
    private ViewPager           mVp;
    private ImagePreviewAdapter mAdapter;
    private int mWidth;
    private int mHeight;
    private int DELAY_DISMISS_SECOND = 10;

    private List<String>        mData = new ArrayList<>();

    /** 查看图片 */
    public static void doPreviewImage(Activity activity, ArrayList<String> mSelected){
        if(mSelected == null || mSelected.size() == 0) return;
        ImagePreviewPop imagePreviewPop = new ImagePreviewPop(activity, mSelected);
        imagePreviewPop.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public ImagePreviewPop(Context context, List<String> mData) {
        super(context);
        this.mCtx = context;

        //计算宽度和高度
        calWidthAndHeight(context);
        setWidth(mWidth);
        setHeight(mHeight);

        this.setOutsideTouchable(true);

        //sdk > 21 解决 标题栏没有办法遮罩的问题
        this.setClippingEnabled(false);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popupFromBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);


        mView = LayoutInflater.from(context).inflate(R.layout.img_preview_pop, null, false);
        setContentView(mView);
        this.mData = mData;

        initView();

        mView.postDelayed(this::doDelayDismiss, DELAY_DISMISS_SECOND * 1000);
    }

    /** 延迟自动关闭 */
    private void doDelayDismiss(){
        if(!isShowing()) return;
        dismiss();
    }

    private void initView(){
        mVp = mView.findViewById(R.id.view_pager);
        mAdapter = new ImagePreviewAdapter(mCtx, mData, this::onPhotoTap);
        mVp.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置PopupWindow的大小
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        mWidth = metrics.widthPixels;
        mHeight= metrics.heightPixels + BarUtils.getStatusBarHeight() + BarUtils.getNavBarHeight();
    }

//    @Override
    public void onPhotoTap(View view, float v, float v1) {
        dismiss();
    }
}
