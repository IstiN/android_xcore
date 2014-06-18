package by.istin.android.xcore.processor;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;

public abstract class AbstractDBProcessor<Result, DataSourceResult> implements IProcessor<Result, DataSourceResult> {

    public static void clearEntity(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz) {
        clearEntity(context, dataSourceRequest, clazz, false);
    }

    public static void clearEntity(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, boolean withNotify) {
        clearEntity(context, dataSourceRequest, clazz, null, null, withNotify);
    }

    public static void clearEntity(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, String selection, String[] selectionArgs, boolean withNotify) {
        Uri deleteUrl = null;
        if (withNotify) {
            deleteUrl = ModelContract.getUri(clazz);
        } else {
            deleteUrl = new ModelContract.UriBuilder(clazz).notNotifyChanges().build();
        }
        context.getContentResolver().delete(ModelContract.getUri(dataSourceRequest, deleteUrl), selection, selectionArgs);
    }

    public static void bulkInsert(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, ContentValues[] result) {
        bulkInsert(context, dataSourceRequest, clazz, result, true);
    }
    public static void bulkInsert(Context context, DataSourceRequest dataSourceRequest, Class<?> clazz, ContentValues[] result, boolean withNotify) {
        Uri uri = null;
        if (withNotify) {
            uri = ModelContract.getUri(dataSourceRequest, clazz);
        } else {
            uri = new ModelContract.UriBuilder(ModelContract.getUri(dataSourceRequest, clazz)).notNotifyChanges().build();
        }

        int rows = context.getContentResolver().bulkInsert(uri, result);
        if (rows == 0) {
            context.getContentResolver().notifyChange(ModelContract.getUri(clazz), null);
        }
    }

    public static void notifyChange(Context context, Class<?> clazz) {
        context.getContentResolver().notifyChange(ModelContract.getUri(clazz), null);
    }

}