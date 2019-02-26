package cn.com.rooten.ctrl;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import lib.grasp.R;
import cn.com.rooten.util.LinearLayoutHelper;
import cn.com.rooten.util.Utilities;

public class EditDialog {
    private static AlertDialog mAlertDlg;

    public interface onPositiveClickLister {
        void getText(String text);
    }


    public interface onPositive2ClickLister {
        void getText(String text1, String text2);
    }

    public static AlertDialog edit(final Context context, String title, String defaultText, int max, final EditDialog.onPositiveClickLister yes, final EditDialog.onPositiveClickLister edne) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AlertDialog);
        builder.setTitle(title);

        LinearLayout layout = LinearLayoutHelper.createHorizontal(context);
        final AppCompatEditText editText = createEdit(context, layout, defaultText);
        if (max < 0) max = Integer.MAX_VALUE;
        editText.addTextChangedListener(new EditWatcher(editText, max));
        builder.setView(layout);

        //确定
        final DialogInterface.OnClickListener y = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);

                String text = editText.getEditableText().toString();
                if (yes != null) yes.getText(text);
            }
        };

        final DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);
            }
        };

        //下载
        final DialogInterface.OnClickListener ne = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);
                String text = editText.getEditableText().toString();
                if (edne != null) edne.getText(text);
            }
        };


        builder.setNeutralButton("下载", ne);
        builder.setNegativeButton("取消", n);
        builder.setPositiveButton("确定", y);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAlertDlg = dialog;

        return dialog;
    }

    public static AlertDialog edit(final Context context, String title, String defaultText, int max, final EditDialog.onPositiveClickLister yes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AlertDialog);
        builder.setTitle(title);

        LinearLayout layout = LinearLayoutHelper.createVertical(context);
        final AppCompatEditText editText = createEdit(context, layout, defaultText);
        if (max < 0) max = Integer.MAX_VALUE;
        editText.addTextChangedListener(new EditWatcher(editText, max));
        builder.setView(layout);

        final DialogInterface.OnClickListener y = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);

                String text = editText.getEditableText().toString();
                if (yes != null) yes.getText(text);
            }
        };

        final DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);
            }
        };

        builder.setPositiveButton("确定", y);
        builder.setNegativeButton("取消", n);

        AlertDialog dialog = builder.create();dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAlertDlg = dialog;

        return dialog;
    }

    /** 输入一个参数 */
    public static AlertDialog edit1Input(final Context context, String title, String hint, String value, int max, int inputType1,final EditDialog.onPositiveClickLister yes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AlertDialog);
        builder.setTitle(title);

        LinearLayout layout = LinearLayoutHelper.createVertical(context);
        final AppCompatEditText editText = createEditWithHint(context, layout, hint, value);
        if (max < 0) max = Integer.MAX_VALUE;
        editText.setInputType(inputType1);
        editText.addTextChangedListener(new EditWatcher(editText, max));
        builder.setView(layout);

        final DialogInterface.OnClickListener y = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);

                String text = editText.getEditableText().toString();
                if (yes != null) yes.getText(text);
            }
        };

        final DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText);
            }
        };

        builder.setPositiveButton("确定", y);
        builder.setNegativeButton("取消", n);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAlertDlg = dialog;

        return dialog;
    }

    /** 输入两个参数 */
    public static AlertDialog edit2Input(final Context context,
                                          String dialogTitle,
                                          String title1,
                                          String title2,
                                          String defaultText1,
                                          String defaultText2,
                                          String hint1,
                                          String hint2,
                                          int max,
                                          final EditDialog.onPositive2ClickLister yes,
                                          final DialogInterface.OnClickListener neutral) {
        return edit2Input(context, dialogTitle,title1,title2,defaultText1,defaultText2,hint1,hint2, InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT, max,yes,neutral);
    }

    /** 输入两个参数,支持自定义输入数据类型 */
    public static AlertDialog edit2Input(final Context context,
                                         String dialogTitle,
                                         String title1,
                                         String title2,
                                         String defaultText1,
                                         String defaultText2,
                                         String hint1,
                                         String hint2,
                                         int max,
                                         int inputType1,
                                         int inputType2,
                                         final EditDialog.onPositive2ClickLister yes,
                                         final DialogInterface.OnClickListener neutral) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AlertDialog);
        builder.setTitle(dialogTitle);

        LinearLayout layout = LinearLayoutHelper.createVertical(context);
        LinearLayout line1 = LinearLayoutHelper.createHorizontal(context);
        LinearLayout line2 = LinearLayoutHelper.createHorizontal(context);
        final AppCompatEditText editText1 = createEditWithTitle(context, line1, title1, defaultText1, hint1);
        final AppCompatEditText editText2 = createEditWithTitle(context, line2, title2, defaultText2, hint2);
        editText1.setInputType(inputType1);
        editText2.setInputType(inputType2);
        LinearLayoutHelper.addView(layout, line1);
        LinearLayoutHelper.addView(layout, line2);
        if (max < 0) max = Integer.MAX_VALUE;
        editText1.addTextChangedListener(new EditWatcher(editText1, max));
        editText2.addTextChangedListener(new EditWatcher(editText2, max));
        builder.setView(layout);

        final DialogInterface.OnClickListener y = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText1);
                hideInput(context, editText2);

                String text1 = editText1.getEditableText().toString();
                String text2 = editText2.getEditableText().toString();
                if (yes != null) yes.getText(text1, text2);
            }
        };

        final DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText1);
                hideInput(context, editText2);
            }
        };

        final DialogInterface.OnClickListener neutral1 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hideInput(context, editText1);
                hideInput(context, editText2);
                if (neutral != null) neutral.onClick(dialog, which);
            }
        };

        if(neutral != null) builder.setNeutralButton("删除", neutral1);
        builder.setPositiveButton("确定", y);
        builder.setNegativeButton("取消", n);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

