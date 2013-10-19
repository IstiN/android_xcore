package by.istin.android.xcore.test.bo;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.*;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IMerge;
import by.istin.android.xcore.source.DataSourceRequest;
import com.google.gson.annotations.SerializedName;

public class TestEntity implements BaseColumns, IMerge {

	@dbLong
	@SerializedName(value="id")
	public static final String ID = _ID;
	
	@dbInteger
	public static final String INT_VALUE = "int_value";
	
	@dbByte
	public static final String BYTE_VALUE = "byte_value";
	
	@dbDouble
	public static final String DOUBLE_VALUE = "double_value";
	
	@dbString
	public static final String STRING_VALUE = "string_value";
	
	@dbBoolean
	public static final String BOOLEAN_VALUE = "boolean_value";
	
	@dbEntity(clazz=SubEntity.class)
	public static final String SUB_ENTITY_VALUE = "sub_entity_value";

	@Override
	public void merge(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues oldValues, ContentValues newValues) {
		// test interface
	}

}