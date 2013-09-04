/**
 * 
 */
package by.istin.android.xcore.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.service.RequestExecutor.ExecuteRunnable;
import by.istin.android.xcore.service.assist.LIFOLinkedBlockingDeque;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Log;

/**
 * @author IstiN
 *
 */
public abstract class AbstractExecutorService extends Service {

    private static final String ACTION_STOP = "stop";
	protected static final String DATA_SOURCE_KEY = "datasourceKey";
    protected static final String PROCESSOR_KEY = "processorKey";
    protected static final String RESULT_RECEIVER = "resultReceiver";

    private RequestExecutor mRequestExecutor;

    public static void stop(Context context, ResultReceiver receiver, Class<? extends AbstractExecutorService> serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        intent.putExtra(ACTION_STOP, true);
        intent.putExtra(RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    protected static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, Class<?> serviceClass) {
        execute(context, dataSourceRequest, processorKey, datasourceKey, null, serviceClass);
    }

    protected static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver, Class<?> serviceClass) {
        if (context == null) {
            context = ContextHolder.getInstance().getContext();
            if (context == null) {
                return;
            }
        }
        Intent intent = createStartIntent(context, dataSourceRequest, processorKey, datasourceKey, resultReceiver, serviceClass);
        if (intent == null) {
            return;
        }
        context.startService(intent);
    }

    protected static Intent createStartIntent(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver, Class<?> serviceClass) {
        if (context == null) {
            context = ContextHolder.getInstance().getContext();
            if (context == null) {
                return null;
            }
        }
        Intent intent = new Intent(context, serviceClass);
        intent.putExtra(DATA_SOURCE_KEY, datasourceKey);
        intent.putExtra(PROCESSOR_KEY, processorKey);
        intent.putExtra(RESULT_RECEIVER, resultReceiver);
        dataSourceRequest.toIntent(intent);
        return intent;
    }

    /* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return new ServiceBinder<AbstractExecutorService>(this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			onHandleIntent(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	protected void onHandleIntent(final Intent intent) {
        final ResultReceiver resultReceiver = intent.getParcelableExtra(RESULT_RECEIVER);
        if (intent.getBooleanExtra(ACTION_STOP, false)) {
            if (mRequestExecutor != null) {
                mRequestExecutor.stop(resultReceiver);
            }
            stopSelf();
            return;
        }
		final DataSourceRequest dataSourceRequest = DataSourceRequest.fromIntent(intent);
        final Bundle bundle = new Bundle();
        dataSourceRequest.toBundle(bundle);
        if (resultReceiver != null) {
            resultReceiver.send(StatusResultReceiver.Status.ADD_TO_QUEUE.ordinal(), bundle);
        }
        onBeforeExecute(intent, dataSourceRequest, resultReceiver, bundle);
        final RequestExecutor requestExecutor = getRequestExecutor();
        requestExecutor.execute(new ExecuteRunnable(resultReceiver) {

            @Override
            public void run() {
                AbstractExecutorService.this.run(this, intent, dataSourceRequest, bundle, resultReceiver);
            }

            @Override
            public String createKey() {
                return dataSourceRequest.toUriParams();
            }

            @Override
            protected void onDone() {
                if (requestExecutor.isEmpty()) {
                    stopSelf();
                    Log.xd(AbstractExecutorService.this, "stop from run");
                }
            }

        });
	}

    protected abstract void run(ExecuteRunnable runnable, final Intent intent, final DataSourceRequest dataSourceRequest, final Bundle bundle, final ResultReceiver resultReceiver);

    protected RequestExecutor getRequestExecutor() {
        if (mRequestExecutor == null) {
            mRequestExecutor = createExecutorService();
        }
        return mRequestExecutor;
    }

    @Override
    public void onDestroy() {
        mRequestExecutor = null;
        super.onDestroy();
    }

    protected RequestExecutor createExecutorService() {
        return new RequestExecutor(RequestExecutor.DEFAULT_POOL_SIZE, new LIFOLinkedBlockingDeque<Runnable>());
    }

    protected void onBeforeExecute(Intent intent, DataSourceRequest dataSourceRequest, ResultReceiver resultReceiver, Bundle bundle) {

    }

    @SuppressWarnings("unchecked")
    public static Object execute(Context context, boolean cacheable, String processorKey, String datasourceKey, DataSourceRequest dataSourceRequest, Bundle bundle) throws Exception {
        final IProcessor processor = (IProcessor) AppUtils.get(context, processorKey);
        final IDataSource datasource = (IDataSource) AppUtils.get(context, datasourceKey);
        Object result = processor.execute(dataSourceRequest, datasource, datasource.getSource(dataSourceRequest));
        if (cacheable) {
            processor.cache(context, dataSourceRequest, result);
        } else {
            if (bundle == null) {
                return result;
            }
            if (result instanceof Parcelable) {
                bundle.putParcelable(StatusResultReceiver.RESULT_KEY, (Parcelable) result);
            } else if (result instanceof Parcelable[]) {
                bundle.putParcelableArray(StatusResultReceiver.RESULT_KEY, (Parcelable[]) result);
            } else if (result instanceof Serializable) {
                bundle.putSerializable(StatusResultReceiver.RESULT_KEY, (Serializable) result);
            } else if (result instanceof ArrayList<?>) {
                bundle.putParcelableArrayList(StatusResultReceiver.RESULT_KEY, (ArrayList<? extends Parcelable>) result);
            }
        }
        return result;
    }

}