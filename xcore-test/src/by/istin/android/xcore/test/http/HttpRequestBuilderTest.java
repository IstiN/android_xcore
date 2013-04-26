package by.istin.android.xcore.test.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import android.app.Application;
import android.test.ApplicationTestCase;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource.DefaultHttpRequestBuilder;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource.DefaultHttpRequestBuilder.Type;

public class HttpRequestBuilderTest extends ApplicationTestCase<Application> {

	private static final String TEST_URL_POST_PUT = "https://dl.dropbox.com/u/52289508/GP/dummy_data_eventlist.json?some_key=value&some_key1=value1";
	
	private static final String TEST_URL_GET_DELETE = "https://dl.dropbox.com/u/52289508/GP/dummy_data_eventlist.json";

	
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
		HttpRequestBase request = requestBuilder.build(TEST_URL_GET_DELETE);
		assertTrue(request instanceof HttpGet);
	}
	
	public void testPost() throws Exception {
		String typedUrl = DefaultHttpRequestBuilder.getUrl(TEST_URL_POST_PUT, Type.POST);
		HttpRequestBase request = requestBuilder.build(typedUrl);
		assertTrue(request instanceof HttpPost);
	}
	
	public void testPut() throws Exception {
		String typedUrl = DefaultHttpRequestBuilder.getUrl(TEST_URL_POST_PUT, Type.PUT);
		HttpRequestBase request = requestBuilder.build(typedUrl);
		assertTrue(request instanceof HttpPut);
	}
	
	public void testDelete() throws Exception {
		String typedUrl = DefaultHttpRequestBuilder.getUrl(TEST_URL_GET_DELETE, Type.DELETE);
		HttpRequestBase request = requestBuilder.build(typedUrl);
		assertTrue(request instanceof HttpDelete);
	}

}
