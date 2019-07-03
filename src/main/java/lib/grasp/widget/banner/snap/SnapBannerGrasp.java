package lib.grasp.widget.banner.snap;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.rooten.BaApp;
import com.rooten.frame.AppHandler;
import com.rooten.frame.IHandler;

import lib.grasp.R;
import lib.grasp.adapter.BaseAdapter;
import lib.grasp.util.ScreenUtil;

/*
        BannerGrasp mBanner = mView.findViewById(R.id.name);
        mBanner.setListener(this);
        mBanner.setDatas(getDatas());
        mBanner.setIsShowTitle(true);
 */

/**
 * 新版本特性
 */
public class SnapBannerGrasp extends FrameLayout implements IHandler {
    private final int MSG_UPDATE_TIME = 1;
    private final int MATCH_PARENT  = LayoutParams.MATCH_PARENT;
    private final int WRAP_CONTENT  = LayoutParams.WRAP_CONTENT;

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
    private List<SnapBannerEntity> mDatas;

    /** viewpager */
    private BaApp mApp;

    /** viewpager */
    private RecyclerView mRecyclerView;
    /** 当前帧序号(从0开始) */
    private int mCurrIndex = 0;

    /** viewpager */
    private SnapBannerAdapter mAdapter;


    /** LinearLayout(防止提示点) */
    private LinearLayout mLL;

    /** 提示点颜色 */
    private int mIndiColor = Color.WHITE;
    /** 提示点之间距离 */
    private int mIndiDistance = 10;
    /** 是否头尾循环 */
    private boolean isCyclable = false;

    /** 每帧停留时间 */
    private int timeFrameStay = 5;

    private AppHandler mHandler = new AppHandler(this);
    private Timer mTimer = null;

    public SnapBannerGrasp(@NonNull Context context) {
        this(context, null);
    }

    public SnapBannerGrasp(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnapBannerGrasp(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mApp = (BaApp) getContext().getApplicationContext();
        mRecyclerView = new RecyclerView(getContext());

        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        if(!(params instanceof LayoutParams)) params = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        else {
            params.width = MATCH_PARENT;
            params.height = MATCH_PARENT;
        }
        addView(mRecyclerView, params);


        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);

        // 将SnapHelper attach 到RecyclrView
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {    // 0: 滑动结束, 1: 拖拽开始滑动, 2: 自动滑动
                super.onScrollStateChanged(recyclerView, newState);
                if(newState != 0) return;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    mCurrIndex = linearManager.findFirstVisibleItemPosition();
                }

                for(SnapBannerEntity entity : mDatas){
                    if(entity.itv == null) return;
                    int color = getContext().getResources().getColor(mDatas.get(mCurrIndex) == entity ? R.color.colorPrimary : R.color.gray);
                    entity.itv.setTextColor(color);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mLL = new LinearLayout(getContext());
        mLL.setOrientation(LinearLayout.HORIZONTAL);
        addView(mLL);

        mAdapter    = new SnapBannerAdapter(mApp, getContext(), null);
        setAdapter(mAdapter);
    }

    private void setAdapter(BaseAdapter adapter){
        if(adapter == null) return;
        mDatas      = adapter.getDatas();
        mRecyclerView.setAdapter(adapter);
    }

    private void resetView(){
        if(mRecyclerView == null) return;
        LayoutParams paramsVp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mRecyclerView.setLayoutParams(paramsVp);

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

    public void setDatas(List<SnapBannerEntity> datas){
        this.mDatas.clear();
        this.mDatas.addAll(datas);
        initIndicatorView();
        if(mAdapter != null) mAdapter.notifyDataSetChanged();
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

    /** 结束计时 */
    private void stopTik(){
        if(mTimer == null) return;
        mTimer.cancel();
        mTimer = null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        showNextFrame();
        return true;
    }

    /** 切换帧(下一张) */
    public void showNextFrame(){
        if(mRecyclerView == null) return;
        mCurrIndex = (mCurrIndex + 1) % Math.max(mDatas.size(), 1);
        mRecyclerView.smoothScrollToPosition(mCurrIndex);
    }

    /** 切换帧(上一张) */
    public void showLastFrame(){
        if(mRecyclerView == null) return;
        mCurrIndex = (mCurrIndex - 1 + mDatas.size()) % Math.max(mDatas.size(), 1);
        mRecyclerView.smoothScrollToPosition(mCurrIndex);
    }

    /** 监听点击某一帧 */
    public void setListener(OnClickListener listener) {
        if(mAdapter == null) return;
        mAdapter.setListener(listener);
    }

    /** 指示器View */
    public void initIndicatorView(){
        if(mDatas == null || mDatas.size() == 0) return;
        mLL.removeAllViews();
        for(SnapBannerEntity entity : mDatas){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtil.getValueByDpi(getContext(), 20), LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            mLL.addView(entity.itv, params);
        }
    }

    /** 设置是否显示banner文字, 需要手动notify */
    public void setIsShowTitle(boolean isShowTitle){
        if(mAdapter == null) return;
        mAdapter.setIsShowTitle(isShowTitle);
        mAdapter.notifyDataSetChanged();
    }

}
