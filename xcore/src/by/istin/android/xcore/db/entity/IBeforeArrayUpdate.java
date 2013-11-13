package by.istin.android.xcore.db.entity;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBSupport;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import android.content.ContentValues;

public interface IBeforeArrayUpdate {

	void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues);
	
}
