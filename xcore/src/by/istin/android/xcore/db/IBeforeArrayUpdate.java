package by.istin.android.xcore.db;

import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import android.content.ContentValues;

public interface IBeforeArrayUpdate {

	void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues);
	
}
