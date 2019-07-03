package lib.grasp.widget.diaglog;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import com.rooten.BaApp;
import lib.grasp.R;
import lib.grasp.adapter.BaseAdapter;

/**
 * 运动记录列表项
 */
public class CheckMultiAdapter extends BaseAdapter<CheckMultiAdapter.Holder, CheckMultiEntity> {

    private BaApp mApp;
    private Context mContext;

    public CheckMultiAdapter(BaApp mApp, Context mContext) {
        this.mApp               = mApp;
        this.mContext           = mContext;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alertdialog_checkmulti_item, parent, false);
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
        private IconTextView checkBox;

        Holder(View itemView) {
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.tv_title);
            checkBox   = itemView.findViewById(R.id.checkbox);
        }

        public void loadData(int position) {
            CheckMultiEntity bean = getDatas().get(position);
            tvTitle.setText(bean.name);
            checkBox.setVisibility(bean.isCheck ? View.VISIBLE : View.INVISIBLE);

            if(listener != null){
                itemView.setTag(bean);
                itemView.setOnClickListener(listener);
            }
        }
    }

    private View.OnClickListener listener = v -> {
        Object o = v.getTag();
        if(!(o instanceof CheckMultiEntity)) return;

        CheckMultiEntity entity = (CheckMultiEntity)o;
        entity.isCheck = !entity.isCheck;
        int indexAe = getDatas().indexOf(o);
        notifyItemChanged(indexAe);
    };
}
