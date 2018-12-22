package lib.grasp.widget.diaglog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.rooten.BaApp;
import lib.grasp.R;
import lib.grasp.adapter.BaseAdapter;

/**
 * 运动记录列表项
 */
public class RadioOneAdapter extends BaseAdapter<RadioOneAdapter.Holder, RadioOneEntity> {

    private BaApp mApp;
    private Context mContext;
    private View.OnClickListener        mOnClickListener;

    public RadioOneAdapter(BaApp mApp, Context mContext, View.OnClickListener mOnClickListener) {
        this.mApp               = mApp;
        this.mContext           = mContext;
        this.mOnClickListener   = mOnClickListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_radio_item, parent, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.loadData(position);
    }

    @Override
    public int getItemCount() {
        super.getItemCount();
        return getDatas().size();
    }

    class Holder extends RecyclerView.ViewHolder{
        private TextView tvTitle;

        Holder(View itemView) {
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.tv_title);
        }

        public void loadData(int position) {
            RadioOneEntity bean = getDatas().get(position);
            tvTitle.setText(bean.name);

            if(mOnClickListener != null){
                itemView.setTag(bean);
                itemView.setOnClickListener(mOnClickListener);
            }
        }
    }
}
