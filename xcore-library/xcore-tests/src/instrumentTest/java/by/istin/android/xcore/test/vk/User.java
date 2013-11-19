package by.istin.android.xcore.test.vk;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.db.entity.IBeforeArrayUpdate;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.entity.IMerge;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;
import com.google.gson.annotations.SerializedName;

public class User implements BaseColumns, IMerge, IBeforeArrayUpdate {

	@dbLong
	@SerializedName(value="uid")
	public static final String ID = _ID;
	
	@dbString
	public static final String FIRST_NAME = "first_name";
	
	@dbString
	public static final String LAST_NAME = "last_name";
	
	@dbString
	public static final String PHOTO = "photo";
	
	@dbString
	public static final String PHOTO_MEDIUM = "photo_medium";
	
	@dbString
	public static final String PHOTO_50 = "photo_50";
	
	@dbString
	public static final String PHOTO_100 = "photo_100";
	
	@dbString
	public static final String PHOTO_BIG = "photo_big";

	@dbByte
	public static final String ONLINE_MOBILE = "online_mobile";
	
	@dbString
	public static final String ONLINE_APP = "online_app";
	
	@dbByte
	public static final String ONLINE = "online";
	
	@dbByte
	public static final String SEX = "sex";
	
	@dbLong
	@SerializedName(value="last_seen:time")
	public static final String LAST_SEEN_TIME = "last_seen_time";
	
	//local fields
	@dbString
	public static final String FULL_NAME = "full_name";
	
	@dbString
	public static final String SEARCH_VALUE = "search_value";
	
	@dbInteger
	public static final String POSITION = "position";

    @dbLong
    public static final String RESPONSE_ID = "response_id";

	@Override
	public void merge(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues oldValues, ContentValues newValues) {
		if (newValues.getAsInteger(POSITION) == null) {
			newValues.put(POSITION, oldValues.getAsInteger(POSITION));
		}
	}

	@Override
	public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
		String fullName = contentValues.getAsString(FIRST_NAME) + " " + contentValues.getAsString(LAST_NAME);
		contentValues.put(FULL_NAME, fullName);
		contentValues.put(SEARCH_VALUE, StringUtil.translit(fullName));
		contentValues.put(POSITION, position);
	}
	
}