package lib.grasp.widget.banner.viewpager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import lib.grasp.R;

/**
 * 轮播适配器
 */
public class VpAdapter extends PagerAdapter implements View.OnClickListener {

    private View.OnClickListener mListener;
    private List<BannerEntity> mDatas;
    /** 是否显示标题 */
    private boolean isShowTitle = false;

    public VpAdapter(List<BannerEntity> datas) {
        mDatas = datas;
    }

    public void setListener(View.OnClickListener mListener) {
        this.mListener = mListener;
    }

    /** 设置数据源同时刷新页面 */
    public void setDatas(List<BannerEntity> datas){
        if(mDatas == null) return;
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(getPositionView(position));
        view.setOnClickListener(null);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View child = getPositionView(position);
        if(!(child instanceof FrameLayout)) return child;
        FrameLayout fl = (FrameLayout)child;
        TextView    tv = fl.findViewById(R.id.tv);

        BannerEntity entity = mDatas.get(position);
        if(isShowTitle) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(entity.title);
        }
        else{
            tv.setVisibility(View.GONE);
        }

        view.addView(child);
        view.setTag(entity);
        view.setClickable(true);
        view.setOnClickListener(this);
        return child;
    }

    private View getPositionView(int position) {
        if (position < 0 || position >= mDatas.size()) return null;
        return mDatas.get(position).mView;
    }

    @Override
    public void onClick(View view) {
        if(mListener == null) return;
        mListener.onClick(view);
    }
}
