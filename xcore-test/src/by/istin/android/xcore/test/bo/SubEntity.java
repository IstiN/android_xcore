package by.istin.android.xcore.test.bo;

import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.provider.ContentValuesEntity;

public class SubEntity extends ContentValuesEntity {

	@dbLong
	public static String ID = _ID;
	
	@dbString
	public static String STRING_VALUE = "string_value";
	
}