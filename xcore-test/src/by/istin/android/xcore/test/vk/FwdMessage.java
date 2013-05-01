package by.istin.android.xcore.test.vk;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IBeforeArrayUpdate;

public class FwdMessage implements BaseColumns, IBeforeArrayUpdate {

	@dbLong
	public static final String ID = _ID;
	
	@dbLong
	public static final String UID = "uid";
	
	@dbLong
	public static final String DATE = "date";
	
	@dbString
	public static final String BODY = "body";

	@Override
	public void onBeforeListUpdate(int position, ContentValues contentValues) {
		String value = contentValues.getAsLong(UID) + contentValues.getAsString(BODY) + contentValues.getAsLong(DATE);
		contentValues.put(ID, value);
	}
	
}