package by.istin.android.xcore.source.impl.http;

import org.apache.http.client.methods.HttpRequestBase;

import by.istin.android.xcore.source.DataSourceRequest;

public interface IHttpRequestBuilder {

	HttpRequestBase build(DataSourceRequest dataSourceRequest);
	
}
