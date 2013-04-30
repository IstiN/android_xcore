package by.istin.android.xcore.test.db;

import java.io.InputStream;

import android.app.Application;
import android.content.ContentValues;
import android.test.ApplicationTestCase;
import android.util.Log;
import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.test.bo.TestEntity;

public class TestDbHelper extends ApplicationTestCase<Application> {

	public TestDbHelper() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
	}

	public void testArrayLoad() throws Exception {
		HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://dl.dropboxusercontent.com/u/16403954/xcore/json_array.json");
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);
		ContentValues[] contentValues = new GsonArrayContentValuesProcessor(TestEntity.class).execute(dataSourceRequest, httpAndroidDataSource, inputStream);
		//TODO put to intent insert uri
		DBHelper dbHelper = new DBHelper(getApplication());
		dbHelper.createTablesForModels(TestEntity.class);
		dbHelper.updateOrInsert(TestEntity.class, contentValues);
		inputStream.close();
		Log.d("TestHttpDataSource", contentValues.toString());
	}

}
