package by.istin.android.xcore.test.http;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource.DefaultHttpRequestBuilder;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource.DefaultHttpRequestBuilder.Type;

public class HttpRequestBuilderTest extends ApplicationTestCase<Application> {

	private static final String TEST_URL_POST_PUT = "https://dl.dropboxusercontent.com/u/16403954/xcore/empty.json?some_key=value&some_key1=value1";
	
	private static final String TEST_URL_GET_DELETE = "https://dl.dropboxusercontent.com/u/16403954/xcore/empty.json";

	
	private DefaultHttpRequestBuilder requestBuilder;
	
	public HttpRequestBuilderTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		requestBuilder = new DefaultHttpRequestBuilder();
	}
	
	public void testGet() throws Exception {
		HttpRequestBase request = requestBuilder.build(new DataSourceRequest(TEST_URL_GET_DELETE));
		assertTrue(request instanceof HttpGet);
	}
	
	public void testPost() throws Exception {
		String typedUrl = DefaultHttpRequestBuilder.getUrl(TEST_URL_POST_PUT, Type.POST);
		HttpRequestBase request = requestBuilder.build(new DataSourceRequest(typedUrl));
		assertTrue(request instanceof HttpPost);
	}
	
	public void testPut() throws Exception {
		String typedUrl = DefaultHttpRequestBuilder.getUrl(TEST_URL_POST_PUT, Type.PUT);
		HttpRequestBase request = requestBuilder.build(new DataSourceRequest(typedUrl));
		assertTrue(request instanceof HttpPut);
	}
	
	public void testDelete() throws Exception {
		String typedUrl = DefaultHttpRequestBuilder.getUrl(TEST_URL_GET_DELETE, Type.DELETE);
		HttpRequestBase request = requestBuilder.build(new DataSourceRequest(typedUrl));
		assertTrue(request instanceof HttpDelete);
	}

}
