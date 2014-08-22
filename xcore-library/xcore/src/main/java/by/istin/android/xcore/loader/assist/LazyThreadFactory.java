package by.istin.android.xcore.loader.assist;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Uladzimir_Klyshevich on 8/22/2014.
 */
public class LazyThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        return new LazyThread(r);
    }
}
