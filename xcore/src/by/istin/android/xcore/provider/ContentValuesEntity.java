package by.istin.android.xcore.provider;

import android.content.ContentValues;
import android.provider.BaseColumns;

public class ContentValuesEntity implements BaseColumns {

	private ContentValues contentValues;

	public ContentValuesEntity() {
		super();
	}

	public ContentValues getContentValues() {
		return contentValues;
	}
	
}