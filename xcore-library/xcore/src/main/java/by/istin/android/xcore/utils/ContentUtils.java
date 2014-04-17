package by.istin.android.xcore.utils;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import by.istin.android.xcore.provider.ModelContract;

public class ContentUtils {

    public static ContentValues getEntity(Context context, Class<?> entityClass, Long id, String ... projection) {
        Uri uri = ModelContract.getUri(entityClass, id);
        return getEntity(context, uri, projection);
    }

    public static ContentValues getEntity(Context context, Uri uri, String ... projection) {
        return getEntity(context, uri, projection, null, null);
    }

    public static ContentValues getEntity(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs) {
        Cursor entityCursor = null;
        ContentValues values = null;
        try {
            entityCursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
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

    public static ContentValues getEntity(Context context, Class<?> entityClass, String[] projection, String selection, String ... selectionArgs) {
        List<ContentValues> entities = getEntities(context, projection, entityClass, selection, selectionArgs);
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

    public static List<ContentValues> getEntities(Context context, String[] projection, Uri uri, String sortOrder, String selection, String[] selectionArgs) {
        Cursor entityCursor = null;
        List<ContentValues> result = null;
        try {
            entityCursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            if (!CursorUtils.isEmpty(entityCursor) && entityCursor.moveToFirst()) {
                result = new ArrayList<ContentValues>();
                CursorUtils.convertToContentValuesAndClose(entityCursor, result);
            }
        } finally {
            CursorUtils.close(entityCursor);
        }
        return result;
    }

    public static List<ContentValues> getEntities(Context context, Uri uri, String sortOrder, String selection, String[] selectionArgs) {
        return getEntities(context, null, uri, sortOrder, selection, selectionArgs);
    }

    public static List<ContentValues> getEntities(Context context, String[] projection, Class<?> entityClass, String selection, String ... selectionArgs) {
        return getEntities(context, projection, ModelContract.getUri(entityClass), null, selection, selectionArgs);
    }

    public static List<ContentValues> getEntities(Context context, Class<?> entityClass, String selection, String ... selectionArgs) {
        return getEntitiesWithOrder(context, entityClass, null, selection, selectionArgs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Set<String> getKeys(ContentValues contentValues) {
        if (UiUtil.hasHoneycomb()) {
            return contentValues.keySet();
        } else {
            Set<Map.Entry<String, Object>> entries = contentValues.valueSet();
            Set set = new HashSet();
            for (Map.Entry<String, Object> objectEntry : entries) {
                set.add(objectEntry.getKey());
            }
            return set;
        }
    }

    public static Cursor listContentValuesToCursor(List<ContentValues> listContentValues) {
        if (listContentValues == null || listContentValues.isEmpty()) {
            return new MatrixCursor(new String[]{});
        }
        ContentValues contentValues = listContentValues.get(0);
        Set<String> keys = getKeys(contentValues);
        String[] columns = new String[keys.size()];
        columns = keys.toArray(columns);
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        for (ContentValues values : listContentValues) {
            Object[] objects = new Object[columns.length];
            for (int i = 0; i < columns.length; i++) {
                objects[i] = values.get(columns[i]);
            }
            matrixCursor.addRow(objects);
        }
        return matrixCursor;
    };
}
