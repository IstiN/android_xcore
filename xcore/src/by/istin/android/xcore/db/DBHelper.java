/**
 * 
 */
package by.istin.android.xcore.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbByteArray;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.utils.ReflectUtils;
import by.istin.android.xcore.utils.Log;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String TAG = DBHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME_TEMPLATE = "%s.main.xcore.db";

	private static final int DATABASE_VERSION = 1;
	
	private DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public DBHelper(Context context) {
		super(context, String.format(DATABASE_NAME_TEMPLATE, context.getPackageName()), null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
/*		db.execSQL("CREATE TABLE IF NOT EXISTS " + MODEL_TABLE_NAME + " ("
				+ ModelColumns.MODEL_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ModelColumns.DATA + " BLOB" + ");");
*/	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + MODEL_TABLE_NAME);
		onCreate(db);*/
	}

	/** The Constant CREATE_TABLE_SQL. */
	public static final String CREATE_FILES_TABLE_SQL = "CREATE TABLE IF NOT EXISTS  %1$s  ("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY ASC)";
	
	final static HashMap<Class<?>, String> sTypeAssociation = new HashMap<Class<?>, String>();
	
	static {
		sTypeAssociation.put(dbString.class, "LONGTEXT");
		sTypeAssociation.put(dbInteger.class, "INTEGER");
		sTypeAssociation.put(dbLong.class, "BIGINT");
		sTypeAssociation.put(dbDouble.class, "DOUBLE");
		sTypeAssociation.put(dbBoolean.class, "BOOLEAN");
		sTypeAssociation.put(dbByte.class, "INTEGER");
		sTypeAssociation.put(dbByteArray.class, "BLOB");
	}
	
	public static String getTableName(Class<?> clazz) {
		return clazz.getCanonicalName().replace(".", "_");
	}
	
	public synchronized void createTablesForModels(Class<?>... models) {
		SQLiteDatabase dbWriter = getWritableDatabase();
		dbWriter.beginTransaction();
		for (Class<?> classOfModel : models) {
			String table = getTableName(classOfModel);
			dbWriter.execSQL(String.format(CREATE_FILES_TABLE_SQL, table));
			List<Field> fields = ReflectUtils.getEntityKeys(classOfModel);
			for (Field field : fields) {
				try {
					String name = field.getName();
					if (name.equals("id")) {
						continue;
					}
					Annotation[] annotations = field.getAnnotations();
					String type = null;
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> classOfAnnotation = annotation.annotationType();
						if (sTypeAssociation.containsKey(classOfAnnotation)) {
							type = sTypeAssociation.get(classOfAnnotation);
						}
					}
					if (type == null) {
						continue;
					}
					dbWriter.execSQL("ALTER TABLE "
							+ table
							+ " ADD "
							+ name
							+ " "
							+ type);				
				} catch (SQLException e) {
					Log.e(TAG, e);
				}
			}	
		}
		dbWriter.setTransactionSuccessful();
		dbWriter.endTransaction();
	}

	public int delete(Class<?> clazz, String where, String[] whereArgs) {
		return delete(getTableName(clazz), where, whereArgs);
	}
	
	public int delete(String tableName, String where, String[] whereArgs) {
		if (isExists(tableName)) {
			SQLiteDatabase db = getWritableDatabase();
			return db.delete(tableName, where, whereArgs);
		} else {
			return 0;
		}
	}

	public boolean isExists(String tableName) {
		SQLiteDatabase readableDb = getReadableDatabase();
		Cursor cursor = readableDb.query("sqlite_master", new String[]{"name"}, "type=? AND name=?", new String[]{"table", tableName}, null, null, null);
		try {
			return cursor != null && cursor.moveToFirst();	
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public int updateOrInsert(Class<?> classOfModel, ContentValues... contentValues) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.beginTransaction();
			IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeArrayUpdate.class);
			for (int i = 0; i < contentValues.length; i++) {
				ContentValues contentValue = contentValues[i];
				if (beforeListUpdate != null) {
					beforeListUpdate.onBeforeListUpdate(i, contentValue);
				}
				updateOrInsert(db, classOfModel, contentValue);
			}
			db.setTransactionSuccessful();
			return contentValues.length;
		} finally {
			db.endTransaction();
		}
	}
	
	
	public long updateOrInsert(SQLiteDatabase db, Class<?> classOfModel, ContentValues contentValues) {
		boolean requestWithoutTransaction = false;
		if (db == null) {
			db = getWritableDatabase();
			requestWithoutTransaction = true;
			db.beginTransaction();
		} 
		try {
			String tableName = getTableName(classOfModel);
			Long id = contentValues.getAsLong(BaseColumns._ID);
			if (id == null) {
				throw new IllegalArgumentException("content values needs to contains _ID");
			}
			IBeforeUpdate beforeUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeUpdate.class);
			if (beforeUpdate != null) {
				beforeUpdate.onBeforeUpdate(contentValues);
			}
			IMerge merge = ReflectUtils.getInstanceInterface(classOfModel, IMerge.class);
			long rowId = 0;
			if (merge == null) {
				int rowCount = db.update(tableName, contentValues, BaseColumns._ID + " = " + id, null);
				if (rowCount == 0) {
					rowId = db.insert(tableName, null, contentValues);
				} else {
					rowId = id;
				}	
			} else {
				Cursor cursor = query(tableName, null, BaseColumns._ID + " = " + id, null, null, null, null, null);
				try {
					if (cursor == null || !cursor.moveToFirst()) {
						rowId = db.insert(tableName, null, contentValues);
					} else {
						ContentValues oldContentValues = new ContentValues();
						DatabaseUtils.cursorRowToContentValues(cursor, oldContentValues);
						merge.merge(oldContentValues, contentValues);
						db.update(tableName, contentValues, BaseColumns._ID + " = " + id, null);
						rowId = id;
					}
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
			}
			if (requestWithoutTransaction) {
				db.setTransactionSuccessful();
			}
			return rowId;
		} finally {
			if (requestWithoutTransaction) {
				db.endTransaction();
			}
		}
	}

	public Cursor query(Class<?> clazz, String[] projection,
			String selection, String[] selectionArgs, String groupBy,
			String having, String sortOrder, String limit) {
		return query(getTableName(clazz), projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
	}
	
	public Cursor query(String tableName, String[] projection,
			String selection, String[] selectionArgs, String groupBy,
			String having, String sortOrder, String limit) {
		if (isExists(tableName)) {
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(tableName);
			SQLiteDatabase db = getReadableDatabase();
			long currentTimeMillis = System.currentTimeMillis();
			Cursor query = qb.query(db, projection, selection, selectionArgs, groupBy,
					having, sortOrder, limit);
			Log.d("db", "request time: " + (System.currentTimeMillis() - currentTimeMillis));
			return query;
		} else {
			return null;
		}
	}

}
