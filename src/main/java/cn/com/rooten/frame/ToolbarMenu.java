package cn.com.rooten.frame;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cn.com.rooten.BaApp;
import cn.com.rooten.ctrl.tab.AppTabView;
import lib.grasp.R;

public class ToolbarMenu implements AppTabView.onAppTabClickListener {
    private BaApp mApp;
    private Activity mAppHome;
    private Toolbar mToolbar;
    protected MenuItem mItem;
//    private TextView mTv;

    public ToolbarMenu(Activity home, Toolbar toolbar) {
        mAppHome    = home;
        mToolbar    = toolbar;
        mApp        = (BaApp) home.getApplication();
//        mTv         = (TextView) mToolbar.findViewById(R.id.tv_title);
    }

    public boolean onCreateOptionsMenu(MenuItem menu) {
        mItem = menu;
        return true;
    }

    @Override
    public void onTabClick(int index) {
        switch (index){
            case 0:{
//                mToolbar.setVisibility(View.GONE);
//                setColor(Color.WHITE);
                break;
            }
            case 1:{
//                mTv.setText("家庭圈");
//                mToolbar.setVisibility(View.VISIBLE);
//                setColor(mAppHome.getResources().getColor(R.color.colorPrimaryDark));
                break;
            }
//            case 2:{
//                mItem.setVisible(false);
//                mTv.setText("消息");
//                mToolbar.setVisibility(View.VISIBLE);
//                setColor(mAppHome.getResources().getColor(R.color.colorPrimaryDark));
//                break;
//            }
            case 2:{
//                mToolbar.setVisibility(View.GONE);
                setColor(mAppHome.getResources().getColor(R.color.colorPrimaryDark));
                break;
            }
        }
    }

    private void setColor(int color){
        if (Build.VERSION.SDK_INT >= 21) {
            mAppHome.getWindow().setNavigationBarColor(color);
            mAppHome.getWindow().setStatusBarColor(color);
        }
    }
}
