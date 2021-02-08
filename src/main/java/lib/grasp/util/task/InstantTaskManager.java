/***************************************************************************************************
 * 单位：北京红云融通技术有限公司
 * 日期：2017-11-20
 * 版本：1.0.0
 * 版权：All rights reserved.
 **************************************************************************************************/
package lib.grasp.util.task;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述：前台运行线程管理
 * 类名：InstantTaskManager
 * 作者：gtzha
 * 日期：2017-11-20
 */
public class InstantTaskManager {

    private static final int KEEP_ALIVE = 30;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            KEEP_ALIVE,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new CustomThreadFactory()
    );

    private static class CustomThreadFactory implements ThreadFactory {

        private final AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String threadName = "InstantTask #" + count.addAndGet(1);
            t.setName(threadName);
            return t;
        }
    }

    private static InstantTaskManager INSTANCE = null;

    public static InstantTaskManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InstantTaskManager();
        }
        return INSTANCE;
    }

    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }
}
