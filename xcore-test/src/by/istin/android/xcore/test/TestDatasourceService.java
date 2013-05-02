package by.istin.android.xcore.test;

import java.io.BufferedReader;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.test.ApplicationTestCase;
import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.processor.impl.AbstractGsonProcessor;
import by.istin.android.xcore.processor.impl.GsonArrayContentValuesProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.test.bo.DialogsResponse;
import by.istin.android.xcore.test.bo.FriendsResponse;
import by.istin.android.xcore.test.vk.Dialog;
import by.istin.android.xcore.test.vk.User;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TestDatasourceService extends ApplicationTestCase<CoreApplication> {

	private static final String ACCESS_TOKEN = "";

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
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://api.vkontakte.ru/method/execute.getFriends?access_token="
				+ ACCESS_TOKEN);
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);
		GsonArrayContentValuesProcessor gsonArrayContentValuesProcessor = new GsonArrayContentValuesProcessor(User.class){

			@Override
			protected ContentValues[] process(Gson gson,
					BufferedReader bufferedReader) {
				return gson.fromJson(bufferedReader, FriendsResponse.class).getResults();
			}
			
		};
		ContentValues[] contentValues = gsonArrayContentValuesProcessor.execute(dataSourceRequest, httpAndroidDataSource, inputStream);
		DBHelper dbHelper = new DBHelper(getApplication());
		dbHelper.createTablesForModels(User.class);
		dbHelper.updateOrInsert(User.class, contentValues);
	}
	
	public void testGetDialogs() throws Exception {
		HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://api.vkontakte.ru/method/execute.getDialogs?offset=0&access_token="+ACCESS_TOKEN);
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);
		AbstractGsonProcessor<DialogsResponse> gsonArrayContentValuesProcessor = new AbstractGsonProcessor<DialogsResponse>(Dialog.class, DialogsResponse.class) {

			@Override
			public void cache(Context context, DataSourceRequest dataSourceRequest, DialogsResponse result) {
				
			}
			
			@Override
			protected DialogsResponse process(Gson gson,
					BufferedReader bufferedReader) {
				JsonObject jsonObject = getGson().fromJson(bufferedReader, JsonObject.class);
				//TODO new Gson().fromJson(json, classOfT)
				DialogsResponse process = super.process(gson, bufferedReader);
				return process;
			}

			@Override
			public String getAppServiceKey() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		DialogsResponse dialogResponse = gsonArrayContentValuesProcessor.execute(dataSourceRequest, httpAndroidDataSource, inputStream);
		ContentValues[] dialogs = dialogResponse.getResponse().getDialogs();
		ContentValues[] users = dialogResponse.getResponse().getUsers();
		DBHelper dbHelper = new DBHelper(getApplication());
		//dbHelper.createTablesForModels(Dialog.class);
		//dbHelper.updateOrInsert(Dialog.class, contentValues);
	}
	

}
