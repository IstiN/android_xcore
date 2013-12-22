package by.istin.android.xcore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.BaseColumns;

import by.istin.android.xcore.provider.ModelContract;

public class ContentUtils {

    public static ContentValues getEntity(Context context, Class<?> entityClass, Long id) {
        Cursor entityCursor = null;
        ContentValues values = null;
        try {
            entityCursor = context.getContentResolver().query(ModelContract.getUri(entityClass, id), null, null, null, null);
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

    public static void removeEntity(Context context, Class<?> entityClass, long id) {
        context.getContentResolver().delete(ModelContract.getUri(entityClass), BaseColumns._ID + "=?", new String[]{String.valueOf(id)});
    }
}
