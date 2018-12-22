package lib.grasp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.grasp.R;
import lib.grasp.util.KeyBoardUtil;

/**
 * 规定位数数字输入控件(验证码,银行密码)
 */
public class VerifyCodeInput extends RelativeLayout {
    // 输入个数
    // 是否密文
    // 获得焦点与没有焦点的输入框的样式
    // 输入框文字颜色大小

    /** 所输内容下有下划线 */
    public static final int INPUT_INDICATE_TYPE_LINE    = 0;
    /** 所输内容有外边框 */
    public static final int INPUT_INDICATE_TYPE_AROUND  = 1;

    /** 输入界面提示 */
    private int inputIndicateType = 0;

    /** 输入个数 */
    private int inputCount = 4;

    /** 是否密文 */
    private boolean isEncript = false;

    /** 所输入的内容 */
    private String keyStr = "";

    /** 隐藏的输入 */
    private EditText mEt;

    /** Container */
    private LinearLayout mContainer;

    /** 子view */
    private List<View> mSonViews = new ArrayList<>();

    /** 输入监听 */
    private TextWatcher mOutWatcher;


    public VerifyCodeInput(Context context) {
        this(context, null);
    }

    public VerifyCodeInput(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs, defStyleAttr);
        init(context);
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeInput, defStyleAttr, 0);
        inputIndicateType = typedArray.getInteger(R.styleable.VerifyCodeInput_indicateType, 0);
        typedArray.recycle();
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.verify_code_input_container, this);
        mContainer  = this.findViewById(R.id.container_et);
        mEt         = this.findViewById(R.id.et);
        mEt.addTextChangedListener(mWatcher);
        mEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputCount)}); //最大输入长度
        initView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetInputView();
        mEt.postDelayed(() -> {
            mEt.requestFocus();
            KeyBoardUtil.showSoftInput(getContext(), mEt);
        }, 300);
    }

    /** 初始化控件 */
    private void initView(){
        for (int i = 0; i < inputCount; i++) {
            View son = initItemView(null);
            son.setOnClickListener(v -> {
                mEt.requestFocus();
                KeyBoardUtil.showSoftInput(getContext(), mEt);
            });
            mSonViews.add(son);
            mContainer.addView(son);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for(View view : mSonViews){
            initItemView(view);
        }
    }

    /** 初始化子view的大小参数(长宽一样) */
    private View initItemView(View view) {
        int widthOfSons = getMeasuredWidth() / (Math.max(inputCount, 4));
        if (view == null) view = View.inflate(getContext(), inputIndicateType == 0 ? R.layout.verify_code_input_item_line : R.layout.verify_code_input_item_around, null);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) params = new LayoutParams(widthOfSons, widthOfSons);
        else {
            params.height = widthOfSons;
            params.width = widthOfSons;
        }
        view.setLayoutParams(params);
        return view;
    }

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(mOutWatcher != null) mOutWatcher.beforeTextChanged(s, start, count, after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mOutWatcher != null) mOutWatcher.onTextChanged(s, start, before, count);

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(mOutWatcher != null) mOutWatcher.afterTextChanged(s);
            onTextInput(s.toString());
        }
    };

    private void onTextInput(String s){
        resetInputView();

        if(s.length() > inputCount) s = s.substring(0, inputCount); //截取
        keyStr = s;

        for(int i = 0; i < s.length(); i++){
            String c = String.valueOf(s.charAt(i));
            TextView tv = mSonViews.get(i).findViewById(R.id.tv);
            tv.setText(c);
            tv.setEnabled(true);
        }
    }

    private void resetInputView(){
        for(int i = 0; i < inputCount; i++){
            TextView tv = mSonViews.get(i).findViewById(R.id.tv);
            tv.setText("");
            tv.setEnabled(false);
        }
    }

    /** 获取所属验证码 */
    public String getText() {
        return keyStr;
    }

    /** 外部设置所输入验证码 */
    public void setText(String text){
        if(TextUtils.isEmpty(text)) return;
        if(text.length() > inputCount) text = text.substring(0, inputCount); //截取
        keyStr = text;
        mEt.setText(text);
        mEt.setSelection(text.length());
    }

    public void addTextChangedListener(TextWatcher mOutWatcher) {
        this.mOutWatcher = mOutWatcher;
    }
}
