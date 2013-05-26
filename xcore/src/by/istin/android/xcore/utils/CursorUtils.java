package by.istin.android.xcore.utils;

import android.database.Cursor;

public class CursorUtils {

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

}
