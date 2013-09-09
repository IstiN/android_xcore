package by.istin.android.xcore.service;

import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import by.istin.android.xcore.utils.Log;


public class RequestExecutor {

    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public static int DEFAULT_POOL_SIZE = Math.max(NUMBER_OF_CORES, 3);

    public static abstract class ExecuteRunnable implements Runnable {

        private class StatusBundle {
            private StatusResultReceiver.Status mStatus;
            private Bundle mBundle;
        }

		private String mKey;

        private List<ResultReceiver> mResultReceivers = new CopyOnWriteArrayList<ResultReceiver>();

        private boolean isRunning = false;

        private List<StatusBundle> mPrevStatuses = Collections.synchronizedList(new ArrayList<StatusBundle>());

        private volatile Object mPrevStatusLock = new Object();

		public ExecuteRunnable(ResultReceiver resultReceiver) {
			super();
			mKey = createKey();
            if (resultReceiver != null) {
                synchronized (mPrevStatusLock) {
                    mResultReceivers.add(resultReceiver);
                }
            }
		}

		public abstract String createKey();
		
		private String getKey() {
			return mKey;
		}

		@Override
		public boolean equals(Object o) {
			return getKey().equals(((ExecuteRunnable)o).getKey());
		}

		@Override
		public int hashCode() {
			return getKey().hashCode();
		}

        public List<ResultReceiver> getResultReceivers() {
            return mResultReceivers;
        }

        protected void addResultReceiver(ResultReceiver resultReceiver) {
            synchronized (mPrevStatusLock) {
                mResultReceivers.add(resultReceiver);
                for (StatusBundle prevStatus : mPrevStatuses) {
                    resultReceiver.send(prevStatus.mStatus.ordinal(), prevStatus.mBundle);
                }
            }
        }

        protected void addResultReceiver(List<ResultReceiver> resultReceivers) {
            for (ResultReceiver resultReceiver : resultReceivers) {
                addResultReceiver(resultReceiver);
            }
        }


        public void sendStatus(StatusResultReceiver.Status status, Bundle bundle) {
            synchronized (mPrevStatusLock) {
                if (mResultReceivers != null) {
                    for (int i = 0; i < mResultReceivers.size(); i++) {
                        ResultReceiver resultReceiver = mResultReceivers.get(i);
                        resultReceiver.send(status.ordinal(), bundle);
                    }
                }
                StatusBundle statusBundle = new StatusBundle();
                statusBundle.mBundle = bundle;
                statusBundle.mStatus = status;
                mPrevStatuses.add(statusBundle);
            }
        }

        protected abstract void onDone();
    }

    private ExecutorService mExecutor;

    private Object mLock = new Object();

    private List<ExecuteRunnable> queue = Collections.synchronizedList(new ArrayList<ExecuteRunnable>());

    private int mThreadPoolSize;

    private BlockingQueue<Runnable> mWorkQueue;

	public RequestExecutor(int threadPoolSize, BlockingQueue<Runnable> workQueue) {
        mThreadPoolSize = threadPoolSize;
        mWorkQueue = workQueue;
        recreateExecutor();
    }

    private void recreateExecutor() {
        mExecutor = new ThreadPoolExecutor(mThreadPoolSize, mThreadPoolSize, 0L, TimeUnit.MILLISECONDS, mWorkQueue);
    }

    public void execute(ExecuteRunnable executeRunnable) {
        synchronized (mLock) {
            if (!queue.contains(executeRunnable)) {
                queue.add(executeRunnable);
                Log.xd(this, "queue: add new " + executeRunnable.getKey());
            } else {
                int index = queue.indexOf(executeRunnable);
                ExecuteRunnable oldRunnable = queue.get(index);
                oldRunnable.addResultReceiver(executeRunnable.getResultReceivers());
                executeRunnable = oldRunnable;
                Log.xd(this, "queue: up to top old " + executeRunnable.getKey());
            }
            Log.xd(this, "queue size: " + queue.size() + " " + executeRunnable.getKey());
            if (executeRunnable.isRunning) {
                Log.xd(this, "queue: already running connect " + executeRunnable.getKey());
                return;
            }
            final ExecuteRunnable finalRunnable = executeRunnable;
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    finalRunnable.isRunning = true;
                    Log.xd(RequestExecutor.this, "queue: start run " + finalRunnable.getKey());
                    finalRunnable.run();
                    synchronized (mLock) {
                        queue.remove(finalRunnable);
                        Log.xd(RequestExecutor.this, "queue: finish and remove, size: " + queue.size() + " " + finalRunnable.getKey());
                        finalRunnable.onDone();
                    }
                }
            });
        }

	}

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void stop(ResultReceiver resultReceiver) {
        mExecutor.shutdownNow();
        resultReceiver.send(0, null);
        recreateExecutor();
    }
}
