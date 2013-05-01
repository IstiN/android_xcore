package by.istin.android.xcore.test.vk;

import android.content.ContentValues;
import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbByte;
import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbEntity;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IBeforeArrayUpdate;
import by.istin.android.xcore.db.IMerge;

public class Dialog implements BaseColumns, IMerge, IBeforeArrayUpdate {

	@dbLong
	public static final String ID = _ID;
	
	@dbLong
	public static final String MID = "mid";
	
	@dbLong
	public static final String UID = "uid";
	
	@dbLong
	public static final String ADMIN_ID = "admin_id";
	
	@dbLong
	public static final String CHAT_ID = "chat_id";
	
	@dbLong
	public static final String DATE = "date";
	
	@dbByte
	public static final String OUT = "out";
	
	@dbByte
	public static final String READ_STATE = "read_state";
	
	@dbByte
	public static final String EMOJI = "emoji";
	
	@dbString
	public static final String TITLE = "title";
	
	@dbString
	public static final String BODY = "body";
	
	@dbString
	public static final String CHAT_ACTIVE = "chat_active";
	
	@dbInteger
	public static final String USER_COUNT = "user_count";
	
	@dbEntity(clazz = Attachment.class)
	public static final String ATTACHMENT = "attachment";
	
	@dbEntities(clazz = Attachment.class)
	public static final String ATTACHMENTS = "attachments";
	
	@dbEntities(clazz = FwdMessage.class)
	public static final String FWD_MESSAGES = "fwd_messages";
	
	//local fields
	@dbString
	public static final String FULL_NAME = "full_name";
	
	@dbString
	public static final String SEARCH_VALUE = "search_value";
	
	@dbInteger
	public static final String POSITION = "position";
	
	@Override
	public void merge(ContentValues oldValues, ContentValues newValues) {
		if (newValues.getAsInteger(POSITION) == null) {
			newValues.put(POSITION, oldValues.getAsInteger(POSITION));
		}
	}

	@Override
	public void onBeforeListUpdate(int position, ContentValues contentValues) {
		contentValues.put(POSITION, position);
	}
	
}