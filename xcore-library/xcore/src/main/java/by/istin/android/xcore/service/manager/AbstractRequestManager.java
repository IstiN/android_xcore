package by.istin.android.xcore.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.io.Serializable;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.XCoreHelper;
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
import by.istin.android.xcore.utils.StringUtil;

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
            if (resultReceiver != null) {
                resultReceiver.send(0, null);
            }
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
    public void onHandleRequest(final Context context, final DataSourceRequest dataSourceRequest, final String processorKey, final String dataSourceKey, final ResultReceiver resultReceiver) {
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
                    //TODO stop(null);
                    /*
                    9-23 16:21:21.031  16270-16270/? E/AndroidRuntime﹕ FATAL EXCEPTION: main
    java.util.concurrent.RejectedExecutionException: Task xh@42ad27f0 rejected from java.util.concurrent.ThreadPoolExecutor@423605d0[Shutting down, pool size = 1, active threads = 1, queued tasks = 0, completed tasks = 0]
            at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:1979)
            at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:786)
            at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:1307)
                     */
                    Log.xd(AbstractRequestManager.this, "stop from run");
                }
            }

        });
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
    public static Object execute(final Context context, String processorKey, String dataSourceKey, final DataSourceRequest dataSourceRequest, Bundle bundle, Runnable cachedRunnable) throws Exception {
        String pKey = dataSourceRequest.getProcessorKey();
        String dsKey = dataSourceRequest.getDataSourceKey();
        if (StringUtil.isEmpty(pKey)) {
            dataSourceRequest.setProcessorKey(processorKey);
        } else {
            processorKey = pKey;
        }
        if (StringUtil.isEmpty(dsKey)) {
            dataSourceRequest.setDataSourceKey(dataSourceKey);
        } else {
            dataSourceKey = dsKey;
        }
        if (StringUtil.isEmpty(processorKey) || StringUtil.isEmpty(dataSourceKey)) {
            throw new IllegalArgumentException("processorKey dataSourceKey can't be empty");
        }
        boolean isCacheable = dataSourceRequest.isCacheable();
        boolean isForceUpdateData = dataSourceRequest.isForceUpdateData();
        CacheRequestHelper.CacheRequestResult cacheRequestResult = new CacheRequestHelper.CacheRequestResult();
        Holder<Long> requestIdHolder = new Holder<Long>();
        synchronized (mDbLockFlag) {
            if (isCacheable && !isForceUpdateData) {
                long requestId = DataSourceRequestEntity.generateId(dataSourceRequest, processorKey, dataSourceKey);
                requestIdHolder.set(requestId);
                cacheRequestResult = CacheRequestHelper.cacheIfNotCached(context, dataSourceRequest, requestId, processorKey, dataSourceKey);
            }
            if (!cacheRequestResult.isAlreadyCached()) {
                if (!CacheRequestHelper.isDataSourceSupportCacheValidation(context, dataSourceKey)) {
                    cacheRequestResult.getListRunnable().add(new Runnable() {
                        @Override
                        public void run() {
                            //TODO remove uri usage
                            XCoreHelper.get().getRequestsContentProvider().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{dataSourceRequest.getUri()});
                        }
                    });
                } else {
                    //TODO remove uri usage
                    XCoreHelper.get().getRequestsContentProvider().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{dataSourceRequest.getUri()});
                }
            }
        }
        if (cacheRequestResult.isAlreadyCached()) {
            if (cachedRunnable != null) {
                cachedRunnable.run();
            }
            return null;
        }
        try {
            Object result = internalExecute(context, isCacheable, processorKey, dataSourceKey, dataSourceRequest, bundle, cacheRequestResult);
            if (cacheRequestResult.isDataSourceCached() && cachedRunnable != null) {
                cachedRunnable.run();
            }
            return result;
        } catch (Exception e) {
            Log.xe(context, e);
            Log.xd(context, "processorKey " + processorKey + " dataSourceKey " + dataSourceKey + " dataSourceKey " + dataSourceRequest.getUri());
            if (!requestIdHolder.isNull()) {
                synchronized (mDbLockFlag) {
                    XCoreHelper.get().getRequestsContentProvider().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestIdHolder.get()), null, null);
                }
            }
            throw e;
        }

    }

    @SuppressWarnings("unchecked")
    private static Object internalExecute(Context context, boolean cacheable, String processorKey, String dataSourceKey, DataSourceRequest dataSourceRequest, Bundle bundle, CacheRequestHelper.CacheRequestResult cacheRequestResult) throws Exception {
        final IProcessor processor = AppUtils.get(context, processorKey);
        final IDataSource dataSource = AppUtils.get(context, dataSourceKey);
        Object source;
        Holder<Boolean> isCached = new Holder<Boolean>(false);
        if (CacheRequestHelper.isDataSourceSupportCacheValidation(context, dataSourceKey)) {
            source = dataSource.getSource(dataSourceRequest, isCached);
            if (isCached.get()) {
                if (cacheRequestResult != null) {
                    cacheRequestResult.setDataSourceCached(true);
                }
                return null;
            }
        } else {
            source = dataSource.getSource(dataSourceRequest, isCached);
        }
        Object result = processor.execute(dataSourceRequest, dataSource, source);
        if (cacheable) {
            processor.cache(context, dataSourceRequest, result);
            if (bundle == null) {
                return result;
            }
            initResult(bundle, result);
        } else {
            if (bundle == null) {
                return result;
            }
            initResult(bundle, result);
        }
        return result;
    }

    private static void initResult(Bundle bundle, Object result) {
        if (result == null) {
            return;
        }
        if (result instanceof Parcelable) {
            bundle.putParcelable(StatusResultReceiver.RESULT_KEY, (Parcelable) result);
        } else if (result instanceof Parcelable[]) {
            bundle.putParcelableArray(StatusResultReceiver.RESULT_KEY, (Parcelable[]) result);
        } else if (result instanceof Serializable) {
            bundle.putSerializable(StatusResultReceiver.RESULT_KEY, (Serializable) result);
        } else {
            throw new IllegalArgumentException("result of processor need to be Parcelable, Parcelable[] or Serializable");
        }
    }
}