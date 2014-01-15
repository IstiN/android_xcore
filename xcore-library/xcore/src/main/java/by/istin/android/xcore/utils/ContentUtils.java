package by.istin.android.xcore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.provider.ModelContract;

public class ContentUtils {

    public static ContentValues getEntity(Context context, Class<?> entityClass, Long id) {
        Uri uri = ModelContract.getUri(entityClass, id);
        return getEntity(context, uri);
    }

    public static ContentValues getEntity(Context context, Uri uri) {
        Cursor entityCursor = null;
        ContentValues values = null;
        try {
            entityCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (!CursorUtils.isEmpty(entityCursor) && entityCursor.moveToFirst()) {
                values = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(entityCursor, values);
            }
        } finally {
            CursorUtils.close(entityCursor);
        }
        return values;
    }

    public static void putEntity(Context context, Class<?> entityClass, ContentValues entity) {
        context.getContentResolver().insert(ModelContract.getUri(entityClass), entity);
    }

    public static void putEntities(Context context, Class<?> entityClass, List<ContentValues> entities) {
        if (entities == null) return;
        putEntities(context, entityClass, entities.toArray(new ContentValues[entities.size()]));
    }

    public static void putEntities(Context context, Class<?> entityClass, ContentValues... entity) {
        context.getContentResolver().bulkInsert(ModelContract.getUri(entityClass), entity);
    }

    public static void removeEntity(Context context, Class<?> entityClass, long id) {
        String where = BaseColumns._ID + "=?";
        removeEntities(context, entityClass, where, String.valueOf(id));
    }

    public static void removeEntities(Context context, Class<?> entityClass, String where, String ... selectionArgs) {
        Uri uri = ModelContract.getUri(entityClass);
        removeEntities(context, uri, where, selectionArgs);
    }

    public static void removeEntities(Context context, Uri uri, String where, String[] selectionArgs) {
        context.getContentResolver().delete(uri, where, selectionArgs);
    }

    public static ContentValues getEntity(Context context, Class<?> entityClass, String selection, String ... selectionArgs) {
        List<ContentValues> entities = getEntities(context, entityClass, selection, selectionArgs);
        if (entities == null || entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    public static List<ContentValues> getEntitiesWithOrder(Context context, Class<?> entityClass, String sortOrder, String selection, String... selectionArgs) {
        Uri uri = ModelContract.getUri(entityClass);
        return getEntities(context, uri, sortOrder, selection, selectionArgs);
    }

    public static List<ContentValues> getEntitiesFromSQL(Context context, String sql, String ... args) {
        Uri uri = ModelContract.getSQLQueryUri(sql, null);
        return getEntities(context, uri, null, null, args);
    }

    public static List<ContentValues> getEntities(Context context, Uri uri, String sortOrder, String selection, String[] selectionArgs) {
        Cursor entityCursor = null;
        List<ContentValues> result = null;
        try {
            entityCursor = context.getContentResolver().query(uri, null, selection, selectionArgs, sortOrder);
            if (!CursorUtils.isEmpty(entityCursor) && entityCursor.moveToFirst()) {
                result = new ArrayList<ContentValues>();
                CursorUtils.convertToContentValuesAndClose(entityCursor, result);
            }
        } finally {
            CursorUtils.close(entityCursor);
        }
        return result;
    }

    public static List<ContentValues> getEntities(Context context, Class<?> entityClass, String selection, String ... selectionArgs) {
        return getEntitiesWithOrder(context, entityClass, null, selection, selectionArgs);
    }
}
