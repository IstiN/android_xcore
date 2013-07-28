package by.istin.android.xcore.service;

import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import by.istin.android.xcore.utils.Log;


public class RequestExecutor {

    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static int POOL_SIZE = Math.max(NUMBER_OF_CORES, 3);

	public static abstract class ExecuteRunnable implements Runnable {

        private class StatusBundle {
            private StatusResultReceiver.Status status;
            private Bundle bundle;
        }

		private String key;

        private List<ResultReceiver> resultReceivers = new ArrayList<ResultReceiver>();

        private boolean isRunning = false;

        private List<StatusBundle> prevStatuses = Collections.synchronizedList(new ArrayList<StatusBundle>());

        private volatile Object prevStatusLock = new Object();

		public ExecuteRunnable(ResultReceiver resultReceiver) {
			super();
			key = createKey();
            if (resultReceiver != null) {
                resultReceivers.add(resultReceiver);
            }
		}

		public abstract String createKey();
		
		private String getKey() {
			return key;
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
            return resultReceivers;
        }

        protected void addResultReceiver(ResultReceiver resultReceiver) {
            resultReceivers.add(resultReceiver);
            synchronized (prevStatusLock) {
                for (StatusBundle prevStatus : prevStatuses) {
                    resultReceiver.send(prevStatus.status.ordinal(), prevStatus.bundle);
                }
            }
        }

        protected void addResultReceiver(List<ResultReceiver> resultReceivers) {
            for (ResultReceiver resultReceiver : resultReceivers) {
                addResultReceiver(resultReceiver);
            }
        }


        public void sendStatus(StatusResultReceiver.Status status, Bundle bundle) {
            if (resultReceivers != null) {
                for (int i = 0; i < resultReceivers.size(); i++) {
                    ResultReceiver resultReceiver = resultReceivers.get(i);
                    resultReceiver.send(status.ordinal(), bundle);
                }
            }
            synchronized (prevStatuses) {
                StatusBundle statusBundle = new StatusBundle();
                statusBundle.bundle = bundle;
                statusBundle.status = status;
                prevStatuses.add(statusBundle);
            }
        }
    }

	private ExecutorService executor;

    private Object lock = new Object();

    private List<ExecuteRunnable> queue = Collections.synchronizedList(new ArrayList<ExecuteRunnable>());


	public RequestExecutor() {
		executor = Executors.newFixedThreadPool(POOL_SIZE);
	}
	
	public void execute(ExecuteRunnable executeRunnable) {
        synchronized (lock) {
            if (!queue.contains(executeRunnable)) {
                queue.add(executeRunnable);
                Log.xd(this, "queue: add new " + executeRunnable.getKey());
            } else {
                int index = queue.indexOf(executeRunnable);
                ExecuteRunnable oldRunnable = queue.get(index);
                oldRunnable.addResultReceiver(executeRunnable.getResultReceivers());
                queue.remove(index);
                queue.add(oldRunnable);
                executeRunnable = oldRunnable;
                Log.xd(this, "queue: up to top old " + executeRunnable.getKey());
            }
            Log.xd(this, "queue size: " + queue.size() +" "+ executeRunnable.getKey());
            if (executeRunnable.isRunning) {
                Log.xd(this, "queue: already running connect " + executeRunnable.getKey());
                return;
            }
            final ExecuteRunnable finalRunnable = executeRunnable;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    finalRunnable.isRunning = true;
                    Log.xd(RequestExecutor.this, "queue: start run " + finalRunnable.getKey());
                    finalRunnable.run();
                    synchronized (lock) {
                        queue.remove(finalRunnable);
                        Log.xd(RequestExecutor.this, "queue: finish and remove, size: " + queue.size()+ " " + finalRunnable.getKey());
                    }
                }
            });
        }

	}

}
