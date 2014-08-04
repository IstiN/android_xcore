package by.istin.android.xcore.model;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.entity.IGenerateID;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.HashUtils;

public class SuperBigTestEntity implements BaseColumns, IBeforeArrayUpdate, IGenerateID {

	@dbLong
	@SerializedName(value="id")
	public static final String ID = _ID;

	@dbString
	@SerializedName(value="i")
	public static final String ID_AS_STRING = "id_as_string";

	@dbString
	@SerializedName(value="t")
	public static final String TITLE = "title";

	@dbString
	@SerializedName(value="o")
	public static final String O = "o";

	@dbString
	@SerializedName(value="c")
	public static final String C = "c";

	@dbLong
	@SerializedName(value="s")
	public static final String START_TIME = "start_time";

	@dbLong
	@SerializedName(value="e")
	public static final String END_TIME = "end_time";

	@dbBoolean
	@SerializedName(value="a")
	public static final String A = "a";

	@dbBoolean
	@SerializedName(value="r")
	public static final String R = "r";

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {

    }

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return HashUtils.generateId(contentValues.getAsString(ID_AS_STRING));
    }
}