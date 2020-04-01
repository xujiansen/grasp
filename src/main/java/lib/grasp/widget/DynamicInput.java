package lib.grasp.widget;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import lib.grasp.R;
import lib.grasp.util.NumberUtil;

/**
 * 带有叉号的输入框
 */
public class DynamicInput extends RelativeLayout {

    private EditText mEt;
    private IconTextView mItv;

    /** 所输入的内容 */
    private String mString;

    private TextWatcher mOuterWatcher;

    public DynamicInput(Context context) {
        super(context);
        initView();
    }

    public DynamicInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DynamicInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        View view = View.inflate(getContext(), R.layout.dynamic_input, null);

        mEt = view.findViewById(R.id.et);
        mEt.setTextSize(16);

        mItv = view.findViewById(R.id.icon);
        mItv.setTextColor(Color.LTGRAY);
        mItv.setTextSize(20);

        addView(view);

        initListener();
    }

    private void initListener(){
        if(mItv == null || mEt == null) return;
        mEt.addTextChangedListener(mInnerWatcher);

        // 点击叉号,清空输入框
        mItv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEt.setText("");
            }
        });
    }

    /** 监听输入框 */
    private TextWatcher mInnerWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(mOuterWatcher != null) mOuterWatcher.beforeTextChanged(s, start, count, after);}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mOuterWatcher != null) mOuterWatcher.onTextChanged(s, start, before, count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mOuterWatcher != null) mOuterWatcher.afterTextChanged(s);
            mString = s.toString();
            if(mItv == null) return;
            String key = s.toString();
            mItv.setVisibility(TextUtils.isEmpty(key) ? View.INVISIBLE : View.VISIBLE);
        }
    };

    /** 设置输入文字字体颜色 */
    public void setTextColor(int color){
        if(mEt == null) return;
        mEt.setTextColor(color);
    }

    /** 设置输入文字字体大小 */
    public void setTextSize(float size){
        if(mEt == null) return;
        mEt.setTextSize(size);
    }

    /** 设置右边叉号的可见性 */
    public void setIconVisiblity(boolean canBeSee){
        mEt.setVisibility(canBeSee ? VISIBLE : INVISIBLE);
    }

    /** 设置输入文字局部可见性(内容须是13为手机号码) */
    public void setPartVisible(boolean canBeSee){
        if(canBeSee) {
            mEt.setText(mString);
            return;
        }
        else{
            String text = NumberUtil.getSecretPhone(mString);
            mEt.setText(text);
        }
    }

    /** 设置提示 */
    public void setHint(String hint){
        if(mEt == null) return;
        if(TextUtils.isEmpty(hint)) mEt.setText("");
        else mEt.setText(hint);
    }

    /** 获取所属内容 */
    public String getText(){
        if(mEt == null) return "";
        return mEt.getText().toString();
    }


    /** 获取所属内容 */
    public void setOuterWatcher(TextWatcher watcher){
        if(watcher == null) return;
        mOuterWatcher = watcher;
    }

    /** 设置输入类型 */
    public void setInputType(int type){
        if(mEt == null) return;
        mEt.setInputType(type);
    }

    public EditText getEditText() {
        return mEt;
    }
}
