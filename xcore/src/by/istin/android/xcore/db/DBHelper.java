/**
 * 
 */
package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.provider.BaseColumns;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbByteArray;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.BytesUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.ReflectUtils;

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
*/
    }

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
	
	final static Map<Class<?>, String> sTypeAssociation = new HashMap<Class<?>, String>();
	
	static {
		sTypeAssociation.put(dbString.class, "LONGTEXT");
		sTypeAssociation.put(dbInteger.class, "INTEGER");
		sTypeAssociation.put(dbLong.class, "BIGINT");
		sTypeAssociation.put(dbDouble.class, "DOUBLE");
		sTypeAssociation.put(dbBoolean.class, "BOOLEAN");
		sTypeAssociation.put(dbByte.class, "INTEGER");
		sTypeAssociation.put(dbByteArray.class, "BLOB");
	}
	
	private Map<Class<?>, List<Field>> dbEntityFieldsCache = new HashMap<Class<?>, List<Field>>();
	
	private Map<Class<?>, List<Field>> dbEntitiesFieldsCache = new HashMap<Class<?>, List<Field>>();
	
	public static String getTableName(Class<?> clazz) {
		return clazz.getCanonicalName().replace(".", "_");
	}

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase writableDatabase = super.getWritableDatabase();
        if (Build.VERSION.SDK_INT > 7 && Build.VERSION.SDK_INT < 16) {
            writableDatabase.setLockingEnabled(false);
        }
        return writableDatabase;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase readableDatabase = super.getReadableDatabase();
        if (Build.VERSION.SDK_INT > 7 && Build.VERSION.SDK_INT < 16) {
            readableDatabase.setLockingEnabled(false);
        }
        return readableDatabase;
    }

	public synchronized void createTablesForModels(Class<?>... models) {
		SQLiteDatabase dbWriter = getWritableDatabase();
        if (Build.VERSION.SDK_INT > 10) {
            dbWriter.enableWriteAheadLogging();
        }
        beginTransaction(dbWriter);
        for (Class<?> classOfModel : models) {
			String table = getTableName(classOfModel);
			dbWriter.execSQL(String.format(CREATE_FILES_TABLE_SQL, table));
			List<Field> fields = ReflectUtils.getEntityKeys(classOfModel);
			for (Field field : fields) {
				try {
					String name = ReflectUtils.getStaticStringValue(field);
					if (name.equals(BaseColumns._ID)) {
						continue;
					}
					Annotation[] annotations = field.getAnnotations();
					String type = null;
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> classOfAnnotation = annotation.annotationType();
						if (sTypeAssociation.containsKey(classOfAnnotation)) {
							type = sTypeAssociation.get(classOfAnnotation);
						} else if (classOfAnnotation.equals(dbEntity.class)) {
							List<Field> list = dbEntityFieldsCache.get(classOfModel);
							if (list == null) {
								list = new ArrayList<Field>();
							}
							list.add(field);
							dbEntityFieldsCache.put(classOfModel, list);
						} else if (classOfAnnotation.equals(dbEntities.class)) {
							List<Field> list = dbEntitiesFieldsCache.get(classOfModel);
							if (list == null) {
								list = new ArrayList<Field>();
							}
							list.add(field);
							dbEntitiesFieldsCache.put(classOfModel, list);
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
        setTransactionSuccessful(dbWriter);
        endTransaction(dbWriter);
    }

    private void endTransaction(SQLiteDatabase dbWriter) {
        dbWriter.endTransaction();
    }

    private void setTransactionSuccessful(SQLiteDatabase dbWriter) {
        dbWriter.setTransactionSuccessful();
    }

    private void beginTransaction(SQLiteDatabase dbWriter) {
        if (Build.VERSION.SDK_INT > 10) {
            dbWriter.beginTransactionNonExclusive();
        } else {
            dbWriter.beginTransaction();
        }
    }

    public int delete(Class<?> clazz, String where, String[] whereArgs) {
		return delete(null, getTableName(clazz), where, whereArgs);
	}
	
	public int delete(SQLiteDatabase db, Class<?> clazz, String where, String[] whereArgs) {
		return delete(db, getTableName(clazz), where, whereArgs);
	}
	
	public int delete(String tableName, String where, String[] whereArgs) {
		return delete(null, tableName, where, whereArgs);
	}
	
	public int delete(SQLiteDatabase db, String tableName, String where, String[] whereArgs) {
		if (isExists(tableName)) {
			if (db == null) {
				db = getWritableDatabase();
			}
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
		return updateOrInsert(null, false, classOfModel, contentValues);
	}
	
	public int updateOrInsert(DataSourceRequest dataSourceRequest, boolean withCleaner, Class<?> classOfModel, ContentValues... contentValues) {
        if (contentValues == null) {
            return 0;
        }
		SQLiteDatabase db = getWritableDatabase();
		try {
            if (!isLockTransaction) {
                beginTransaction(db);
            }
			if (withCleaner) {
				ICleaner cleaner = ReflectUtils.getInstanceInterface(classOfModel, ICleaner.class);
				if (cleaner != null) {
					cleaner.clean(this, db, dataSourceRequest, contentValues);
				}
			}
			IBeforeArrayUpdate beforeListUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeArrayUpdate.class);
			int count = 0;
			for (int i = 0; i < contentValues.length; i++) {
				ContentValues contentValue = contentValues[i];
				if (contentValue == null) {
					continue;
				}
				if (beforeListUpdate != null) {
					beforeListUpdate.onBeforeListUpdate(this, db, dataSourceRequest, i, contentValue);
				}
				long id = updateOrInsert(dataSourceRequest, db, classOfModel, contentValue);
				if (id != -1l) {
					count++;
				}
			}
            if (!isLockTransaction) {
                setTransactionSuccessful(db);
            }
			return count;
		} finally {
            if (!isLockTransaction) {
                endTransaction(db);
            }
		}
	}
	
	public long updateOrInsert(SQLiteDatabase db, Class<?> classOfModel, ContentValues contentValues) {
		return updateOrInsert(null, db, classOfModel, contentValues);
	}
	
	public long updateOrInsert(DataSourceRequest dataSourceRequest, SQLiteDatabase db, Class<?> classOfModel, ContentValues contentValues) {
		boolean requestWithoutTransaction = false;
		if (db == null) {
			db = getWritableDatabase();
			requestWithoutTransaction = true;
            if (!isLockTransaction) {
                beginTransaction(db);
            }
			ICleaner cleaner = ReflectUtils.getInstanceInterface(classOfModel, ICleaner.class);
			if (cleaner != null) {
				cleaner.clean(this, db, dataSourceRequest, contentValues);
			}
		} 
		try {
			IBeforeUpdate beforeUpdate = ReflectUtils.getInstanceInterface(classOfModel, IBeforeUpdate.class);
			if (beforeUpdate != null) {
				beforeUpdate.onBeforeUpdate(this, db, dataSourceRequest, contentValues);
			}
			Long id = contentValues.getAsLong(BaseColumns._ID);
			if (id == null) {
				throw new IllegalArgumentException("content values needs to contains _ID");
			}
			List<Field> listDbEntity = dbEntityFieldsCache.get(classOfModel);
			if (listDbEntity != null) {
				storeSubEntity(dataSourceRequest, id, classOfModel, db, contentValues, dbEntity.class, listDbEntity);
			}
			List<Field> listDbEntities = dbEntitiesFieldsCache.get(classOfModel);
			if (listDbEntities != null) {
				storeSubEntity(dataSourceRequest, id, classOfModel, db, contentValues, dbEntities.class, listDbEntities);
			}
			String tableName = getTableName(classOfModel);
			IMerge merge = ReflectUtils.getInstanceInterface(classOfModel, IMerge.class);
			long rowId = 0;
			if (merge == null) {
				int rowCount = db.update(tableName, contentValues, BaseColumns._ID + " = " + id, null);
				if (rowCount == 0) {
					rowId = db.insert(tableName, null, contentValues);
					if (rowId == -1l) {
						throw new IllegalArgumentException("can not insert content values:" + contentValues.toString() + " to table " + classOfModel+". Check keys in contentvalues and fields in model.");
					}
				} else {
					rowId = id;
				}	
			} else {
				Cursor cursor = query(tableName, null, BaseColumns._ID + " = " + id, null, null, null, null, null);
				try {
					if (cursor == null || !cursor.moveToFirst()) {
						rowId = db.insert(tableName, null, contentValues);
						if (rowId == -1l) {
							throw new IllegalArgumentException("can not insert content values:" + contentValues.toString() + " to table " + classOfModel+". Check keys in contentvalues and fields in model.");
						}
					} else {
						ContentValues oldContentValues = new ContentValues();
						DatabaseUtils.cursorRowToContentValues(cursor, oldContentValues);
						merge.merge(this, db, dataSourceRequest, oldContentValues, contentValues);
						if (!isContentValuesEquals(oldContentValues, contentValues)) {
							db.update(tableName, contentValues, BaseColumns._ID + " = " + id, null);
							rowId = id;
						} else {
							rowId = -1l;
						}
					}
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
			}
			if (requestWithoutTransaction) {
                if (!isLockTransaction) {
                    setTransactionSuccessful(db);
                }
			}
			return rowId;
		} finally {
			if (requestWithoutTransaction) {
                if (!isLockTransaction) {
                    endTransaction(db);
                }
			}
		}
	}

	public static boolean isContentValuesEquals(ContentValues oldContentValues, ContentValues contentValues) {
		Set<Entry<String, Object>> keySet = contentValues.valueSet();
		for (Iterator<Entry<String, Object>> iterator = keySet.iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			Object newObject = entry.getValue();
			Object oldObject = oldContentValues.get(entry.getKey());
			if (newObject == null && oldObject == null) {
				continue;
			}
			if (newObject != null && newObject.equals(oldObject)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	private void storeSubEntity(DataSourceRequest dataSourceRequest, long id, Class<?> foreignEntity, SQLiteDatabase db, ContentValues contentValues, Class<? extends Annotation> dbAnnotation, List<Field> listDbEntity) {
		for (Field field : listDbEntity) {
			String columnName = ReflectUtils.getStaticStringValue(field);
			byte[] entityAsByteArray = contentValues.getAsByteArray(columnName);
			if (entityAsByteArray == null) {
				continue;
			}
			Annotation annotation = field.getAnnotation(dbAnnotation);
			String contentValuesKey;
			String foreignId = foreignEntity.getSimpleName().toLowerCase()+"_id";
			try {
				contentValuesKey = (String) annotation.annotationType().getMethod("contentValuesKey").invoke(annotation);
			} catch (Exception e) {
				throw new IllegalArgumentException(e); 
			}
			String className = contentValues.getAsString(contentValuesKey);
			Class<?> modelClass;
			try {
				modelClass = Class.forName(className);
			} catch (ClassNotFoundException e1) {
				throw new IllegalArgumentException(e1);
			}
			if (annotation.annotationType().equals(dbEntity.class)) {
				ContentValues entityValues = BytesUtils.contentValuesFromByteArray(entityAsByteArray);
				putEntity(dataSourceRequest, id, db, contentValuesKey, foreignId, modelClass, entityValues);
			} else {
				ContentValues[] entitiesValues = BytesUtils.arrayContentValuesFromByteArray(entityAsByteArray);
				for (ContentValues cv : entitiesValues) {
					putEntity(dataSourceRequest, id, db, contentValuesKey, foreignId, modelClass, cv);
				}
			}
			contentValues.remove(columnName);
			contentValues.remove(contentValuesKey);
			if (dbAnnotation.equals(dbEntities.class)) {
				if (((dbEntities)annotation).isNotify()) {
					ContextHolder.getInstance().getContext().getContentResolver().notifyChange(ModelContract.getUri(modelClass), null);
				}
			}
		}
	}

	private void putEntity(DataSourceRequest dataSourceRequest, long id, SQLiteDatabase db, String contentValuesKey, String foreignId, Class<?> modelClass, ContentValues entityValues) {
		entityValues.remove(contentValuesKey);
		entityValues.put(foreignId, id);
		updateOrInsert(dataSourceRequest, db, modelClass, entityValues);
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
			return query;
		} else {
			return null;
		}
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}

    public static void moveFromOldValues(ContentValues oldValues, ContentValues newValues, String ... keys) {
        for (String key : keys) {
            Object value = oldValues.get(key);
            if (value != null && newValues.get(key) == null) {
                if (value instanceof Long) {
                    newValues.put(key, (Long)value);
                } else if (value instanceof Integer) {
                    newValues.put(key, (Integer)value);
                } else if (value instanceof String) {
                    newValues.put(key, (String)value);
                } else if (value instanceof Byte) {
                    newValues.put(key, (Byte)value);
                } else if (value instanceof byte[]) {
                    newValues.put(key, (byte[])value);
                } else if (value instanceof Boolean) {
                    newValues.put(key, (Boolean)value);
                } else if (value instanceof Double) {
                    newValues.put(key, (Double)value);
                } else if (value instanceof Float) {
                    newValues.put(key, (Float)value);
                } else if (value instanceof Short) {
                    newValues.put(key, (Short)value);
                }
            }
        }
    }

    private volatile Boolean isLockTransaction = false;

    public void lockTransaction() {
        synchronized (isLockTransaction) {
            isLockTransaction = true;
            SQLiteDatabase writableDatabase = getWritableDatabase();
            beginTransaction(writableDatabase);
        }
    }

    public void unlockTransaction() {
        synchronized (isLockTransaction) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            setTransactionSuccessful(writableDatabase);
            endTransaction(writableDatabase);
            isLockTransaction = false;
        }
    }

    public void errorUnlockTransaction() {
        synchronized (isLockTransaction) {
            endTransaction(getWritableDatabase());
            isLockTransaction =  false;
        }
    }
}