package by.istin.android.xcore.service;

import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RequestExecutor {

	public static abstract class ExecuteRunnable implements Runnable {
		
		private String key;

        private List<ResultReceiver> resultReceivers = new ArrayList<ResultReceiver>();

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
        }
	}

	private ThreadPoolExecutor executor;
	
	/*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    
	public RequestExecutor() {
		executor = new ThreadPoolExecutor(Math.max(NUMBER_OF_CORES, 3), Math.max(NUMBER_OF_CORES, 3), 1, TimeUnit.SECONDS, new BlockingLifoQueue<Runnable>());
	}
	
	public synchronized void execute(ExecuteRunnable executeRunnable) {
		BlockingQueue<Runnable> queue = executor.getQueue();
		if (!queue.contains(executeRunnable)) {
			executor.execute(executeRunnable);
		} else {
			queue.remove(executeRunnable);
			executor.execute(executeRunnable);
		}
	}

}
