package by.istin.android.xcore.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RequestExecutor {

	public static abstract class ExecuteRunnable implements Runnable {
		
		private String key;
		
		public ExecuteRunnable() {
			super();
			key = createKey();
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
		
	}

	private ThreadPoolExecutor executor;
	
	/*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    
	public RequestExecutor() {
		executor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, 1, TimeUnit.SECONDS, new BlockingLifoQueue<Runnable>());		
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
