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
import android.os.ResultReceiver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

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
    protected void run(RequestExecutor.ExecuteRunnable runnable, Intent intent, DataSourceRequest dataSourceRequest, Bundle bundle, ResultReceiver resultReceiver) {
        runnable.sendStatus(StatusResultReceiver.Status.START, bundle);
        boolean isCacheable = dataSourceRequest.isCacheable();
        boolean isForceUpdateData = dataSourceRequest.isForceUpdateData();
        ContentValues contentValues = DataSourceRequestEntity.prepare(dataSourceRequest);
        Long requestId = contentValues.getAsLong(DataSourceRequestEntity.ID);
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
                            runnable.sendStatus(StatusResultReceiver.Status.CACHED, bundle);
                            return;
                        } else {
                            contentValues = DataSourceRequestEntity.prepare(dataSourceRequest);
                            getContentResolver().insert(ModelContract.getPaginatedUri(DataSourceRequestEntity.class), contentValues);
                        }
                    }
                } finally {
                    CursorUtils.close(cursor);
                }
            }
            String requestParentUri = dataSourceRequest.getRequestParentUri();
            if (!StringUtil.isEmpty(requestParentUri)) {
                getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.PARENT_URI + "=?", new String[]{requestParentUri});
            }
        }
        try {
            final String processorKey = intent.getStringExtra(PROCESSOR_KEY);
            final String datasourceKey = intent.getStringExtra(DATA_SOURCE_KEY);
            execute(this, isCacheable, processorKey, datasourceKey, dataSourceRequest, bundle);
            runnable.sendStatus(StatusResultReceiver.Status.DONE, bundle);
        } catch (Exception e) {
            synchronized (mDbLockFlag) {
                getContentResolver().delete(ModelContract.getUri(DataSourceRequestEntity.class, requestId), null, null);
            }
            bundle.putSerializable(StatusResultReceiver.ERROR_KEY, e);
            runnable.sendStatus(StatusResultReceiver.Status.ERROR, bundle);
        }
    }
}