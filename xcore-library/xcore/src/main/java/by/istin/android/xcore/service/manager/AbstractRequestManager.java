package by.istin.android.xcore.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.io.Serializable;

import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.CacheRequestHelper;
import by.istin.android.xcore.service.RequestExecutor;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.service.assist.LIFOLinkedBlockingDeque;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;

public abstract class AbstractRequestManager implements IRequestManager {

    private RequestExecutor mRequestExecutor;

    @Override
    public void stop(ResultReceiver resultReceiver) {
        Log.xd(this, "action stop");
        RequestExecutor requestExecutor = mRequestExecutor;
        if (requestExecutor != null) {
            requestExecutor.stop(resultReceiver);
            mRequestExecutor = null;
        } else {
            resultReceiver.send(0, null);
        }
    }

    @Override
    public RequestExecutor getRequestExecutor() {
        if (mRequestExecutor == null) {
            mRequestExecutor = createExecutorService();
        }
        return mRequestExecutor;
    }

    @Override
    public RequestExecutor createExecutorService() {
        return new RequestExecutor(RequestExecutor.DEFAULT_POOL_SIZE, new LIFOLinkedBlockingDeque<Runnable>());
    }

    @Override
    public void onHandleRequest(final Context context, final DataSourceRequest dataSourceRequest, final String processorKey, final String dataSourceKey, final StatusResultReceiver resultReceiver) {
        final Bundle bundle = new Bundle();
        dataSourceRequest.toBundle(bundle);
        if (resultReceiver != null) {
            resultReceiver.send(StatusResultReceiver.Status.ADD_TO_QUEUE.ordinal(), bundle);
        }
        final RequestExecutor requestExecutor = getRequestExecutor();
        requestExecutor.execute(new RequestExecutor.ExecuteRunnable(resultReceiver) {

            @Override
            public void run() {
                AbstractRequestManager.this.run(context, processorKey, dataSourceKey, this, dataSourceRequest, bundle, resultReceiver);
            }

            @Override
            public String createKey() {
                return dataSourceRequest.toUriParams();
            }

            @Override
            protected void onDone() {
                if (requestExecutor.isEmpty()) {
                    stop(null);
                    Log.xd(AbstractRequestManager.this, "stop from run");
                }
            }

        });
    }

    @Override
    public String getAppServiceKey() {
        return IRequestManager.APP_SERVICE_KEY;
    }

    private static final Object mDbLockFlag = new Object();

    @SuppressWarnings("unchecked")
    public static Object execute(Context context, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest) throws Exception {
        return execute(context, processorKey, dataSourceKey, dataSourceRequest, null, null);
    }

    @SuppressWarnings("unchecked")
    public static Object execute(Context context, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest, Bundle bundle) throws Exception {
        return execute(context, processorKey, dataSourceKey, dataSourceRequest, bundle, null);
    }

    @SuppressWarnings("unchecked")
    public static Object execute(Context context, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest, Bundle bundle, Runnable cachedRunnable) throws Exception {
        boolean isCacheable = dataSourceRequest.isCacheable();
        boolean isForceUpdateData = dataSourceRequest.isForceUpdateData();
        boolean isAlreadyCached = false;
        Holder<Long> requestIdHolder = new Holder<Long>();
        synchronized (mDbLockFlag) {
            if (isCacheable && !isForceUpdateData) {
                long requestId = DataSourceRequestEntity.generateId(dataSourceRequest);
                requestIdHolder.set(requestId);
                isAlreadyCached = CacheRequestHelper.cacheIfNotCached(context, dataSourceRequest, requestId);
            }
            if (!isAlreadyCached) {
                context.getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{dataSourceRequest.getUri()});
            }
        }
        if (isAlreadyCached) {
            if (cachedRunnable != null) {
                cachedRunnable.run();
            }
            return null;
        }
        try {
            return internalExecute(context, isCacheable, processorKey, dataSourceKey, dataSourceRequest, bundle);
        } catch (Exception e) {
            if (!requestIdHolder.isNull()) {
                synchronized (mDbLockFlag) {
                    context.getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestIdHolder.get()), null, null);
                }
            }
            throw e;
        }

    }

    @SuppressWarnings("unchecked")
    private static Object internalExecute(Context context, boolean cacheable, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest, Bundle bundle) throws Exception {
        final IProcessor processor = AppUtils.get(context, processorKey);
        final IDataSource dataSource = AppUtils.get(context, dataSourceKey);
        Object result = processor.execute(dataSourceRequest, dataSource, dataSource.getSource(dataSourceRequest));
        if (cacheable) {
            processor.cache(context, dataSourceRequest, result);
            if (bundle == null) {
                return result;
            }
            if (result instanceof Parcelable) {
                bundle.putParcelable(StatusResultReceiver.RESULT_KEY, (Parcelable) result);
            } else if (result instanceof Parcelable[]) {
                bundle.putParcelableArray(StatusResultReceiver.RESULT_KEY, (Parcelable[]) result);
            } else if (result instanceof Serializable) {
                bundle.putSerializable(StatusResultReceiver.RESULT_KEY, (Serializable) result);
            }
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
            }
        }
        return result;
    }
}
