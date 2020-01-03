package com.rooten.sys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.joanzapata.iconify.IconDrawable;

import com.rooten.Constant;
import com.rooten.frame.AppActivity;
import com.rooten.frame.SinglePageMgr;
import com.rooten.util.IconifyUtil;

import lib.grasp.R;

public class AppChangePassword extends AppActivity {
    private SinglePageMgr mPageMgr = null;
    private boolean mInQuery = false;
    private AppChangePasswordPage mPage = null;

    @Override
    public void initView(Bundle savedInstanceState) {
        setTitle("修改密码");

        mPageMgr = new SinglePageMgr(this);
        mPage = new AppChangePasswordPage(this);
        mPageMgr.setPage(mPage, "修改密码");
        mPageMgr.setOnSubmitCompletedListener(this);
        installView(mPageMgr, 0);
    }

    @Override
    protected void initActionMenu(MenuItem item) {
        item.setTitle("确定");

        IconDrawable d = IconifyUtil.getIcon(this, "md-done");
        if (d == null) return;

        d.color(Constant.COLOR_TOOLBAR);
        d.actionBarSize();
        item.setIcon(d);

        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onOK() {
        mPageMgr.doPageVerifyAndSubmit();
    }

    @Override
    public void onSubmitCompleted() {
        final String oldPwd = mPage.getOldpsw();
        final String newPwd = mPage.getNewpsw();

        if (mInQuery) return;
        mInQuery = true;

        doQuery(oldPwd, newPwd);
    }

    DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    };

    public void doQuery(String pwd_old, final String pwd_new) {
//        TreeMap<String, String> map = new TreeMap<>(GaQuUtil.COMPARATOR);
//        map.put("LoginPwd"      , pwd_old);
//        map.put("newLoginPwd"   , pwd_new);
//        BaseResponse.Listener<ObjResponse> listener = new BaseResponse.Listener<ObjResponse>() {
//            @Override
//            public void onResponse(ObjResponse response) {
//                mInQuery = false;
//                if (response == null || !response.success) {
//                    TOAST.showShort(AppChangePassword.this, "操作失败" + ((response != null && !TextUtils.isEmpty(response.msg)) ? "," + response.msg : ""));
//                    return;
//                }
//                TOAST.showShort(AppChangePassword.this, "操作成功" + ((!TextUtils.isEmpty(response.msg)) ? "," + response.msg : ""));
//                finish();
//            }
//        };
//
//        BaseResponse.ErrorListener errorListener  = new BaseResponse.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                mInQuery = false;
//                TOAST.showShort(AppChangePassword.this, "操作失败" + volleyError.networkResponse.statusCode);
//            }
//        };
//
//        VolleyHelper task = new VolleyHelper(mApp, this, null, true);
//        task.setParam(map)
//                .setHeadParam(App.getApp().getUserData().getHeadParam(map))
//                .setMethod(Request.Method.POST)
//                .setURL(Constants.CHANGE_PWD)
//                .setmInfoStr("正在提交数据")
//                .execute(new TypeToken<ObjResponse>() {}.getType(), listener, errorListener);
    }
}
