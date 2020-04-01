package lib.grasp.widget.banner.viewpager;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.rooten.AppHandler;
import com.rooten.interf.IHandler;

import lib.grasp.util.ScreenUtil;

/*
        BannerGrasp mBanner = mView.findViewById(R.id.name);
        mBanner.setListener(this);
        mBanner.setDatas(getDatas());
        mBanner.setIsShowTitle(true);
 */

/**
 * 轮播
 */
public class BannerGrasp extends FrameLayout implements IHandler, ViewPager.OnPageChangeListener {
    private final int MSG_UPDATE_TIME = 1;
    private final int MATCH_PARENT  = FrameLayout.LayoutParams.MATCH_PARENT;
    private final int WRAP_CONTENT  = FrameLayout.LayoutParams.WRAP_CONTENT;

    /*
     1. viewpager
     2. indicator(每个点之间的间隔)
     3. 是否尾头循环
     4. 是否显示文字(显示文字就不显示indicator)
     5. 每帧停留时间
     6. 切换帧画面消耗时间
     */
    /** 是否暂停 */
    private boolean isPause = false;

    /** 数据源 */
    private List<BannerEntity> mDatas = new ArrayList<>();

    /** viewpager */
    private ViewPager mVp;
    /** 当前帧序号(从0开始) */
    private int mCurrIndex = 0;

    /** viewpager */
    private VpAdapter mAdapter;

    /** LinearLayout(防止提示点) */
    private LinearLayout mLL;

    /** 提示点颜色 */
    private int mIndiColor = Color.WHITE;
    /** 提示点之间距离 */
    private int mIndiDistance = 10;
    /** 是否头尾循环 */
    private boolean isCyclable = false;
    /** 是否显示标题 */
    private boolean isShowTitle = false;

    /** 每帧停留时间 */
    private int timeFrameStay = 20;

    private AppHandler mHandler = new AppHandler(this);
    private Timer mTimer = null;

    public BannerGrasp(@NonNull Context context) {
        this(context, null);
    }

    public BannerGrasp(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerGrasp(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resetView();
    }

    private void init(){
        initView();
        startTik();
    }

    private void initView(){
        mVp = new ViewPager(getContext());
        ViewGroup.LayoutParams params = mVp.getLayoutParams();
        if(!(params instanceof LayoutParams)) params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        else {
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.WRAP_CONTENT;
        }
        addView(mVp, params);

        mLL = new LinearLayout(getContext());
        mLL.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLL);

        mAdapter = new VpAdapter(mDatas);
        mVp.setAdapter(mAdapter);
        mVp.addOnPageChangeListener(this);
    }

    /**
     * 设置是否显示标题
     * 1. 显示标题, 则不显示指示器
     * 2. 不显示标题, 则显示指示器
     */
    public void setIsShowTitle(boolean showTitle){
        isShowTitle = showTitle;
        if(mLL != null) mLL.setVisibility(isShowTitle ? GONE : VISIBLE);
        if(mAdapter != null) mAdapter.setShowTitle(isShowTitle);

    }

    private void resetView(){
        if(mVp == null || mLL == null) return;

        FrameLayout.LayoutParams paramsVp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mVp.setLayoutParams(paramsVp);

        FrameLayout.LayoutParams paramsLL = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsLL.gravity = Gravity.CENTER_HORIZONTAL| Gravity.BOTTOM;
        paramsLL.bottomMargin = ScreenUtil.getValueByDpi(getContext(), 10);
        mLL.setLayoutParams(paramsLL);
    }

    /** 开始(继续)动画 */
    public void onResume(){
        isPause = false;
    }

    /** 暂停动画 */
    public void onPause(){
        isPause = true;
    }

    public void setCyclable(boolean isPause){
        this.isPause = isPause;
    }

    /** 销毁 */
    public void onDestroy(){
        mTimer.cancel();
        mTimer = null;
    }

    public void setDatas(List<BannerEntity> datas){
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        initIndicatorView();
        if(mVp != null) mVp.removeAllViews();
        mAdapter.setDatas(datas);
    }

    /** 开始计时 */
    private void startTik(){
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                if(isPause) return;
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        };

        mTimer = new Timer();
        mTimer.schedule(timeTask, 10, timeFrameStay * 1000);
    }

    @Override
    public boolean handleMessage(Message msg) {
        showNextFrame();
        return true;
    }

    /** 切换帧(下一张) */
    public void showNextFrame(){
        if(mVp == null || mLL == null) return;
        int targetIndex = (mCurrIndex + 1) % Math.max(mDatas.size(), 1);
        mVp.setCurrentItem(targetIndex);
    }

    /** 切换帧(上一张) */
    public void showLastFrame(){
        if(mVp == null || mLL == null) return;
        int targetIndex = (mCurrIndex - 1 + mDatas.size()) % Math.max(mDatas.size(), 1);
        mVp.setCurrentItem(targetIndex);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position < 0 || position >= mDatas.size()) return;
        BannerEntity entityLeft = mDatas.get(position);
        entityLeft.itv.setAlpha(1 - 0.7f * positionOffset); // 变淡

        int rightIndex = position + 1;
        if(rightIndex < 0 || rightIndex >= mDatas.size()) return;
        BannerEntity entityRight = mDatas.get(rightIndex);
        entityRight.itv.setAlpha(0.3f + 0.7f * positionOffset); // 变浓
    }

    @Override
    public void onPageSelected(int position) {
        mCurrIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    /** 指示器View */
    public void initIndicatorView(){
        if(mDatas == null || mDatas.size() == 0) return;
        mLL.removeAllViews();
        for(int i = 0; i < mDatas.size(); i++){
            BannerEntity entity = mDatas.get(i);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtil.getValueByDpi(getContext(), 20), LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mLL.addView(entity.itv, params);
        }
    }

    /** 监听点击某一帧 */
    public void setListener(OnClickListener listener) {
        if(mAdapter == null) return;
        mAdapter.setListener(listener);
    }
}
