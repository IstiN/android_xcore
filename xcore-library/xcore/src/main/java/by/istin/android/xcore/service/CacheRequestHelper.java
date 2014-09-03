package by.istin.android.xcore.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;

public class CacheRequestHelper {

    public static class CacheRequestResult {

        private List<Runnable> listRunnable = new ArrayList<Runnable>();

        private boolean isAlreadyCached = false;

        private boolean isDataSourceCached = false;

        public List<Runnable> getListRunnable() {
            return listRunnable;
        }

        public boolean isAlreadyCached() {
            return isAlreadyCached;
        }

        public boolean isDataSourceCached() {
            return isDataSourceCached;
        }

        public void setDataSourceCached(boolean isDataSourceCached) {
            this.isDataSourceCached = isDataSourceCached;
            if (!isDataSourceCached) {
                for (Runnable runnable : listRunnable) {
                    runnable.run();
                }
            }
        }
    }

    public static CacheRequestResult cacheIfNotCached(Context context, DataSourceRequest dataSourceRequest, long requestId, String processorKey, String dataSourceKey) {
        long currentTimeMillis = System.currentTimeMillis();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ModelContract.getUri(DataSourceRequestEntity.class);
        Log.xd(context, "request cache " + currentTimeMillis+ " " + requestId);
        Log.xd(context, "request cache cacheIfNotCached requestId " + requestId);
        Log.xd(context, "request cache getCacheExpiration " + dataSourceRequest.getCacheExpiration() + " " + requestId);
        Uri requestUri = ModelContract.getUri(DataSourceRequestEntity.class, requestId);
        Cursor cursor = contentResolver.query(requestUri, new String[]{DataSourceRequestEntity.LAST_UPDATE}, null, null, null);
        CacheRequestResult cacheRequestResult = new CacheRequestResult();
        try {
            if (CursorUtils.isEmpty(cursor)) {
                cacheRequestResult.isAlreadyCached = false;
                contentResolver.insert(uri, DataSourceRequestEntity.prepare(dataSourceRequest, processorKey, dataSourceKey));
            } else {
                cursor.moveToFirst();
                Long lastUpdate = CursorUtils.getLong(DataSourceRequestEntity.LAST_UPDATE, cursor);
                if (currentTimeMillis - dataSourceRequest.getCacheExpiration() < lastUpdate) {
                    cacheRequestResult.isAlreadyCached = true;
                } else {
                    cacheRequestResult.isAlreadyCached = false;
                    contentResolver.insert(uri, DataSourceRequestEntity.prepare(dataSourceRequest, processorKey, dataSourceKey));
                }
            }
        } finally {
            CursorUtils.close(cursor);
        }
        return cacheRequestResult;
    }

    public static boolean isDataSourceSupportCacheValidation(Context context, String dataSourceKey) {
        IDataSource dataSource = AppUtils.get(context, dataSourceKey);
        return (dataSource instanceof IDataSource.ICacheValidationSupport);
    }
}
