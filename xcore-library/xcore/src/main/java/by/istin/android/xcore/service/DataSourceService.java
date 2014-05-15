/**
 * 
 */
package by.istin.android.xcore.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;

import java.io.Serializable;

/**
 * @author IstiN
 *
 */
public class DataSourceService extends AbstractExecutorService {

    private Object mDbLockFlag = new Object();

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey) {
        execute(context, dataSourceRequest, processorKey, datasourceKey, DataSourceService.class);
    }

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver) {
        execute(context, dataSourceRequest, processorKey, datasourceKey, resultReceiver, DataSourceService.class);
    }

    public static Intent createStartIntent(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver) {
        return createStartIntent(context, dataSourceRequest, processorKey, datasourceKey, resultReceiver, DataSourceService.class);
    }

    @Override
    protected void run(final RequestExecutor.ExecuteRunnable runnable, Intent intent, DataSourceRequest dataSourceRequest, Bundle bundle, ResultReceiver resultReceiver) {
        runnable.sendStatus(StatusResultReceiver.Status.START, bundle);
        boolean isCacheable = dataSourceRequest.isCacheable();
        boolean isForceUpdateData = dataSourceRequest.isForceUpdateData();
        ContentValues contentValues = DataSourceRequestEntity.prepare(dataSourceRequest);
        Long requestId = contentValues.getAsLong(DataSourceRequestEntity.ID);
        boolean isAlreadyCached = false;
        synchronized (mDbLockFlag) {
            if (isCacheable && !isForceUpdateData) {
                Cursor cursor = getContentResolver().query(ModelContract.getUri(DataSourceRequestEntity.class, requestId), null, null, null, null);
                try {
                    if (cursor == null || !cursor.moveToFirst()) {
                        getContentResolver().insert(ModelContract.getUri(DataSourceRequestEntity.class), contentValues);
                    } else {
                        ContentValues storedRequest = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cursor, storedRequest);
                        Long lastUpdate = storedRequest.getAsLong(DataSourceRequestEntity.LAST_UPDATE);
                        if (System.currentTimeMillis() - dataSourceRequest.getCacheExpiration() < lastUpdate) {
                            isAlreadyCached = true;
                        } else {
                            contentValues = DataSourceRequestEntity.prepare(dataSourceRequest);
                            getContentResolver().insert(ModelContract.getUri(DataSourceRequestEntity.class), contentValues);
                        }
                    }
                } finally {
                    CursorUtils.close(cursor);
                }
            }
            if (!isAlreadyCached) {
                String requestParentUri = dataSourceRequest.getRequestParentUri();
                if (!StringUtil.isEmpty(requestParentUri)) {
                    getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{requestParentUri});
                }
            }
        }
        if (isAlreadyCached) {
            if (isExecuteJoinedRequestsSuccessful(runnable, intent, dataSourceRequest, bundle)) {
                runnable.sendStatus(StatusResultReceiver.Status.CACHED, bundle);
            }
            return;
        }
        try {
            final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
            final String dataSourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
            execute(this, isCacheable, processorKey, dataSourceKey, dataSourceRequest, bundle);
            if (isExecuteJoinedRequestsSuccessful(runnable, intent, dataSourceRequest, bundle)) {
                runnable.sendStatus(StatusResultReceiver.Status.DONE, bundle);
            }
        } catch (Exception e) {
            synchronized (mDbLockFlag) {
                getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestId), null, null);
            }
            try {
                if (e instanceof Serializable) {
                    bundle.putSerializable(StatusResultReceiver.ERROR_KEY, e);
                } else {
                    bundle.putSerializable(StatusResultReceiver.ERROR_KEY, new Exception(e.getMessage()));
                }
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
            String joinedDataSource = dataSourceRequest.getJoinedDataSourceKey();
            String joinedProcessor = dataSourceRequest.getJoinedProcessorKey();
            ErrorRedirectExecuteRunnable redirectRunnable = new ErrorRedirectExecuteRunnable(parentRunnable);
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