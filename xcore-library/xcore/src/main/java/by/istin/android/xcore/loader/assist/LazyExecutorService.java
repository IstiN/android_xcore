package by.istin.android.xcore.loader.assist;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Uladzimir_Klyshevich on 8/22/2014.
 */
public class LazyExecutorService extends ThreadPoolExecutor {

    private static final int DEFAULT_THREAD_COUNT = 3;

    public LazyExecutorService() {
        super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new LazyThreadFactory());
    }

    private void setThreadCount(int threadCount) {
        setCorePoolSize(threadCount);
        setMaximumPoolSize(threadCount);
    }
}
