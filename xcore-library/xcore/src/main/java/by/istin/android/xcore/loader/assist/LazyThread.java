package by.istin.android.xcore.loader.assist;

/**
 * Created by Uladzimir_Klyshevich on 8/22/2014.
 */
public class LazyThread extends Thread {

    public LazyThread(Runnable r) {
        super(r);
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        super.run();
    }
}
