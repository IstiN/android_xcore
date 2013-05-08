package by.istin.android.xcore.utils;

import android.database.Cursor;

public class CursorUtils {

	public static String getString(String columnName, Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(columnName));
	}
	
	public static Integer getInt(String columnName, Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(columnName));
	}
	
	public static byte getByte(String columnName, Cursor cursor) {
		return getInt(columnName, cursor).byteValue();
	}
	
	public static Double getDouble(String columnName, Cursor cursor) {
		return cursor.getDouble(cursor.getColumnIndex(columnName));
	}
	
	public static Float getFloat(String columnName, Cursor cursor) {
		return cursor.getFloat(cursor.getColumnIndex(columnName));
	}
	
	public static Long getLong(String columnName, Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndex(columnName));
	}
	
	public static Short getShort(String columnName, Cursor cursor) {
		return cursor.getShort(cursor.getColumnIndex(columnName));
	}
	
	public static byte[] getBlob(String columnName, Cursor cursor) {
		return cursor.getBlob(cursor.getColumnIndex(columnName));
	}

}
