package lib.grasp.widget.banner.dialog;

import android.content.Context;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.joanzapata.iconify.widget.IconTextView;

import lib.grasp.R;
import lib.grasp.util.ScreenUtil;
import lib.grasp.widget.banner.viewpager.BannerGrasp;


/*
        BannerGrasp mBanner = new BannerGrasp(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtil.getValueByDpi(this, 300), ScreenUtil.getValueByDpi(this, 200));
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
    private BannerGrasp mAdView;
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
        paramsItv.setMargins(0, ScreenUtil.getValueByDpi(mCtx, 80),0,0);
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
