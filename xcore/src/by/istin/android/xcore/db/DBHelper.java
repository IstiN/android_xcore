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
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbIndex;
import by.istin.android.xcore.db.impl.SQLiteConnector;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Uladzimir_Klyshevich
 *
 */
public class DBHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    private IDBConnector mDbConnector;

    public DBHelper(Context context) {
        super();
        mDbConnector = new SQLiteConnector(context);
        dbAssociationCache = DBAssociationCache.get();
	}

    private DBAssociationCache dbAssociationCache;

	public static String getTableName(Class<?> clazz) {
        DBAssociationCache associationCache = DBAssociationCache.get();
        String tableName = associationCache.getTableName(clazz);
        if (tableName == null) {
            tableName = clazz.getCanonicalName().replace(".", "_");
            associationCache.setTableName(clazz, tableName);
        }
        return tableName;
	}


	public synchronized void createTablesForModels(Class<?>... models) {
		IDBConnection dbWriter = mDbConnector.getWritableConnection();
        dbWriter.beginTransaction(dbWriter);
        StringBuilder builder = new StringBuilder();
        List<String> foreignKeys = new ArrayList<String>();
        for (Class<?> classOfModel : models) {
			String table = getTableName(classOfModel);
            dbAssociationCache.setTableCreated(table, null);
			dbWriter.execSQL(mDbConnector.getCreateFilesTableSQLTemplate(table));
			List<Field> fields = ReflectUtils.getEntityKeys(classOfModel);
			for (Field field : fields) {
				try {
					String name = ReflectUtils.getStaticStringValue(field);
					if (name.equals(BaseColumns._ID)) {
						continue;
					}
					Annotation[] annotations = field.getAnnotations();
					String type = null;
                    boolean isForeign = false;
					for (Annotation annotation : annotations) {
						Class<? extends Annotation> classOfAnnotation = annotation.annotationType();
						if (DBAssociationCache.TYPE_ASSOCIATION.containsKey(classOfAnnotation)) {
							type = DBAssociationCache.TYPE_ASSOCIATION.get(classOfAnnotation);
						} else if (classOfAnnotation.equals(dbEntity.class)) {
							List<Field> list = dbAssociationCache.getEntityFields(classOfModel);
							if (list == null) {
								list = new ArrayList<Field>();
							}
							list.add(field);
                            dbAssociationCache.putEntityFields(classOfModel, list);
                            addForeignKey(foreignKeys, classOfModel, annotation);
                            isForeign = true;
						} else if (classOfAnnotation.equals(dbEntities.class)) {
							List<Field> list = dbAssociationCache.getEntitiesFields(classOfModel);
							if (list == null) {
								list = new ArrayList<Field>();
							}
							list.add(field);
                            addForeignKey(foreignKeys, classOfModel, annotation);
                            dbAssociationCache.putEntitiesFields(classOfModel, list);
                            isForeign = true;
						} else if (classOfAnnotation.equals(dbIndex.class)) {
                            builder.append("CREATE INDEX "
                                    + "fk_" + table + "_" + name
                                    + " ON "
                                    + table
                                    + " "
                                    + "(" + name + " ASC);");
                        }
					}
					if (type == null) {
						continue;
					}
                    if (!isForeign) {
					    dbWriter.execSQL("ALTER TABLE "
							+ table
							+ " ADD "
							+ name
							+ " "
							+ type);
                    }
				} catch (SQLException e) {
					Log.e(TAG, e);
				}
			}
            String sql = builder.toString();
            Log.xd(this, sql);
            if (!StringUtil.isEmpty(sql)) {
                try {
                    dbWriter.execSQL(sql);
                } catch (SQLException e) {
                    Log.e(TAG, e);
                }
            }
            builder.setLength(0);
		}
        for (String foreignKey : foreignKeys) {
            try {
                //TODO needs solution for foreign keys dbWriter.execSQL(foreignKey);
            } catch (SQLException e) {
                Log.e(TAG, e);
            }
        }
        setTransactionSuccessful(dbWriter);
        endTransaction(dbWriter);
    }

    private void addForeignKey(List<String> foreignKeys, Class<?> classOfModel, Annotation annotation) {
        /*TODO Class<?> childClazz;
        if (annotation instanceof dbEntities) {
            childClazz = ((dbEntities) annotation).clazz();
        } else {
            childClazz = ((dbEntity) annotation).clazz();
        }
        String tableName = DBHelper.getTableName(childClazz);
        String foreignKey = StringUtil.format(FOREIGN_KEY_TEMPLATE, tableName, DBHelper.getTableName(classOfModel), classOfModel.getSimpleName().toLowerCase());
        foreignKeys.add(foreignKey);*/
    }

    public int delete(Class<?> clazz, String where, String[] whereArgs) {
		return delete(null, getTableName(clazz), where, whereArgs);
	}
	
	public int delete(IDBConnection db, Class<?> clazz, String where, String[] whereArgs) {
		return delete(db, getTableName(clazz), where, whereArgs);
	}
	
	public int delete(String tableName, String where, String[] whereArgs) {
		return delete(null, tableName, where, whereArgs);
	}
	
	public int delete(IDBConnection db, String tableName, String where, String[] whereArgs) {
		if (isExists(tableName)) {
			if (db == null) {
				db = mDbConnector.getWritableConnection();
			}
            return db.delete(tableName, where, whereArgs);
		} else {
			return 0;
		}
	}

	public boolean isExists(String tableName) {
        Boolean isTableCreated = dbAssociationCache.isTableCreated(tableName);
        if (isTableCreated != null) {
            return isTableCreated;
        }
        IDBConnection readableDb = mDbConnector.getReadableConnection();
        boolean isExists = false;
        isExists = readableDb.isExists(tableName);
        dbAssociationCache.setTableCreated(tableName, isExists);
        return isExists;

	}
	
	public int updateOrInsert(Class<?> classOfModel, ContentValues... contentValues) {
		return updateOrInsert(null, classOfModel, contentValues);
	}
	
	public int updateOrInsert(DataSourceRequest dataSourceRequest, Class<?> classOfModel, ContentValues... contentValues) {
        if (contentValues == null) {
            return 0;
        }
		IDBConnection db = mDbConnector.getWritableConnection();
		try {
            beginTransaction(db);
            int count = updateOrInsert(dataSourceRequest, classOfModel, db, contentValues);
            setTransactionSuccessful(db);
			return count;
		} finally {
            endTransaction(db);
		}
	}

    private int updateOrInsert(DataSourceRequest dataSourceRequest, Class<?> classOfModel, SQLiteDatabase db, ContentValues[] contentValues) {
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
        return count;
    }

    public long updateOrInsert(SQLiteDatabase db, Class<?> classOfModel, ContentValues contentValues) {
		return updateOrInsert(null, db, classOfModel, contentValues);
	}

	public long updateOrInsert(DataSourceRequest dataSourceRequest, SQLiteDatabase db, Class<?> classOfModel, ContentValues contentValues) {
		boolean requestWithoutTransaction = false;
		if (db == null) {
			db = getWritableDatabase();
			requestWithoutTransaction = true;
            beginTransaction(db);
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
			List<Field> listDbEntity = dbAssociationCache.getEntityFields(classOfModel);
			if (listDbEntity != null) {
				storeSubEntity(dataSourceRequest, id, classOfModel, db, contentValues, dbEntity.class, listDbEntity);
			}
			List<Field> listDbEntities = dbAssociationCache.getEntitiesFields(classOfModel);
			if (listDbEntities != null) {
				storeSubEntity(dataSourceRequest, id, classOfModel, db, contentValues, dbEntities.class, listDbEntities);
			}
			String tableName = getTableName(classOfModel);
			IMerge merge = ReflectUtils.getInstanceInterface(classOfModel, IMerge.class);
			long rowId = 0;
			if (merge == null) {
				int rowCount = db.update(tableName, contentValues, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)});
				if (rowCount == 0) {
					rowId = internalInsert(db, classOfModel, contentValues, tableName);
					if (rowId == -1l) {
						throw new IllegalArgumentException("can not insert content values:" + contentValues.toString() + " to table " + classOfModel+". Check keys in contentvalues and fields in model.");
					}
				} else {
					rowId = id;
				}
			} else {
                Cursor cursor = null;
                try {
				    cursor = query(tableName, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, null);
					if (cursor == null || !cursor.moveToFirst()) {
						rowId = internalInsert(db, classOfModel, contentValues, tableName);
						if (rowId == -1l) {
							throw new IllegalArgumentException("can not insert content values:" + contentValues.toString() + " to table " + classOfModel+". Check keys in contentvalues and fields in model.");
						}
					} else {
						ContentValues oldContentValues = new ContentValues();
						DatabaseUtils.cursorRowToContentValues(cursor, oldContentValues);
						merge.merge(this, db, dataSourceRequest, oldContentValues, contentValues);
						if (!isContentValuesEquals(oldContentValues, contentValues)) {
							internalUpdate(db, classOfModel, contentValues, id, tableName);
							rowId = id;
						} else {
							rowId = -1l;
						}
					}
				} finally {
                    CursorUtils.close(cursor);
				}
			}
			if (requestWithoutTransaction) {
                setTransactionSuccessful(db);
			}
			return rowId;
		} finally {
			if (requestWithoutTransaction) {
                endTransaction(db);
			}
		}
	}

    private int internalUpdate(SQLiteDatabase db, Class<?> clazz, ContentValues contentValues, Long id, String tableName) {
        return db.update(tableName, contentValues, BaseColumns._ID + " = " + id, null);
    }

    public void endTransaction(IDBConnection dbWriter) {
        dbWriter.endTransaction();
    }

    public void setTransactionSuccessful(IDBConnection dbWriter) {
        dbWriter.setTransactionSuccessful();
    }


    public void beginTransaction(IDBConnection dbWriter) {
        dbWriter.beginTransaction(dbWriter);
    }

    private long internalInsert(SQLiteDatabase db, Class<?> clazz, ContentValues contentValues, String tableName) {
        //TODO needs some parameter to configure strategy insertWithStatement(db, clazz, contentValues, tableName);
        //return 0;
        return db.insert(tableName, null, contentValues);
    }

    private void insertWithStatement(SQLiteDatabase db, Class<?> clazz, ContentValues contentValues, String tableName) {
        SQLiteStatement insertStatement = dbAssociationCache.getInsertStatement(clazz);
        if (insertStatement == null) {
            List<Field> fields = ReflectUtils.getEntityKeys(clazz);
            List<String> columns = new ArrayList<String>();
            for (Field field : fields) {
                if (field.isAnnotationPresent(dbEntity.class)) {
                    continue;
                }
                if (field.isAnnotationPresent(dbEntities.class)) {
                    continue;
                }
                String name = ReflectUtils.getStaticStringValue(field);
                columns.add(name);
            }
            insertStatement = db.compileStatement(createInsert(tableName, columns.toArray(new String[columns.size()])));
            dbAssociationCache.setInsertStatement(clazz, insertStatement);
        }
        insertStatement.clearBindings();
        Set<String> keys = contentValues.keySet();
        String[] values = new String[keys.size()];
        int i = 0;
        for (String key : keys) {
            values[i] = contentValues.getAsString(key);
            i++;
        }
        insertStatement.bindAllArgsAsStrings(values);
        insertStatement.execute();
    }

    static public String createInsert(final String tableName, final String[] columnNames) {
        if (tableName == null || columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException();
        }
        final StringBuilder s = new StringBuilder();
        s.append("INSERT OR REPLACE INTO ").append(tableName).append(" (");
        for (String column : columnNames) {
            s.append(column).append(" ,");
        }
        int length = s.length();
        s.delete(length - 2, length);
        s.append(") VALUES( ");
        for (int i = 0; i < columnNames.length; i++) {
            s.append(" ? ,");
        }
        length = s.length();
        s.delete(length - 2, length);
        s.append(")");
        return s.toString();
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
                putForeignIdAndClear(id, contentValuesKey, foreignId, entityValues);
                updateOrInsert(dataSourceRequest, db, modelClass, entityValues);
			} else {
				ContentValues[] entitiesValues = BytesUtils.arrayContentValuesFromByteArray(entityAsByteArray);
                for (ContentValues cv : entitiesValues) {
                    putForeignIdAndClear(id, contentValuesKey, foreignId, cv);
                }
                updateOrInsert(dataSourceRequest, modelClass, db, entitiesValues);
			}
			contentValues.remove(columnName);
			contentValues.remove(contentValuesKey);
		}
	}

	private void putForeignIdAndClear(long id, String contentValuesKey, String foreignId, ContentValues entityValues) {
		entityValues.remove(contentValuesKey);
		entityValues.put(foreignId, id);
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
            return qb.query(db, projection, selection, selectionArgs, groupBy,
                    having, sortOrder, limit);
		} else {
			return null;
		}
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, selectionArgs);
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

}