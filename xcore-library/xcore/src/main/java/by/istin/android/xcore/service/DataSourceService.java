/**
 * 
 */
package by.istin.android.xcore.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

/**
 * @author IstiN
 *
 */
public class DataSourceService extends AbstractExecutorService {

    private final Object mDbLockFlag = new Object();

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey) {
        execute(context, dataSourceRequest, processorKey, dataSourceKey, DataSourceService.class);
    }

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver) {
        execute(context, dataSourceRequest, processorKey, dataSourceKey, resultReceiver, DataSourceService.class);
    }

    public static Intent createStartIntent(Context context, DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey, StatusResultReceiver resultReceiver) {
        return createStartIntent(context, dataSourceRequest, processorKey, dataSourceKey, resultReceiver, DataSourceService.class);
    }

    @Override
    protected void run(final RequestExecutor.ExecuteRunnable runnable, Intent intent, final DataSourceRequest dataSourceRequest, Bundle bundle, ResultReceiver resultReceiver) {
        String processorKey = dataSourceRequest.getProcessorKey();
        String dataSourceKey = dataSourceRequest.getDataSourceKey();
        if (StringUtil.isEmpty(processorKey)) {
            processorKey = intent.getStringExtra(PROCESSOR_KEY);
            dataSourceRequest.setProcessorKey(processorKey);
        }
        if (StringUtil.isEmpty(dataSourceKey)) {
            dataSourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
            dataSourceRequest.setDataSourceKey(dataSourceKey);
        }
        if (StringUtil.isEmpty(processorKey) || StringUtil.isEmpty(dataSourceKey)) {
            throw new IllegalArgumentException("processorKey dataSourceKey can't be empty");
        }
        runnable.sendStatus(StatusResultReceiver.Status.START, bundle);
        boolean isCacheable = dataSourceRequest.isCacheable();
        boolean isForceUpdateData = dataSourceRequest.isForceUpdateData();
        CacheRequestHelper.CacheRequestResult cacheRequestResult = new CacheRequestHelper.CacheRequestResult();
        Holder<Long> requestIdHolder = new Holder<Long>();
        synchronized (mDbLockFlag) {
            Log.xd(this, "request cache isCacheable " + isCacheable + " isForceUpdateData " + isForceUpdateData + " " + DataSourceRequestEntity.generateId(dataSourceRequest, processorKey, dataSourceKey));
            if (isCacheable && !isForceUpdateData) {
                long requestId = DataSourceRequestEntity.generateId(dataSourceRequest, processorKey, dataSourceKey);
                requestIdHolder.set(requestId);
                cacheRequestResult = CacheRequestHelper.cacheIfNotCached(this, dataSourceRequest, requestId, processorKey, dataSourceKey);
            }
            if (!cacheRequestResult.isAlreadyCached()) {
                if (!CacheRequestHelper.isDataSourceSupportCacheValidation(this, dataSourceKey)) {
                    cacheRequestResult.getListRunnable().add(new Runnable() {
                        @Override
                        public void run() {
                            getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{dataSourceRequest.getUri()});
                        }
                    });
                } else {
                    getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{dataSourceRequest.getUri()});
                }
            }
        }
        if (cacheRequestResult.isAlreadyCached()) {
            if (isExecuteJoinedRequestsSuccessful(runnable, intent, dataSourceRequest, bundle)) {
                runnable.sendStatus(StatusResultReceiver.Status.CACHED, bundle);
            }
            return;
        }
        try {
            execute(this, isCacheable, processorKey, dataSourceKey, dataSourceRequest, bundle, cacheRequestResult);
            if (isExecuteJoinedRequestsSuccessful(runnable, intent, dataSourceRequest, bundle)) {
                if (cacheRequestResult.isDataSourceCached()) {
                    runnable.sendStatus(StatusResultReceiver.Status.CACHED, bundle);
                } else {
                    runnable.sendStatus(StatusResultReceiver.Status.DONE, bundle);
                }
            }
            getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class),
                    DataSourceRequestEntity.DATA_SOURCE_KEY + " IS NULL OR "
                            + DataSourceRequestEntity.PROCESSOR_KEY + " IS NULL OR ("
                            + "? - " + DataSourceRequestEntity.EXPIRATION + ") < " + DataSourceRequestEntity.LAST_UPDATE, new String[]{
                            String.valueOf(System.currentTimeMillis())
                    });
        } catch (Exception e) {
            if (!requestIdHolder.isNull()) {
                synchronized (mDbLockFlag) {
                    getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestIdHolder.get()), null, null);
                }
            }
            try {
                bundle.putSerializable(StatusResultReceiver.ERROR_KEY, e);
                runnable.sendStatus(StatusResultReceiver.Status.ERROR, bundle);
            } catch (RuntimeException e1) {
                bundle.remove(StatusResultReceiver.ERROR_KEY);
                runnable.sendStatus(StatusResultReceiver.Status.ERROR, bundle);
            }
        }
    }

    private boolean isExecuteJoinedRequestsSuccessful(final RequestExecutor.ExecuteRunnable parentRunnable, Intent intent, DataSourceRequest dataSourceRequest, Bundle statusBundle) {
        DataSourceRequest joinedRequest = dataSourceRequest.getJoinedRequest();
        if (joinedRequest != null) {
            ErrorRedirectExecuteRunnable redirectRunnable = new ErrorRedirectExecuteRunnable(parentRunnable);
            String joinedDataSource = dataSourceRequest.getJoinedDataSourceKey();
            String joinedProcessor = dataSourceRequest.getJoinedProcessorKey();
            Intent joinIntent = new Intent();
            joinIntent.putExtra(DATA_SOURCE_KEY, joinedDataSource);
            joinIntent.putExtra(PROCESSOR_KEY, joinedProcessor);
            run(redirectRunnable, joinIntent, joinedRequest, statusBundle, null);
            if (redirectRunnable.isError) {
                return false;
            }
        }
        return true;
    }


    private class ErrorRedirectExecuteRunnable extends RequestExecutor.ExecuteRunnable {

        private final RequestExecutor.ExecuteRunnable parentRunnable;

        private boolean isError = false;

        public ErrorRedirectExecuteRunnable(RequestExecutor.ExecuteRunnable parentRunnable) {
            super(new ResultReceiver(new Handler(DataSourceService.this.getMainLooper())));
            this.parentRunnable = parentRunnable;
        }

        @Override
        public String createKey() {
            //used only like redirect to parent request
            return null;
        }

        @Override
        protected void onDone() {
            //used only like redirect to parent request
        }

        @Override
        public void run() {
            //used only like redirect to parent request
        }

        @Override
        public void sendStatus(StatusResultReceiver.Status status, Bundle bundle) {
            if (status == StatusResultReceiver.Status.ERROR) {
                parentRunnable.sendStatus(status, bundle);
                isError = true;
            }
        }

        public boolean isError() {
            return isError;
        }
    }
}