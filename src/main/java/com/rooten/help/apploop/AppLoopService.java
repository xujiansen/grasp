package com.rooten.help.apploop;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.rooten.BaApp;
import com.rooten.Constant;
import com.rooten.help.apploop.loopentity.TaskItem;
import com.rooten.help.apploop.util.LoopHelper;

/**
 * App轮询服务
 */
public class AppLoopService extends Service {
    /** 系统轮训帮助类 */
    public LoopHelper mLoopHelper = new LoopHelper();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null) return START_STICKY;

        // 从intent里面获取参数
        // 是新建任务还是, 删除任务(1: 新增任务, 0: 删除任务)
        int opId            = intent.getIntExtra("operationId", -1);
        String broadcastStr = intent.getStringExtra("broadcastStr");
        int intervalTime    = intent.getIntExtra("intervalSecond", 0);

        if(opId == 1){      // 新增任务
            if(TextUtils.isEmpty(broadcastStr) || intervalTime <= 0) return START_STICKY;
            TaskItem item = new TaskItem(broadcastStr, intervalTime, 0L);
            mLoopHelper.addTask(item);
        }
        else if(opId == 0){// 删除任务
            if(TextUtils.isEmpty(broadcastStr)) return START_STICKY;
            mLoopHelper.removeTask(broadcastStr);
        }

        mLoopHelper.startAppPoll(this, Constant.ID_POLL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, AppLoopService.class);
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
