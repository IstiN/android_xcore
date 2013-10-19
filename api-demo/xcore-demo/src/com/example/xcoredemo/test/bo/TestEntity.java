package com.example.xcoredemo.test.bo;

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
	public static final String INT_VALUE = "INT_VALUE";

	@dbByte
	public static final String BYTE_VALUE = "BYTE_VALUE";

	@dbDouble
	public static final String DOUBLE_VALUE = "DOUBLE_VALUE";

	@dbString
	public static final String STRING_VALUE = "STRING_VALUE";

	@dbBoolean
	public static final String BOOLEAN_VALUE = "BOOLEAN_VALUE";

	@dbEntity(clazz = SubEntity.class)
	public static final String SUB_ENTITY_VALUE = "sub_entity_value";

	@Override
	public void merge(DBHelper dbHelper, IDBConnection db,
			DataSourceRequest dataSourceRequest, ContentValues oldValues,
			ContentValues newValues) {
		// test interface
	}

}
