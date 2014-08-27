package by.istin.android.xcore.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;

public class CacheRequestHelper {

    public static boolean cacheIfNotCached(Context context, DataSourceRequest dataSourceRequest, long requestId, String processorKey, String dataSourceKey) {
        long currentTimeMillis = System.currentTimeMillis();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ModelContract.getUri(DataSourceRequestEntity.class);
        Log.xd(context, "request cache " + currentTimeMillis);
        contentResolver.delete(uri,
                DataSourceRequestEntity.DATA_SOURCE_KEY + " IS NULL OR "
                + DataSourceRequestEntity.PROCESSOR_KEY + " IS NULL OR ("
                + "? - " + DataSourceRequestEntity.EXPIRATION + ") < " + DataSourceRequestEntity.LAST_UPDATE , new String[]{
                String.valueOf(currentTimeMillis)
        });
        Uri requestUri = ModelContract.getUri(DataSourceRequestEntity.class, requestId);
        Cursor cursor = contentResolver.query(requestUri, new String[]{DataSourceRequestEntity.LAST_UPDATE}, null, null, null);
        try {
            if (CursorUtils.isEmpty(cursor)) {
                contentResolver.insert(uri, DataSourceRequestEntity.prepare(dataSourceRequest, processorKey, dataSourceKey));
            } else {
                cursor.moveToFirst();
                Long lastUpdate = CursorUtils.getLong(DataSourceRequestEntity.LAST_UPDATE, cursor);
                if (currentTimeMillis - dataSourceRequest.getCacheExpiration() < lastUpdate) {
                    return true;
                } else {
                    contentResolver.insert(uri, DataSourceRequestEntity.prepare(dataSourceRequest, processorKey, dataSourceKey));
                }
            }
        } finally {
            CursorUtils.close(cursor);
        }
        return false;
    }

}
