package by.istin.android.xcore.db;

import android.content.ContentValues;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;

public interface IBeforeUpdate {

	void onBeforeUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues);
	
}
