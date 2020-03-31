package lib.grasp.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import lib.grasp.R;
import lib.grasp.widget.imagepreview.ImagePreviewAdapter;

/**
 * 查看图片弹窗
 */
public class PopViewUtil
{
    private static PopView instance;

    /** 初始化全屏popView并显示 */
    public static PopView setPopView(Activity activity, View view){
        if(activity == null) return null;
        if(instance == null) instance = new PopView(activity, view);
        else {
            instance.setView(view);
            instance.dismiss();
        }

        return instance;
    }

    public static class PopView extends PopupWindow
//        implements PhotoViewAttacher.OnPhotoTapListener
    {
        private Context             mCtx;
        private View                mView;
        private int                 mWidth;
        private int                 mHeight;


        PopView(Context context, View view) {
            super(context);
            this.mCtx = context;

            // 计算宽度和高度
            calWidthAndHeight(context);
            setWidth(mWidth);
            setHeight(mHeight);

            this.setOutsideTouchable(false);

            //sdk > 21 解决 标题栏没有办法遮罩的问题
            this.setClippingEnabled(false);
            // 设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(false);
            // 设置SelectPicPopupWindow弹出窗体动画效果
            this.setAnimationStyle(R.style.popupFromBottom);
            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0x80000000);
            // 设置SelectPicPopupWindow弹出窗体的背景
            this.setBackgroundDrawable(dw);

            setView(view);
        }

        public void setView(View view){
            if(view == null) return;
            this.mView = view;
            setContentView(view);
        }

        /** 延迟自动关闭 */
        private void doDelayDismiss(){
            if(!isShowing()) return;
            dismiss();
        }

        /**
         * 设置PopupWindow的大小
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

}
