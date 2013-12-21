/**
 * 
 */
package by.istin.android.xcore.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.source.sync.helper.SyncHelper;
import by.istin.android.xcore.utils.Log;

/**
 * @author IstiN
 *
 */
public class SyncDataSourceService extends DataSourceService {

    private static Object sDbLockFlag = new Object();

    private static ExecutorService sSingleExecutor = Executors.newSingleThreadExecutor();

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void execute(final Context context, final DataSourceRequest dataSourceRequest, final String processorKey, final String datasourceKey) {
        SyncHelper.get(context).addSyncAccount();
        StatusResultReceiver wrappedReceiver = createWrappedReceiver(context, dataSourceRequest, processorKey, datasourceKey, null);
        execute(context, dataSourceRequest, processorKey, datasourceKey, wrappedReceiver, SyncDataSourceService.class);
    }

    public static void execute(final Context context, final DataSourceRequest dataSourceRequest, final String processorKey, final String datasourceKey, final StatusResultReceiver resultReceiver) {
        SyncHelper.get(context).addSyncAccount();
        StatusResultReceiver wrappedReceiver = createWrappedReceiver(context, dataSourceRequest, processorKey, datasourceKey, resultReceiver);
        execute(context, dataSourceRequest, processorKey, datasourceKey, wrappedReceiver, SyncDataSourceService.class);
    }

    private static StatusResultReceiver createWrappedReceiver(final Context context, final DataSourceRequest dataSourceRequest, final String processorKey, final String datasourceKey, final StatusResultReceiver resultReceiver) {
        return new StatusResultReceiver(sHandler) {

            @Override
            public void onStart(Bundle resultData) {
                if (resultReceiver != null) {
                    ((StatusResultReceiver)resultReceiver).onStart(resultData);
                }
            }

            @Override
            protected void onCached(Bundle resultData) {
                super.onCached(resultData);
                if (resultReceiver != null) {
                    ((StatusResultReceiver)resultReceiver).onCached(resultData);
                }
                removeSyncEntity(context, dataSourceRequest, datasourceKey, processorKey);
            }

            @Override
            public void onDone(Bundle resultData) {
                if (resultReceiver != null) {
                    ((StatusResultReceiver)resultReceiver).onDone(resultData);
                }
                removeSyncEntity(context, dataSourceRequest, datasourceKey, processorKey);
            }

            @Override
            public void onError(Exception exception) {
                if (resultReceiver != null) {
                    ((StatusResultReceiver)resultReceiver).onError(exception);
                }
                if (ErrorHandler.getErrorType(exception) != ErrorHandler.ErrorType.DEVELOPER_ERROR) {
                    Log.xe(context, "developer error", exception);
                    markSyncEntityAsError(context, dataSourceRequest, datasourceKey, processorKey, exception);
                } else {
                    removeSyncEntity(context, dataSourceRequest, datasourceKey, processorKey);
                }
            }
        };
    }

    @Override
    protected void run(RequestExecutor.ExecuteRunnable runnable, final Intent intent, final DataSourceRequest dataSourceRequest, final Bundle bundle, final ResultReceiver resultReceiver) {
        final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
        final String datasourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
        synchronized (sDbLockFlag) {
            super.run(runnable, intent, dataSourceRequest, bundle, resultReceiver);
        }
    }

    private static void markSyncEntityAsError(final Context context, final DataSourceRequest dataSourceRequest, final String datasourceKey, final String processorKey, Exception exception) {
        sSingleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (sDbLockFlag) {
                    ContentValues contentValues = SyncDataSourceRequestEntity.prepare(dataSourceRequest);
                    contentValues.put(SyncDataSourceRequestEntity.IS_ERROR, true);
                    contentValues.put(SyncDataSourceRequestEntity.DATASOURCE_KEY, datasourceKey);
                    contentValues.put(SyncDataSourceRequestEntity.PROCESSOR_KEY, processorKey);
                    context.getContentResolver().insert(ModelContract.getUri(SyncDataSourceRequestEntity.class), contentValues);
                }
            }
        });
    }

    private static void removeSyncEntity(final Context context, final DataSourceRequest dataSourceRequest, final String datasourceKey, final String processorKey) {
        sSingleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (sDbLockFlag) {
                    ContentValues contentValues = SyncDataSourceRequestEntity.prepare(dataSourceRequest);
                    Long requestId = contentValues.getAsLong(SyncDataSourceRequestEntity.ID);
                    contentValues.put(SyncDataSourceRequestEntity.DATASOURCE_KEY, datasourceKey);
                    contentValues.put(SyncDataSourceRequestEntity.PROCESSOR_KEY, processorKey);
                    context.getContentResolver().delete(ModelContract.getUri(SyncDataSourceRequestEntity.class, requestId), null, null);
                }
            }
        });
    }

    @Override
    protected void onBeforeExecute(final Intent intent, final DataSourceRequest dataSourceRequest, ResultReceiver resultReceiver, Bundle bundle) {
        final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
        final String datasourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
        sSingleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (sDbLockFlag) {
                    ContentValues contentValues = SyncDataSourceRequestEntity.prepare(dataSourceRequest);
                    contentValues.put(SyncDataSourceRequestEntity.DATASOURCE_KEY, datasourceKey);
                    contentValues.put(SyncDataSourceRequestEntity.PROCESSOR_KEY, processorKey);
                    getContentResolver().insert(ModelContract.getUri(SyncDataSourceRequestEntity.class), contentValues);
                }
            }
        });
    }

    @Override
    protected RequestExecutor createExecutorService() {
        return new RequestExecutor(1, new LinkedBlockingQueue<Runnable>());
    }
}