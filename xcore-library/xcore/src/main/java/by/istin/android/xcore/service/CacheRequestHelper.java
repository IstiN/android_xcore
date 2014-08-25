package by.istin.android.xcore.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.CursorUtils;

public class CacheRequestHelper {

    public static boolean cacheIfNotCached(Context context, DataSourceRequest dataSourceRequest, long requestId, String processorKey, String dataSourceKey) {
        long currentTimeMillis = System.currentTimeMillis();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ModelContract.getUri(DataSourceRequestEntity.class);
        contentResolver.delete(uri,
                DataSourceRequestEntity.DATA_SOURCE_KEY + " IS NULL OR "
                + DataSourceRequestEntity.PROCESSOR_KEY + " IS NULL OR "
                + DataSourceRequestEntity.LAST_UPDATE + " + " + DataSourceRequestEntity.EXPIRATION + " < ?", new String[]{
                String.valueOf(currentTimeMillis)
        });
        Uri requestUri = ModelContract.getUri(DataSourceRequestEntity.class, requestId);
        Cursor cursor = contentResolver.query(requestUri, new String[]{DataSourceRequestEntity.LAST_UPDATE}, null, null, null);
        try {
            if (cursor == null || !cursor.moveToFirst()) {
                contentResolver.insert(uri, DataSourceRequestEntity.prepare(dataSourceRequest, processorKey, dataSourceKey));
            } else {
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
