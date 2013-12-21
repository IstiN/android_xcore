package by.istin.android.xcore.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

/**
 * Created by IstiN on 8.12.13.
 */
public class TagEntity implements BaseColumns, IGenerateID {

    @dbLong
    public static final String ID = _ID;

    @dbString
    public static final String VALUE = "value";

    @dbLong
    public static final String SIMPLE_ENTITY_PARENT = DBHelper.getForeignKey(SimpleEntityWithPrimitiveEntity.class);


    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        //generate unique ID for every association tag with entity, because tags can be repeated
        return HashUtils.generateId(contentValues.getAsString(VALUE), contentValues.getAsLong(SIMPLE_ENTITY_PARENT));
    }
}
