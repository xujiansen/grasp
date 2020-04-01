package com.rooten.biz;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.grasp.R;
import com.rooten.frame.page.bizpage.TablePage;

public class AppChangePwdPage extends TablePage {
    private EditText mEdtOldPwd;
    private EditText mEdtNewPwd;
    private EditText mEdtNewPwd2;

    public AppChangePwdPage(Context context) {
        super(context);

        initPage();
        initCtrl();
    }

    @Override
    protected void initPage() {
        mEdtOldPwd = createPasswordEdit("原始密码");        // 原始密码
        mEdtNewPwd = createPasswordEdit("新密码　");        // 新密码
        mEdtNewPwd2 = createPasswordEdit("确认密码");        // 确认密码
    }

    @Override
    protected void initCtrl() {
    }

    public void reset() {
        mEdtOldPwd.setText("");
        mEdtNewPwd.setText("");
        mEdtNewPwd2.setText("");
    }

    private EditText createPasswordEdit(String desc) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.app_password_edit, null, false);

        addTableRow(layout, 0);

        TextView title = layout.findViewById(R.id.password_edit_title);
        title.setText(desc);
        EditText edt = layout.findViewById(R.id.password_edit_text);
        edt.setHint(desc);
        edt.setHintTextColor(Color.parseColor("#c9c9c9"));

        return edt;
    }

    @Override
    public void doVerifyData() {
        final String oldPwd = mEdtOldPwd.getEditableText().toString();
        final String newPwd = mEdtNewPwd.getEditableText().toString();
        final String newPwd2 = mEdtNewPwd2.getEditableText().toString();

        if (oldPwd.length() == 0) {
            showError(mEdtOldPwd, "请输入原始密码！");
            return;
        }

        if (!oldPwd.equalsIgnoreCase(mApp.getUserData().mStrPwd)) {
            showError(mEdtOldPwd, "原始密码不正确！");
            return;
        }

        if (newPwd.length() == 0 || newPwd2.length() == 0) {
            showError(mEdtNewPwd, "请输入新密码和确认密码！");
            return;
        }

        if (!newPwd.equalsIgnoreCase(newPwd2)) {
            showError(mEdtNewPwd2, "新密码和确认密码不一致！");
            return;
        }

        if (newPwd.equalsIgnoreCase(oldPwd)) {
            showError(mEdtNewPwd, "新密码和原始密码一样！");
            return;
        }
        verifyCompleted();
    }

    @Override
    public void doSubmitData() {
        submitCompleted();
    }

    public String getOldpsw() {
        return mEdtOldPwd.getEditableText().toString();
    }

    public String getNewpsw() {
        return mEdtNewPwd.getEditableText().toString();
    }
}
