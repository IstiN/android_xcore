package by.istin.android.xcore.test.vk;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IBeforeArrayUpdate;
import by.istin.android.xcore.db.IBeforeUpdate;
import by.istin.android.xcore.utils.HashUtils;

public class FwdMessage implements BaseColumns, IBeforeArrayUpdate, IBeforeUpdate {

	@dbLong
	public static final String ID = _ID;
	
	@dbLong
	public static final String UID = "uid";
	
	@dbLong
	public static final String DATE = "date";
	
	@dbString
	public static final String BODY = "body";

	@dbLong
	public static final String DIALOG_ID = "dialog_id";
	
	@Override
	public void onBeforeListUpdate(int position, ContentValues contentValues) {
		
	}

	@Override
	public void onBeforeUpdate(ContentValues contentValues) {
		String value = contentValues.getAsLong(UID) + contentValues.getAsString(BODY) + contentValues.getAsLong(DATE);
		contentValues.put(ID, HashUtils.generateId(value));		
	}
	
}