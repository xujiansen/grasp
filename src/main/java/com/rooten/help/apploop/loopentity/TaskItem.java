package com.rooten.help.apploop.loopentity;

/**
 * Created by JS_grasp on 2019/1/19.
 */
public class TaskItem {
    /** 到点发布广播 */
    public String   broadCast = "";
    /** 发布广播间隔 */
    public int      intervalSecond = 30 * 1000;
    /** 上次触发时间 */
    public long     lastTriggerTime = 0;

    /**
     * @param broadCast         到点发布广播
     * @param intervalSecond    发布广播间隔
     * @param lastTriggerTime   上次触发时间
     */
    public TaskItem(String broadCast, int intervalSecond, long lastTriggerTime) {
        this.broadCast = broadCast;
        this.intervalSecond = intervalSecond;
        this.lastTriggerTime = lastTriggerTime;
    }
}
