/***************************************************************************************************
 * 单位：北京红云融通技术有限公司
 * 日期：2017-11-20
 * 版本：1.0.0
 * 版权：All rights reserved.
 **************************************************************************************************/
package lib.grasp.util.task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lib.grasp.util.L;

/**
 * 描述：后台运行线程管理
 * 类名：BackgroudTaskManager
 * 作者：gtzha
 * 日期：2017-11-20
 */
public class BackgroundTaskManager {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 30;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(128),
            new CustomThreadFactory(),
            new CustomRejectedExecutionHandler()
    );

    private static class CustomThreadFactory implements ThreadFactory {

        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String threadName = "BackgroundTask #" + count.addAndGet(1);
            t.setName(threadName);
            return t;
        }
    }

    private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                // 核心改造点，由blockingqueue的offer改成put阻塞方法
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                L.log("InterruptedException:" + e);
            }
        }
    }

    private static BackgroundTaskManager INSTANCE = null;

    public static BackgroundTaskManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BackgroundTaskManager();
        }
        return INSTANCE;
    }

    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }
}
