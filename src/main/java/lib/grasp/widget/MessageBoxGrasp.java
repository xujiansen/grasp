package lib.grasp.widget;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.rooten.BaApp;

import lib.grasp.R;
import lib.grasp.util.ScreenUtil;
import lib.grasp.widget.diaglog.CheckMultiAdapter;
import lib.grasp.widget.diaglog.CheckMultiEntity;
import lib.grasp.widget.diaglog.CheckOneAdapter;
import lib.grasp.widget.diaglog.CheckOneEntity;
import lib.grasp.widget.diaglog.RadioOneAdapter;
import lib.grasp.widget.diaglog.RadioOneEntity;

/** 各种框子(提示, 确认, 选择) */
final public class MessageBoxGrasp {

    /**
     * 主标题默认为"提示框"
     */
    public static void infoMsg(Activity activity, String strMsg) {
        MessageBoxGrasp.infoMsg(activity, "提示框", strMsg, true);
    }

    /**
     * @param strMsg       显示信息
     * @param isCancelAble 是否点击提示框外部取消
     */
    public static void infoMsg(final Activity activity, String title, String strMsg, boolean isCancelAble) {
        infoMsg(activity, title, strMsg, isCancelAble, null);
    }

    /**
     * 有按钮的提示框
     *
     * @param strMsg       显示信息
     * @param isCancelAble 是否点击提示框外部取消
     */
    public static void infoMsg(final Activity activity, String title, String strMsg, boolean isCancelAble, View.OnClickListener listener) {
        View view = View.inflate(activity, R.layout.alertdialog_info, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvContent = view.findViewById(R.id.content);
        TextView tvSubmit = view.findViewById(R.id.submit);
        tvTitle.setText(title);
        tvContent.setText(strMsg);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);
        showDlg(dlg);

        tvSubmit.setOnClickListener(v -> {
            if (listener != null) listener.onClick(v);
            dismiss(dlg);
        });
    }

    /**
     * 单选一个,点击就消失
     */
    public static void radioOne(final Activity activity,
                                String title,
                                boolean isCancelAble,
                                List<RadioOneEntity> list,
                                View.OnClickListener listener) {
        if (list == null || list.size() == 0) return;
        BaApp app = (BaApp) activity.getApplicationContext();
        View view = View.inflate(activity, R.layout.alertdialog_radio, null);
        LinearLayout llTitle = view.findViewById(R.id.ll_title);
        TextView tvTitle = view.findViewById(R.id.title);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        tvTitle.setText(TextUtils.isEmpty(title) ? "请选择" : title);
        llTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);


        View.OnClickListener listener1 = v -> {
            dismiss(dlg);
            if (listener != null) listener.onClick(v);
        };

