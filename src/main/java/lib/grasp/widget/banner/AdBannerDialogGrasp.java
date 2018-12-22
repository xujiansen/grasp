package lib.grasp.widget.banner;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.rooten.frame.AppHandler;
import cn.com.rooten.frame.IHandler;
import cn.com.rooten.util.Utilities;
import lib.grasp.R;


/*
        BannerGrasp mBanner = new BannerGrasp(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utilities.getValueByDpi(this, 300), Utilities.getValueByDpi(this, 200));
        mBanner.setLayoutParams(params);
        mBanner.setListener(this);
        mBanner.setDatas(getDatas());
        mBanner.setIsShowTitle(true);

        AdBannerDialogGrasp mBannerDialog = new AdBannerDialogGrasp(this, mBanner);
        mBannerDialog.show();
 */

/**
 * 弹窗式广告banner
 */
public class AdBannerDialogGrasp implements View.OnClickListener {
    private final int MATCH_PARENT  = LinearLayout.LayoutParams.MATCH_PARENT;
    private final int WRAP_CONTENT  = LinearLayout.LayoutParams.WRAP_CONTENT;


    private Context                 mCtx;

    /** View */
    private LinearLayout    mView;
    /** 广告View */
    private BannerGrasp     mAdView;
    /** 下面叉号 */
    private IconTextView    mItv;
    /** 弹窗 */
    private AlertDialog     mDlg;

    /** 设置广告内容View */
    public AdBannerDialogGrasp(Context ctx, BannerGrasp adView) {
        this.mAdView = adView;
        this.mCtx = ctx;
        init();
    }

    private void init(){
        mView = new LinearLayout(mCtx);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mView.setGravity(Gravity.CENTER_HORIZONTAL);
        mView.setLayoutParams(params);
        mView.setOrientation(LinearLayout.VERTICAL);

        mAdView.setCyclable(false);


        mItv = new IconTextView(mCtx);
        mItv.setText("{md-highlight-off}");
        mItv.setTextSize(30);
        mItv.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams paramsItv = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsItv.setMargins(0,Utilities.getValueByDpi(mCtx, 80),0,0);
        paramsItv.gravity = Gravity.CENTER_HORIZONTAL;
        mItv.setLayoutParams(paramsItv);
        mItv.setOnClickListener(this);

        mView.addView(mAdView);
        mView.addView(mItv);

        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, R.style.dialog_grasp);
        builder.setView(mView);
        mDlg = builder.create();
        mDlg.setCanceledOnTouchOutside(true);
        mDlg.setCancelable(true);
    }

    public void show() {
        showDlg(mDlg);
    }

    private static void showDlg(AlertDialog dlg) {
        try {
            dlg.show();
        } catch (RuntimeException e) {
        } catch (Exception e) {
        }
    }

    public static void dismiss(AlertDialog dlg) {
        if (dlg == null || !dlg.isShowing()) return;
        dlg.dismiss();
    }

    @Override
    public void onClick(View v) {
        dismiss(mDlg);
    }
}
