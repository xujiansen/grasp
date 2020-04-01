package com.rooten.help.apploop.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import com.rooten.Constant;
import com.rooten.AppHandler;
import com.rooten.interf.IHandler;
import com.rooten.help.apploop.imp.AppLoopJob;
import com.rooten.help.apploop.imp.AppLoopReceiver;
import com.rooten.help.apploop.loopentity.TaskItem;

public class LoopHelper implements IHandler {
    public List<TaskItem> taskItemList = new ArrayList<>();

    /** 添加定时任务 */
    public void addTask(String broadCast, int intervalSecond, long lastTriggerTime){
        for(TaskItem taskItem : taskItemList){
            if(TextUtils.equals(taskItem.broadCast, broadCast)) return;
        }

        taskItemList.add(new TaskItem(broadCast, intervalSecond, lastTriggerTime));
    }

    /** 添加定时任务 */
    public void addTask(TaskItem item){
        for(TaskItem taskItem : taskItemList){
            if(TextUtils.equals(taskItem.broadCast, item.broadCast)) return;
        }
        taskItemList.add(item);
    }

    /** 删除定时任务 */
    public void removeTask(String broadCast){
        TaskItem temp = null;
        for(TaskItem taskItem : taskItemList){
            if(TextUtils.equals(taskItem.broadCast, broadCast)) {
                temp = taskItem;
                break;
            }
        }
        if(temp == null) return;
        taskItemList.remove(temp);
    }


    /** 开始轮训APP存活状态------------------------------------------------------------------------------------------------- */
    public void startAppPoll(Context context, int pollId) {
        int milliSecondPoll = Constant.TIME_POLL * 1000;        // 轮询最小的时间粒度
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.setBroadcastAlarm(context, AppLoopReceiver.class, pollId, milliSecondPoll);
        } else {
            JobUtil.scheduleLatencyJob(context, AppLoopJob.class, pollId, milliSecondPoll);
        }

        doBiz(context);
    }


    /** 停止轮训APP存活状态 */
    public void stopAppPoll(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AlarmUtil.cancelBroadcastAlarm(context, AppLoopReceiver.class, Constant.ID_POLL);
        } else {
            JobUtil.cancelJob(context, Constant.ID_POLL);
        }
    }

    /** 停止所有任务------------------------------------------------------------------------------------------------- */
    public void stopAllTask(Context context) {
        stopAppPoll(context);
        JobUtil.cancelAllJob(context);
    }


    /** 轮询的同时做的事情 */
    private void doBiz(Context context){
        doSendBroadCast(context, Constant.ARG_BROADCAST_LOOP);               // 发轮询广播(必须播放)
//        System.out.println("发出轮询广播");

//        boolean isQuit = AppParamsMgr.isQuit(context);
//        if (isQuit) {
//            stopAppPoll(context);
//            return;
//        }

        long curSysTime = System.currentTimeMillis();
        for(TaskItem taskItem : taskItemList){
            long currInterval = curSysTime - taskItem.lastTriggerTime;  // 当前时间与上次记录的时间的毫秒差
            long interval = taskItem.intervalSecond * 1000;             // 当前时间与上次记录的时间的毫秒差
            if(currInterval < interval) continue;
            taskItem.lastTriggerTime = curSysTime;
//            taskItem.lastTriggerTime += interval;
            doSendBroadCast(context, taskItem.broadCast);               // 发指定广播
        }
    }

    private void doSendBroadCast(Context context, String str){
        Message msg = handler.obtainMessage();
        msg.obj = context;

        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message msg) {
        Context context = (Context) msg.obj;
        if (context == null) return false;

        Bundle bundle = msg.getData();
        if(bundle == null) return true;
        String broadcast = bundle.getString("data");
        if(TextUtils.isEmpty(broadcast)) return true;

        Intent intent = new Intent();
        intent.setAction(broadcast);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
        return true;
    }

    private AppHandler handler = new AppHandler(this);

}
