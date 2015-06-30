package by.istin.android.xcore.source.impl.http;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;

@Deprecated
public interface IHttpRequestBuilder {

    HttpRequestBase build(DataSourceRequest dataSourceRequest) throws IOException;

}
