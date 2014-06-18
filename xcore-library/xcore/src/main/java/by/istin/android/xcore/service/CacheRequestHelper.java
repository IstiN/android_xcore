package by.istin.android.xcore.service;

import android.content.Context;
import android.database.Cursor;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.CursorUtils;

/**
 * Created by Uladzimir_Klyshevich on 5/27/2014.
 */
public class CacheRequestHelper {

    public static boolean cacheIfNotCached(Context context, DataSourceRequest dataSourceRequest, long requestId) {
        Cursor cursor = context.getContentResolver().query(ModelContract.getUri(DataSourceRequestEntity.class, requestId), new String[]{DataSourceRequestEntity.LAST_UPDATE}, null, null, null);
        try {
            if (cursor == null || !cursor.moveToFirst()) {
                context.getContentResolver().insert(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.prepare(dataSourceRequest));
            } else {
                Long lastUpdate = CursorUtils.getLong(DataSourceRequestEntity.LAST_UPDATE, cursor);
                if (System.currentTimeMillis() - dataSourceRequest.getCacheExpiration() < lastUpdate) {
                    return true;
                } else {
                    context.getContentResolver().insert(ModelContract.getUri(DataSourceRequestEntity.class), DataSourceRequestEntity.prepare(dataSourceRequest));
                }
            }
        } finally {
            CursorUtils.close(cursor);
        }
        return false;
    }

}
