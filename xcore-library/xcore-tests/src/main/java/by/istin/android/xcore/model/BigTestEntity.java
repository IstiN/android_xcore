package by.istin.android.xcore.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbDouble;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IMerge;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

public class BigTestEntity implements BaseColumns, IMerge, IBeforeArrayUpdate {

	@dbLong
	@SerializedName(value="id")
	public static final String ID = _ID;
	
	@dbInteger
	public static final String INT_VALUE = "INT_VALUE";
	
	@dbByte
	public static final String BYTE_VALUE = "BYTE_VALUE";
	
	@dbDouble
	public static final String DOUBLE_VALUE = "DOUBLE_VALUE";
	
	@dbString
	public static final String STRING_VALUE = "STRING_VALUE";
	
	@dbBoolean
	public static final String BOOLEAN_VALUE = "BOOLEAN_VALUE";
	
	@dbEntity(clazz=BigTestSubEntity.class, contentValuesKey = "subEntityValue")
	public static final String SUB_ENTITY_VALUE = "sub_entity_value";

	@dbEntities(clazz=BigTestSubEntity.class, contentValuesKey = "subEntityValues")
	public static final String SUB_ENTITY_VALUES = "sub_entity_values";

	@Override
	public void merge(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues oldValues, ContentValues newValues) {
		// test interface
    }

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        contentValues.put(ID, HashUtils.generateId(contentValues.getAsLong(ID), position));
    }
}