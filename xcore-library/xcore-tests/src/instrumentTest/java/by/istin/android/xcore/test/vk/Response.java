package by.istin.android.xcore.test.vk;

import android.content.ContentValues;
import android.provider.BaseColumns;

import by.istin.android.xcore.annotations.JsonSubJSONObject;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;

public class Response implements BaseColumns, IGenerateID {

	@dbLong
	public static final String ID = _ID;

	@dbEntities(clazz = User.class, contentValuesKey = "users")
    @JsonSubJSONObject
	public static final String USERS = "users";

	@dbEntities(clazz = Dialog.class, contentValuesKey = "dialogs")
    @JsonSubJSONObject
	public static final String DIALOGS = "dialogs";

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return 1l;
    }
}