package lib.grasp.widget.recyclerview.group.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lib.grasp.R;


public class RecyclerViewTestMainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp_recyclerview_test_activity_main);

        findViewById(R.id.btn_sticky_list).setOnClickListener(this);
        findViewById(R.id.btn_group_list).setOnClickListener(this);
        findViewById(R.id.btn_no_header).setOnClickListener(this);
        findViewById(R.id.btn_no_footer).setOnClickListener(this);
        findViewById(R.id.btn_grid_1).setOnClickListener(this);
        findViewById(R.id.btn_grid_2).setOnClickListener(this);
        findViewById(R.id.btn_expandable).setOnClickListener(this);
        findViewById(R.id.btn_expandable).setOnClickListener(this);
        findViewById(R.id.btn_various).setOnClickListener(this);
        findViewById(R.id.btn_various_child).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sticky_list) {
            StickyActivity.openActivity(this);

        } else if (i == R.id.btn_group_list) {
            GroupedListActivity.openActivity(this);

        } else if (i == R.id.btn_no_header) {
            NoHeaderActivity.openActivity(this);

        } else if (i == R.id.btn_no_footer) {
            NoFooterActivity.openActivity(this);

        } else if (i == R.id.btn_grid_1) {
            Grid1Activity.openActivity(this);

        } else if (i == R.id.btn_grid_2) {
            Grid2Activity.openActivity(this);

        } else if (i == R.id.btn_expandable) {
            ExpandableActivity.openActivity(this);

        } else if (i == R.id.btn_various) {
            VariousActivity.openActivity(this);

        } else if (i == R.id.btn_various_child) {
            VariousChildActivity.openActivity(this);

        }
    }
}
