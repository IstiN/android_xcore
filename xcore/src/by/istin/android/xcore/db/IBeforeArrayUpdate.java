package by.istin.android.xcore.db;

import android.content.ContentValues;

public interface IBeforeArrayUpdate {

	void onBeforeListUpdate(int position, ContentValues contentValues);
	
}