//        showInput(context, editText1);

        mAlertDlg = dialog;

        return dialog;
    }

    /** seekbar选择 */
    public static AlertDialog seekbarInput(final Context context, String title, int max, final int min,int defaultValue, final EditDialog.onPositiveClickLister yes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AlertDialog);
        builder.setTitle(title);

        LinearLayout layout = LinearLayoutHelper.createHorizontal(context);
        final TextView textView = new TextView(context);
        textView.setText(String.valueOf(defaultValue));
        textView.setTextSize(16);
        textView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        LinearLayoutHelper.addView(layout, textView, Utilities.getValueByDpi(context, 50), Utilities.getValueByDpi(context, 50));

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(max - min);
        seekBar.setProgress(Math.max(defaultValue - min, 0));
        LinearLayoutHelper.addView(layout, seekBar, ViewGroup.LayoutParams.MATCH_PARENT, Utilities.getValueByDpi(context, 50));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.valueOf(progress + min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setView(layout);

        final DialogInterface.OnClickListener y = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = textView.getText().toString();
                if (yes != null) yes.getText(text);
            }
        };

        final DialogInterface.OnClickListener n = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };

        builder.setPositiveButton("确定", y);
        builder.setNegativeButton("取消", n);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAlertDlg = dialog;

        return dialog;
    }

    private static void showInput(Context context, EditText editText) {
        InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.showSoftInput(editText, 0);
    }

    private static void hideInput(Context context, EditText editText) {
        InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static AlertDialog edit(Context context, String title, EditDialog.onPositiveClickLister yes) {
        return edit(context, title, "", Integer.MAX_VALUE, yes);
    }

    public static AlertDialog edit(Context context, String title, EditDialog.onPositiveClickLister yes, EditDialog.onPositiveClickLister ne) {
        return edit(context, title, "", Integer.MAX_VALUE, yes, ne);
    }

    public static AlertDialog edit(Context context, String title, int max, EditDialog.onPositiveClickLister yes) {
        return edit(context, title, "", max, yes);
    }

    public static AlertDialog edit(Context context, String title, String defaultText, EditDialog.onPositiveClickLister yes) {
        return edit(context, title, defaultText, Integer.MAX_VALUE, yes);
    }

    private static AppCompatEditText createEditWithTitle(final Context context, LinearLayout layout, String title, String defaultText, String hint) {
        if (Utilities.isEmpty(defaultText)) defaultText = "";
        if (Utilities.isEmpty(title))       title = "";
        if (Utilities.isEmpty(hint))        hint = "";

        final AppCompatTextView textView = new AppCompatTextView(context);
        textView.setText(title);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);

        final AppCompatEditText edtText = new AppCompatEditText(context);
        edtText.setHint(hint);
        edtText.setText(defaultText);
        edtText.setTextSize(16);
        edtText.setSelection(defaultText.length());

        int pad = context.getResources().getDimensionPixelSize(R.dimen.abc_dialog_padding_material);
        layout.setPadding(pad, Utilities.getValueByDpi(context, 8), pad, 0);
        int width = 0;
        int height = LinearLayoutHelper.WRAP_CONTENT;
        LinearLayoutHelper.addView(layout, textView, width, height, 1);
        LinearLayoutHelper.addView(layout, edtText, width, height, 3);
        return edtText;
    }

    private static AppCompatEditText createEditWithHint(final Context context, LinearLayout layout, String hint, String value) {
        if (Utilities.isEmpty(hint)) hint = "";
        if (Utilities.isEmpty(value)) value = "";

        final AppCompatEditText edtText = new AppCompatEditText(context);
        edtText.setHint(hint);
        edtText.setText(value);
        edtText.setSelection(value.length());

        int pad = context.getResources().getDimensionPixelSize(R.dimen.abc_dialog_padding_material);
        layout.setPadding(pad, Utilities.getValueByDpi(context, 8), pad, 0);
        int width = LinearLayoutHelper.MATCH_PARENT;
        int height = LinearLayoutHelper.WRAP_CONTENT;
        LinearLayoutHelper.addView(layout, edtText, width, height);
        return edtText;
    }

    private static AppCompatEditText createEdit(final Context context, LinearLayout layout, String defaultText) {
        return createEditWithHint(context, layout, "", defaultText);
    }

    public static void dismiss() {
        if (mAlertDlg == null) return;
        mAlertDlg.dismiss();
        mAlertDlg = null;
    }

    private static class EditWatcher implements TextWatcher {
        private EditText mEdit;
        private String mText;
        private boolean mUpdateText = false;
        private int mEndPos;
        private int mMaxLen;

        EditWatcher(EditText edit, int maxLen) {
            mEdit = edit;
            mMaxLen = maxLen;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!mUpdateText) mText = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            if (!mUpdateText) {
                mText = s.toString();
                mEndPos = mEdit.getSelectionEnd();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            if (s.length() > mMaxLen && !mUpdateText) {
                Toast.makeText(mEdit.getContext(), "已达到最大输入字符", Toast.LENGTH_SHORT).show();
                mUpdateText = true;
                mEdit.setText(mText);
                mEdit.setSelection(mEndPos);
                mUpdateText = false;
                mText = null;
            }
        }
    }
}
