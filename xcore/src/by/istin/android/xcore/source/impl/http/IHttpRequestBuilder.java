package by.istin.android.xcore.source.impl.http;

import org.apache.http.client.methods.HttpRequestBase;

public interface IHttpRequestBuilder {

	HttpRequestBase build(String url);
	
}
