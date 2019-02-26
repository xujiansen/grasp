package lib.grasp.widget.banner.snap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.rooten.BaApp;
import lib.grasp.R;
import lib.grasp.adapter.BaseAdapter;
import lib.grasp.util.GlideUtils;

/**
 * Created by JS_grasp on 2019/1/29.
 */
public class SnapBannerAdapter extends BaseAdapter<SnapBannerAdapter.Holder, SnapBannerEntity> {
    private BaApp mApp;
    private Context mContext;
    private View.OnClickListener mListener;

    /** 是否显示标题 */
    private boolean mIsShowTitle = false;

    public SnapBannerAdapter(BaApp mApp, Context mContext, View.OnClickListener listener) {
        this.mApp               = mApp;
        this.mContext           = mContext;
        this.mListener          = listener;
    }

    @Override
    public SnapBannerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.banner_grasp_item, parent, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(SnapBannerAdapter.Holder holder, int position) {
        holder.loadData(position);
    }

    @Override
    public int getItemCount() {
        super.getItemCount();
        return getDatas().size();
    }

    public void setListener(View.OnClickListener listener) {
        this.mListener          = listener;
    }

    class Holder extends RecyclerView.ViewHolder{
        private ImageView iv;
        private TextView tv;

        Holder(View itemView) {
            super(itemView);
            iv   = itemView.findViewById(R.id.iv);
            tv   = itemView.findViewById(R.id.tv);
        }

        public void loadData(int position) {
            SnapBannerEntity bean = getDatas().get(position);
            GlideUtils.getInstance().LoadContextBitmap(mContext, bean.imgUrl, iv);
            tv.setText(bean.title);
            tv.setVisibility(mIsShowTitle ? View.VISIBLE : View.GONE);

            if(mListener != null){
                itemView.setTag(bean);
                itemView.setOnClickListener(mListener);
            }
        }
    }

    /** 设置是否显示banner文字, 需要手动notify */
    public void setIsShowTitle(boolean isShowTitle){
        this.mIsShowTitle = isShowTitle;
    }
}
