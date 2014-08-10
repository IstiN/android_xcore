package by.istin.android.xcore.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.service.CacheRequestHelper;
import by.istin.android.xcore.service.DataSourceService;
import by.istin.android.xcore.service.RequestExecutor;
import by.istin.android.xcore.service.StatusResultReceiver;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.Holder;

public class DefaultRequestManager extends AbstractRequestManager {

    private final Object mDbLockFlag = new Object();

    private static final String SERVICE_KEY = IRequestManager.APP_SERVICE_KEY + DataSourceService.class.getName();

    @Override
    public String getAppServiceKey() {
        return SERVICE_KEY;
    }

    @Override
    public void run(Context context, String processorKey, String dataSourceKey, RequestExecutor.ExecuteRunnable executeRunnable, DataSourceRequest dataSourceRequest, Bundle dataSourceRequestBundle, ResultReceiver resultReceiver) {
        executeRunnable.sendStatus(StatusResultReceiver.Status.START, dataSourceRequestBundle);
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
            if (isExecuteJoinedRequestsSuccessful(context, executeRunnable, dataSourceRequest, dataSourceRequestBundle)) {
                executeRunnable.sendStatus(StatusResultReceiver.Status.CACHED, dataSourceRequestBundle);
            }
            return;
        }
        try {
            execute(context, isCacheable, processorKey, dataSourceKey, dataSourceRequest, dataSourceRequestBundle);
            if (isExecuteJoinedRequestsSuccessful(context, executeRunnable, dataSourceRequest, dataSourceRequestBundle)) {
                executeRunnable.sendStatus(StatusResultReceiver.Status.DONE, dataSourceRequestBundle);
            }
        } catch (Exception e) {
            if (!requestIdHolder.isNull()) {
                synchronized (mDbLockFlag) {
                    context.getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestIdHolder.get()), null, null);
                }
            }
            try {
                dataSourceRequestBundle.putSerializable(StatusResultReceiver.ERROR_KEY, e);
                executeRunnable.sendStatus(StatusResultReceiver.Status.ERROR, dataSourceRequestBundle);
            } catch (RuntimeException e1) {
                dataSourceRequestBundle.remove(StatusResultReceiver.ERROR_KEY);
                executeRunnable.sendStatus(StatusResultReceiver.Status.ERROR, dataSourceRequestBundle);
            }
        }
    }

    private boolean isExecuteJoinedRequestsSuccessful(Context context, final RequestExecutor.ExecuteRunnable parentRunnable, DataSourceRequest dataSourceRequest, Bundle statusBundle) {
        DataSourceRequest joinedRequest = dataSourceRequest.getJoinedRequest();
        if (joinedRequest != null) {
            ErrorRedirectExecuteRunnable redirectRunnable = new ErrorRedirectExecuteRunnable(parentRunnable);
            String joinedDataSource = dataSourceRequest.getJoinedDataSourceKey();
            String joinedProcessor = dataSourceRequest.getJoinedProcessorKey();
            run(context, joinedProcessor, joinedDataSource, redirectRunnable, joinedRequest, statusBundle, null);
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
            super(new ResultReceiver(new Handler(Looper.getMainLooper())));
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
