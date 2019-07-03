package lib.grasp.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * list适配器
 * @param <T>   单项的viewholder类型
 * @param <M>   数据类型
 */
public abstract class BaseAdapter<T extends RecyclerView.ViewHolder, M> extends RecyclerView.Adapter<T> {
    private List<M> mDatas = new ArrayList<>();
    private View    mBg;

    @Override
    public void onBindViewHolder(T holder, int position) { }

    @Override
    public int getItemCount() {
        if(mBg != null) mBg.setVisibility((mDatas.size() == 0) ? View.VISIBLE : View.GONE);
        return mDatas.size();
    }

    public void setBg(View mBg) {
        this.mBg = mBg;
    }

    public List<M> getDatas() {
        return mDatas;
    }
}
