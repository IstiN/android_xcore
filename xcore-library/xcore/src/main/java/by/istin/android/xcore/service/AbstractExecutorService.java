/**
 * 
 */
package by.istin.android.xcore.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.service.manager.AbstractRequestManager;
import by.istin.android.xcore.service.manager.IRequestManager;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Log;

/**
 * @author IstiN
 *
 */
public abstract class AbstractExecutorService extends Service {

    private static final String ACTION_STOP = "stop";
	protected static final String DATA_SOURCE_KEY = "dataSourceKey";
    protected static final String PROCESSOR_KEY = "processorKey";
    protected static final String RESULT_RECEIVER = "resultReceiver";

    public static void stop(Context context, ResultReceiver receiver, Class<? extends AbstractExecutorService> serviceClass, boolean isLocalAppService) {
        if (isLocalAppService) {
            IRequestManager.Impl.get(serviceClass, context).stop(receiver);
            return;
        }
        Intent intent = new Intent(context, serviceClass);
        intent.putExtra(ACTION_STOP, true);
        intent.putExtra(RESULT_RECEIVER, receiver);
        context.startService(intent);
    }

    protected static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, Class<?> serviceClass) {
        execute(context, dataSourceRequest, processorKey, dataSourceKey, null, serviceClass, true);
    }

    protected static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver, Class<?> serviceClass, boolean isLocalAppService) {
        if (context == null) {
            context = ContextHolder.getInstance().getContext();
            if (context == null) {
                return;
            }
        }
        if (isLocalAppService) {
            IRequestManager.Impl.get(serviceClass, context).onHandleRequest(context, dataSourceRequest, processorKey, dataSourceKey, resultReceiver);
            return;
        }
        Intent intent = createStartIntent(context, dataSourceRequest, processorKey, dataSourceKey, resultReceiver, serviceClass);
        if (intent == null) {
            return;
        }
        context.startService(intent);
    }

    protected static Intent createStartIntent(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver, Class<?> serviceClass) {
        if (context == null) {
            context = ContextHolder.getInstance().getContext();
            if (context == null) {
                return null;
            }
        }
        Intent intent = new Intent(context, serviceClass);
        intent.putExtra(DATA_SOURCE_KEY, dataSourceKey);
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
        IRequestManager requestManager = IRequestManager.Impl.get(this.getClass(), this);
        final ResultReceiver resultReceiver = intent.getParcelableExtra(RESULT_RECEIVER);
        boolean isStopAction = intent.getBooleanExtra(ACTION_STOP, false);
        if (isStopAction) {
            Log.xd(this, "action stop");
            requestManager.stop(resultReceiver);
            Log.xd(this, "action stop executor recreated");
            return;
        }
		final DataSourceRequest dataSourceRequest = DataSourceRequest.fromIntent(intent);
        requestManager.onHandleRequest(this, dataSourceRequest, intent.getStringExtra(PROCESSOR_KEY), intent.getStringExtra(DATA_SOURCE_KEY), resultReceiver);
	}

    @SuppressWarnings("unchecked")
    public static Object execute(Context context, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest, Bundle bundle) throws Exception {
        return AbstractRequestManager.execute(context, processorKey, dataSourceKey, dataSourceRequest, bundle);
    }

}