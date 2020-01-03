package com.rooten.help.apploop.util;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;

import java.util.List;

public class JobUtil {

    /**
     * 安排一个[定期]执行的工作
     * @param context
     * @param service
     * @param jobId
     * @param intervalMillis
     */
    public static void schedulePeriodicJob(Context context, Class<? extends JobService> service, int jobId, int intervalMillis) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context, service));
        builder.setPersisted(false);                                    // 是否需要开机启动
        builder.setMinimumLatency(0);                                   // 最小延迟时间
        builder.setOverrideDeadline(0);                                 // 截至日期
        builder.setPeriodic(intervalMillis);                            // 定期任务 不能设置最小延迟和戒指日期
        builder.setRequiresCharging(false);                             // 是否需要充电
        builder.setRequiresDeviceIdle(false);                           // 设备空闲
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);      // 需要的网络类型，默认不需要
        builder.setBackoffCriteria(0, JobInfo.BACKOFF_POLICY_LINEAR);   // 设置重试

        JobInfo job = builder.build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(job);
    }

    /**
     * 安排一个[延迟]时间执行的工作
     * @param context
     * @param service
     * @param jobId
     * @param intervalMillis
     */
    public static void scheduleLatencyJob(Context context, Class<? extends JobService> service, int jobId, int intervalMillis) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        PersistableBundle bundle = new PersistableBundle();

        JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context, service));
        builder.setPersisted(false);                                    // 是否需要开机启动
        builder.setRequiresCharging(false);                             // 是否需要充电
        builder.setRequiresDeviceIdle(false);                           // 设备空闲
        builder.setMinimumLatency(intervalMillis);                      // 最小延迟时间
        builder.setOverrideDeadline(intervalMillis);                    // 截至日期
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);      // 需要的网络类型，默认不需要
        builder.setBackoffCriteria(0, JobInfo.BACKOFF_POLICY_LINEAR);   // 设置重试
        builder.setExtras(bundle);

        JobInfo job = builder.build();
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(job);
    }

    /** 检查是否存在什么任务 */
    public static boolean hasJob(Context context, int jobId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return false;

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> list = jobScheduler.getAllPendingJobs();
        for (JobInfo job : list) {
            int id = job.getId();
            if (id == jobId) return true;
        }
        return false;
    }

    /** 取消指定任务 */
    public static void cancelJob(Context context, int jobId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(jobId);
    }

    /** 取消所有任务 */
    public static void cancelAllJob(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
    }
}
