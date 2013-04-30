package by.istin.android.xcore.test.bo;

import android.provider.BaseColumns;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;

public class SubEntity implements BaseColumns {

	@dbLong
	public static String ID = _ID;
	
	@dbString
	public static String STRING_VALUE = "string_value";
	
}