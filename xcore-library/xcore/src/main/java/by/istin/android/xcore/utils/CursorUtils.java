package by.istin.android.xcore.utils;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Build;

import java.util.List;

public final class CursorUtils {

    public static Cursor listContentValuesToCursor(List<ContentValues> listContentValues, String ... defaultColumnsIfNull) {
        return ContentUtils.listContentValuesToCursor(listContentValues, defaultColumnsIfNull);
    }

	public static String getString(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getString(columnIndex);
	}
	
	public static Integer getInt(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.getInt(columnIndex);
	}
	
	public static byte getByte(String columnName, Cursor cursor) {
		return getInt(columnName, cursor).byteValue();
	}
	
	public static Double getDouble(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getDouble(columnIndex);
	}
	
	public static Float getFloat(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getFloat(columnIndex);
	}
	
	public static Long getLong(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getLong(columnIndex);
	}
	
	public static Short getShort(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getShort(columnIndex);
	}
	
	public static byte[] getBlob(String columnName, Cursor cursor) {
		int columnIndex = cursor.getColumnIndex(columnName);
		if (columnIndex == -1) {
			return null;
		}
		return cursor.getBlob(columnIndex);
	}

	public static boolean isEmpty(Cursor cursor) {
		return cursor == null || cursor.getCount() == 0;
	}

	public static void close(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

    public static boolean isClosed(Cursor cursor) {
        return cursor == null || cursor.isClosed();
    }

    public static int getSize(Cursor cursor) {
        if (cursor == null || cursor.isClosed()) {
            return 0;
        }
        return cursor.getCount();
    }

    public static boolean getBoolean(String columnName, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return columnIndex != -1 && cursor.getInt(columnIndex) == 1;
    }

    public static abstract class Converter {

        public abstract void convert(Cursor cursor, ContentValues contentValues);

        public static Converter get() {
            return new Converter() {
                @Override
                public void convert(Cursor cursor, ContentValues contentValues) {
                    cursorRowToContentValues(cursor, contentValues);
                }
            };
        }
    }

    /**
     * Read the entire contents of a cursor row and store them in a ContentValues.
     *
     * @param cursor the cursor to read from.
     * @param values the {@link ContentValues} to put the row into.
     */
    public static void cursorRowToContentValues(Cursor cursor, ContentValues values) {
        AbstractWindowedCursor awc =
                (cursor instanceof AbstractWindowedCursor) ? (AbstractWindowedCursor) cursor : null;

        String[] columns = cursor.getColumnNames();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            if (awc != null && isBlob(awc, i)) {
                values.put(columns[i], cursor.getBlob(i));
            } else {
                values.put(columns[i], cursor.getString(i));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isBlob(AbstractWindowedCursor awc, int columnIndex) {
        if (UiUtil.hasHoneycomb()) {
            int type = awc.getType(columnIndex);
            return type == AbstractWindowedCursor.FIELD_TYPE_BLOB;
        } else {
            return awc.isBlob(columnIndex);
        }
    }

    public static void convertToContentValuesAndClose(Cursor cursor, List<ContentValues> list) {
        convertToContentValuesAndClose(cursor, list, Converter.get());
    }

    public static void convertToContentValuesAndClose(Cursor cursor, List<ContentValues> list, Converter converter) {
        convertToContentValues(cursor, list, converter);
        close(cursor);
    }

    public static void convertToContentValues(Cursor cursor, List<ContentValues> list, Converter converter) {
        if (isEmpty(cursor)) {
            return;
        }
        cursor.moveToFirst();
        do {
            ContentValues contentValues = new ContentValues();
            converter.convert(cursor, contentValues);
            list.add(contentValues);
        } while (cursor.moveToNext());
    }

    public static void putIntValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getInt(key, cursor));
    }

    public static void putLongValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getLong(key, cursor));
    }

    public static void putStringValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getString(key, cursor));
    }

    public static void putDoubleValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getDouble(key, cursor));
    }

    public static void putByteValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getByte(key, cursor));
    }

    public static void putBlobValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getBlob(key, cursor));
    }

    public static void putBooleanValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getBoolean(key, cursor));
    }

    public static void putFloatValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getFloat(key, cursor));
    }

    public static void putShortValue(String key, Cursor cursor, ContentValues contentValues) {
        contentValues.put(key, getShort(key, cursor));
    }

    public static void cursorRowToContentValues(Class<?> clazz, Cursor cursor, ContentValues contentValues) {
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
    }

}
