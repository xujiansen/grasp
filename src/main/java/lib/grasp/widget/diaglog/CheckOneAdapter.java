package lib.grasp.widget.diaglog;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rooten.BaApp;
import lib.grasp.R;
import lib.grasp.adapter.BaseAdapter;

/**
 * 运动记录列表项
 */
public class CheckOneAdapter extends BaseAdapter<CheckOneAdapter.Holder, CheckOneEntity> {

    private BaApp mApp;
    private Context mContext;
    private View.OnClickListener        mOnClickListener;

    public CheckOneAdapter(BaApp mApp, Context mContext, View.OnClickListener mOnClickListener) {
        this.mApp               = mApp;
        this.mContext           = mContext;
        this.mOnClickListener   = mOnClickListener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_checkone_item, parent, false);
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
        private AppCompatCheckedTextView tvTitle;

        Holder(View itemView) {
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.ctv);
        }

        public void loadData(int position) {
            CheckOneEntity bean = getDatas().get(position);
            tvTitle.setText(bean.name);
            tvTitle.setChecked(bean.isCheck);

            if(listener != null){
                itemView.setTag(bean);
                itemView.setOnClickListener(listener);
            }
        }
    }

    private View.OnClickListener listener = v -> {
        Object o = v.getTag();
        if(!(o instanceof CheckOneEntity)) return;

        CheckOneEntity target = null;
        for(CheckOneEntity one : getDatas()){
            if(!one.isCheck) continue;
            one.isCheck = false;
            target = one;
        }
        int indexDe = getDatas().indexOf(target);
        notifyItemChanged(indexDe);

        CheckOneEntity entity = (CheckOneEntity)o;
        entity.isCheck = true;
        int indexAe = getDatas().indexOf(o);
        notifyItemChanged(indexAe);

        if(mOnClickListener != null) mOnClickListener.onClick(v);
    };
}
