package lib.grasp.widget.recyclerview.group.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import lib.grasp.R;
import lib.grasp.widget.recyclerview.group.adapter.NoFooterAdapter;
import lib.grasp.widget.recyclerview.group.adapter.base.GroupedRecyclerViewAdapter;
import lib.grasp.widget.recyclerview.group.holder.BaseViewHolder;
import lib.grasp.widget.recyclerview.group.model.GroupModel;

/**
 * 没有组尾的分组列表。
 */
public class NoFooterActivity extends AppCompatActivity {

    private TextView tvTitle;
    private RecyclerView rvList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_activity_group_list);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        tvTitle.setText(R.string.no_footer);

        rvList.setLayoutManager(new LinearLayoutManager(this));
        NoFooterAdapter adapter = new NoFooterAdapter(this, GroupModel.getGroups1(10, 5));
        adapter.setOnHeaderClickListener(new GroupedRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {
                Toast.makeText(NoFooterActivity.this, "组头：groupPosition = " + groupPosition,
                        Toast.LENGTH_LONG).show();
                Log.e("eee", adapter.toString() + "  " + holder.toString());
            }
        });

        adapter.setOnChildClickListener(new GroupedRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                Toast.makeText(NoFooterActivity.this, "子项：groupPosition = " + groupPosition
                                + ", childPosition = " + childPosition,
                        Toast.LENGTH_LONG).show();
            }
        });
        rvList.setAdapter(adapter);
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, NoFooterActivity.class);
        context.startActivity(intent);
    }
}
