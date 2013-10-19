package by.istin.android.xcore.db;

import by.istin.android.xcore.source.DataSourceRequest;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public interface IBeforeArrayUpdate {

	void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues);
	
}
