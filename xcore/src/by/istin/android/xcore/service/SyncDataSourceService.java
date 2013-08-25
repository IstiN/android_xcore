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

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;

/**
 * @author IstiN
 *
 */
public class SyncDataSourceService extends DataSourceService {

    private Object mDbLockFlag = new Object();

    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    private Handler handler = new Handler(Looper.getMainLooper());

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey) {
        //TODO move result receiver from run
        execute(context, dataSourceRequest, processorKey, datasourceKey, SyncDataSourceService.class);
    }

    public static void execute(Context context, DataSourceRequest dataSourceRequest, String processorKey, String datasourceKey, StatusResultReceiver resultReceiver, Class<?> serviceClass) {
        //TODO move result receiver from run
        execute(context, dataSourceRequest, processorKey, datasourceKey, resultReceiver, SyncDataSourceService.class);
    }

    @Override
    protected void run(RequestExecutor.ExecuteRunnable runnable, final Intent intent, final DataSourceRequest dataSourceRequest, final Bundle bundle, final ResultReceiver resultReceiver) {
        final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
        final String datasourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
        synchronized (mDbLockFlag) {
            super.run(runnable, intent, dataSourceRequest, bundle, new StatusResultReceiver(handler) {

                @Override
                public void onStart(Bundle resultData) {
                    ((StatusResultReceiver)resultReceiver).onStart(resultData);
                }

                @Override
                protected void onCached(Bundle resultData) {
                    super.onCached(resultData);
                    ((StatusResultReceiver)resultReceiver).onCached(resultData);
                    removeSyncEntity(dataSourceRequest, datasourceKey, processorKey);
                }

                @Override
                public void onDone(Bundle resultData) {
                    ((StatusResultReceiver)resultReceiver).onDone(resultData);
                    removeSyncEntity(dataSourceRequest, datasourceKey, processorKey);
                }

                @Override
                public void onError(Exception exception) {
                    ((StatusResultReceiver)resultReceiver).onError(exception);
                    markSyncEntityAsError(dataSourceRequest, datasourceKey, processorKey, exception);
                }
            });
        }
    }

    private void markSyncEntityAsError(final DataSourceRequest dataSourceRequest, final String datasourceKey, final String processorKey, Exception exception) {
        singleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mDbLockFlag) {
                    ContentValues contentValues = SyncDataSourceRequestEntity.prepare(dataSourceRequest);
                    contentValues.put(SyncDataSourceRequestEntity.IS_ERROR, true);
                    contentValues.put(SyncDataSourceRequestEntity.DATASOURCE_KEY, datasourceKey);
                    contentValues.put(SyncDataSourceRequestEntity.PROCESSOR_KEY, processorKey);
                    getContentResolver().insert(ModelContract.getUri(SyncDataSourceRequestEntity.class), contentValues);
                }
            }
        });
    }

    private void removeSyncEntity(final DataSourceRequest dataSourceRequest, final String datasourceKey, final String processorKey) {
        singleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mDbLockFlag) {
                    ContentValues contentValues = SyncDataSourceRequestEntity.prepare(dataSourceRequest);
                    Long requestId = contentValues.getAsLong(SyncDataSourceRequestEntity.ID);
                    contentValues.put(SyncDataSourceRequestEntity.DATASOURCE_KEY, datasourceKey);
                    contentValues.put(SyncDataSourceRequestEntity.PROCESSOR_KEY, processorKey);
                    getContentResolver().delete(ModelContract.getUri(SyncDataSourceRequestEntity.class, requestId), null, null);
                }
            }
        });
    }

    @Override
    protected void onBeforeExecute(final Intent intent, final DataSourceRequest dataSourceRequest, ResultReceiver resultReceiver, Bundle bundle) {
        final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
        final String datasourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
        singleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (mDbLockFlag) {
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