        RadioOneAdapter adapter = new RadioOneAdapter(app, activity, listener1);
        adapter.getDatas().addAll(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.measure(0,0);
        int maxHeight = 600;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)recyclerView.getLayoutParams();
        params.height = Math.min(maxHeight, recyclerView.getMeasuredHeight());
        params.width = ScreenUtil.getScreenWidthPixels(activity) * 4 / 5;
        recyclerView.setLayoutParams(params);
        showDlg(dlg);
    }

    /**
     * 单选一个,点击确定消失
     */
    public static void checkOne(final Activity activity,
                                String title,
                                boolean isCancelAble,
                                List<CheckOneEntity> list,
                                final View.OnClickListener listenerOk,
                                final View.OnClickListener listenerCancel) {
        if (list == null || list.size() == 0) return;
        BaApp app = (BaApp) activity.getApplicationContext();
        View view = View.inflate(activity, R.layout.alertdialog_check_one, null);
        TextView tvTitle = view.findViewById(R.id.title);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        TextView tvCancel = view.findViewById(R.id.cancel);
        TextView tvSubmit = view.findViewById(R.id.submit);

        tvTitle.setText(TextUtils.isEmpty(title) ? "请选择" : title);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);

        View.OnClickListener listener1 = v -> {
            // 已经完成界面维护
            tvSubmit.setTag(v.getTag());
        };

        CheckOneAdapter adapter = new CheckOneAdapter(app, activity, listener1);
        adapter.getDatas().addAll(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.measure(0,0);
        int maxHeight = 600;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)recyclerView.getLayoutParams();
        params.height = Math.min(maxHeight, recyclerView.getMeasuredHeight());
        recyclerView.setLayoutParams(params);

        tvCancel.setOnClickListener(v -> {
            dismiss(dlg);
            if (listenerCancel != null) listenerCancel.onClick(v);
        });
        tvSubmit.setOnClickListener(v -> {
            dismiss(dlg);
            if (listenerOk != null) listenerOk.onClick(v);
        });

        showDlg(dlg);
    }

    /**
     * 多选,点击确定消失
     */
    public static void checkMulti(final Activity activity,
                                  String title,
                                  boolean isCancelAble,
                                  List<CheckMultiEntity> list,
                                  final View.OnClickListener listenerOk,
                                  final View.OnClickListener listenerCancel) {
        if (list == null || list.size() == 0) return;
        BaApp app = (BaApp) activity.getApplicationContext();
        View view = View.inflate(activity, R.layout.alertdialog_check_multi, null);
        TextView tvTitle = view.findViewById(R.id.title);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        TextView tvCancel = view.findViewById(R.id.cancel);
        TextView tvSubmit = view.findViewById(R.id.submit);

        tvTitle.setText(TextUtils.isEmpty(title) ? "请选择" : title);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);

        CheckMultiAdapter adapter = new CheckMultiAdapter(app, activity);
        adapter.getDatas().addAll(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.measure(0,0);
        int maxHeight = 600;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)recyclerView.getLayoutParams();
        params.height = Math.min(maxHeight, recyclerView.getMeasuredHeight());
        recyclerView.setLayoutParams(params);

        tvCancel.setOnClickListener(v -> {
            dismiss(dlg);
            if (listenerCancel != null) listenerCancel.onClick(v);
        });
        tvSubmit.setOnClickListener(v -> {
            dismiss(dlg);
            List<CheckMultiEntity> all = adapter.getDatas();
            List<CheckMultiEntity> selectedList = new ArrayList<>();
            for(CheckMultiEntity entity : all){
                if(!entity.isCheck) continue;
                selectedList.add(entity);
            }
            v.setTag(selectedList);
            if (listenerOk != null) listenerOk.onClick(v);
        });

        showDlg(dlg);
    }


    /**
     * 主标题默认为"确认提示框"
     */
    public static void confirmMsg(Activity activity, String strMsg, View.OnClickListener listenerOk) {
        MessageBoxGrasp.confirmMsg(activity, "确认提示框", strMsg, listenerOk, null, true);
    }

    /**
     * 确认提示框
     *
     * @param title          标题
     * @param strMsg         显示信息
     * @param listenerOk     点击确认回调
     * @param listenerCancel 点击取消回调
     * @param isCancelAble   是否点击提示框外部取消
     */
    public static void confirmMsg(final Activity activity,
                                  String title,
                                  String strMsg,
                                  final View.OnClickListener listenerOk,
                                  final View.OnClickListener listenerCancel,
                                  boolean isCancelAble) {
        View view = View.inflate(activity, R.layout.alertdialog_confirm, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvContent = view.findViewById(R.id.content);
        TextView tvCancel = view.findViewById(R.id.cancel);
        TextView tvSubmit = view.findViewById(R.id.submit);
        tvTitle.setText(title);
        tvContent.setText(strMsg);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);
        showDlg(dlg);

        tvCancel.setOnClickListener(v -> {
            dismiss(dlg);
            if (listenerCancel != null) listenerCancel.onClick(v);
        });
        tvSubmit.setOnClickListener(v -> {
            dismiss(dlg);
            if (listenerOk != null) listenerOk.onClick(v);
        });
    }

    private static void showDlg(AlertDialog dlg) {
        try {
            dlg.show();
        } catch (RuntimeException e) {
        } catch (Exception e) {
        }
    }

    public static void dismiss(AlertDialog dlg) {
        if (dlg == null || !dlg.isShowing()) return;
        dlg.dismiss();
    }
}
