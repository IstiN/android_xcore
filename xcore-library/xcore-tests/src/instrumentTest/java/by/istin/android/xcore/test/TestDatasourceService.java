package by.istin.android.xcore.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.db.impl.sqlite.SQLiteSupport;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.test.bo.DialogsResponse;
import by.istin.android.xcore.test.bo.FriendsResponse;
import by.istin.android.xcore.test.vk.Dialog;
import by.istin.android.xcore.test.vk.Response;
import by.istin.android.xcore.test.vk.User;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;

public class TestDatasourceService extends ApplicationTestCase<CoreApplication> {

	public TestDatasourceService() {
		super(CoreApplication.class);
	}

	@Override
	protected void setUp() throws Exception {
		createApplication();
		super.setUp();
	}
	
	
	public void testGetFriends() throws Exception {
		HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://dl.dropboxusercontent.com/u/16403954/xcore/getFriends.json");
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);
		GsonArrayContentValuesProcessor gsonArrayContentValuesProcessor = new GsonArrayContentValuesProcessor(User.class){

			@Override
			protected ContentValues[] process(Gson gson,
					BufferedReader bufferedReader) {
				return gson.fromJson(bufferedReader, FriendsResponse.class).getResults();
			}
			
		};
		ContentValues[] contentValues = gsonArrayContentValuesProcessor.execute(dataSourceRequest, httpAndroidDataSource, inputStream);
        IDBConnector connector = new SQLiteSupport().createConnector(getApplication());
		DBHelper dbHelper = new DBHelper(connector);
		dbHelper.createTablesForModels(User.class);
		dbHelper.updateOrInsert(User.class, contentValues);
	}


    public void testGetDialogs() throws Exception {
        IDBConnector connector = new SQLiteSupport().createConnector(getApplication());
        DBHelper dbHelper = new DBHelper(connector);
        dbHelper.delete(DBHelper.getTableName(Dialog.class), null, null);
        dbHelper.delete(DBHelper.getTableName(User.class), null, null);
		HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://dl.dropboxusercontent.com/u/16403954/xcore/getDialogs2.json");
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);

        //print(inputStream);

        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(getApplication(), new Class<?>[]{Response.class, Dialog.class, User.class});
        AbstractGsonBatchProcessor<DialogsResponse> gsonArrayContentValuesProcessor = new AbstractGsonBatchProcessor<DialogsResponse>(Response.class, DialogsResponse.class, dbContentProvider) {

            @Override
			public String getAppServiceKey() {
				// not needs there
				return null;
			}
		};
		gsonArrayContentValuesProcessor.execute(dataSourceRequest, httpAndroidDataSource, inputStream);
        Log.startAction("dialogInsertResult");
        Cursor cursor = dbHelper.query(Dialog.class, null, null, null, null, null, null, null);
        int dialogCount = cursor.getCount();
        CursorUtils.close(cursor);
        cursor = dbHelper.query(User.class, null, null, null, null, null, null, null);
        int userCount = cursor.getCount();
        CursorUtils.close(cursor);

        assertEquals(dialogCount, 171);
        assertEquals(userCount, 166);

	}

    private void print(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        inputStream));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();
        System.out.print("response: "+ response.toString());
        Log.d("response", response.toString());
    }


}
