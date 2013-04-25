package by.istin.android.xcore.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpDataSource;

public class TestHttpDataSource extends ApplicationTestCase<Application> {

	public TestHttpDataSource() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
	}

	public void testLoad() throws Exception {
		InputStream inputStream = new HttpDataSource().getSource(new DataSourceRequest("https://dl.dropboxusercontent.com/u/16403954/app_bundler.json"));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8192);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line).append(System.getProperty("line.separator"));
		}
		String value = sb.toString();
		Log.d("TestHttpDataSource", value);
	}

}
