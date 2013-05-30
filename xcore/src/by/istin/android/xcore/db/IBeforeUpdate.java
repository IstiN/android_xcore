package by.istin.android.xcore.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import by.istin.android.xcore.source.DataSourceRequest;

public interface IBeforeUpdate {

	void onBeforeUpdate(DBHelper dbHelper, SQLiteDatabase db, DataSourceRequest dataSourceRequest, ContentValues contentValues);
	
}
