package by.istin.android.xcore.db;

import android.content.ContentValues;

public interface IBeforeUpdate {

	void onBeforeUpdate(ContentValues contentValues);
	
}
