package lib.grasp.widget;

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
import lib.grasp.widget.diaglog.CheckMultiAdapter;
import lib.grasp.widget.diaglog.CheckMultiEntity;
import lib.grasp.widget.diaglog.CheckOneAdapter;
import lib.grasp.widget.diaglog.CheckOneEntity;
import lib.grasp.widget.diaglog.RadioOneAdapter;
import lib.grasp.widget.diaglog.RadioOneEntity;


final public class MessageBoxGrasp {

    /**
     * 主标题默认为"提示框"
     */
    public static void infoMsg(Context ctx, String strMsg) {
        MessageBoxGrasp.infoMsg(ctx, "提示框", strMsg, true);
    }

    /**
     * @param strMsg       显示信息
     * @param isCancelAble 是否点击提示框外部取消
     */
    public static void infoMsg(final Context ctx, String title, String strMsg, boolean isCancelAble) {
        infoMsg(ctx, title, strMsg, isCancelAble, null);
    }

    /**
     * 有按钮的提示框
     *
     * @param strMsg       显示信息
     * @param isCancelAble 是否点击提示框外部取消
     */
    public static void infoMsg(final Context ctx, String title, String strMsg, boolean isCancelAble, View.OnClickListener listener) {
        View view = View.inflate(ctx, R.layout.alertdialog_info, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvContent = view.findViewById(R.id.content);
        TextView tvSubmit = view.findViewById(R.id.submit);
        tvTitle.setText(title);
        tvContent.setText(strMsg);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.dialog_grasp);
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
    public static void radioOne(final Context ctx,
                                String title,
                                boolean isCancelAble,
                                List<RadioOneEntity> list,
                                View.OnClickListener listener) {
        if (list == null || list.size() == 0) return;
        BaApp app = (BaApp) ctx.getApplicationContext();
        View view = View.inflate(ctx, R.layout.alertdialog_radio, null);
        LinearLayout llTitle = view.findViewById(R.id.ll_title);
        TextView tvTitle = view.findViewById(R.id.title);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        tvTitle.setText(TextUtils.isEmpty(title) ? "请选择" : title);
        llTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);


        View.OnClickListener listener1 = v -> {
            dismiss(dlg);
            if (listener != null) listener.onClick(v);
        };

        RadioOneAdapter adapter = new RadioOneAdapter(app, ctx, listener1);
        adapter.getDatas().addAll(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.measure(0,0);
        int maxHeight = 600;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)recyclerView.getLayoutParams();
        params.height = Math.min(maxHeight, recyclerView.getMeasuredHeight());
        recyclerView.setLayoutParams(params);
        showDlg(dlg);
    }

    /**
     * 单选一个,点击确定消失
     */
    public static void checkOne(final Context ctx,
                                String title,
                                boolean isCancelAble,
                                List<CheckOneEntity> list,
                                final View.OnClickListener listenerOk,
                                final View.OnClickListener listenerCancel) {
        if (list == null || list.size() == 0) return;
        BaApp app = (BaApp) ctx.getApplicationContext();
        View view = View.inflate(ctx, R.layout.alertdialog_check_one, null);
        TextView tvTitle = view.findViewById(R.id.title);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        TextView tvCancel = view.findViewById(R.id.cancel);
        TextView tvSubmit = view.findViewById(R.id.submit);

        tvTitle.setText(TextUtils.isEmpty(title) ? "请选择" : title);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);

        View.OnClickListener listener1 = v -> {
            // 已经完成界面维护
            tvSubmit.setTag(v.getTag());
        };

        CheckOneAdapter adapter = new CheckOneAdapter(app, ctx, listener1);
        adapter.getDatas().addAll(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
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
    public static void checkMulti(final Context ctx,
                                  String title,
                                  boolean isCancelAble,
                                  List<CheckMultiEntity> list,
                                  final View.OnClickListener listenerOk,
                                  final View.OnClickListener listenerCancel) {
        if (list == null || list.size() == 0) return;
        BaApp app = (BaApp) ctx.getApplicationContext();
        View view = View.inflate(ctx, R.layout.alertdialog_check_multi, null);
        TextView tvTitle = view.findViewById(R.id.title);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        TextView tvCancel = view.findViewById(R.id.cancel);
        TextView tvSubmit = view.findViewById(R.id.submit);

        tvTitle.setText(TextUtils.isEmpty(title) ? "请选择" : title);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.dialog_grasp);
        builder.setView(view);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(isCancelAble);
        dlg.setCancelable(isCancelAble);

        CheckMultiAdapter adapter = new CheckMultiAdapter(app, ctx);
        adapter.getDatas().addAll(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
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
    public static void confirmMsg(Context ctx, String strMsg, View.OnClickListener listenerOk) {
        MessageBoxGrasp.confirmMsg(ctx, "确认提示框", strMsg, listenerOk, null, true);
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
    public static void confirmMsg(final Context ctx,
                                  String title,
                                  String strMsg,
                                  final View.OnClickListener listenerOk,
                                  final View.OnClickListener listenerCancel,
                                  boolean isCancelAble) {
        View view = View.inflate(ctx, R.layout.alertdialog_confirm, null);
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvContent = view.findViewById(R.id.content);
        TextView tvCancel = view.findViewById(R.id.cancel);
        TextView tvSubmit = view.findViewById(R.id.submit);
        tvTitle.setText(title);
        tvContent.setText(strMsg);

        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.dialog_grasp);
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
