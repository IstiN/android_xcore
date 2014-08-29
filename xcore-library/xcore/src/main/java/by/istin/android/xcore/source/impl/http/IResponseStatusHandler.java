package by.istin.android.xcore.source.impl.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.utils.Holder;


public interface IResponseStatusHandler {
	
	void statusHandle(HttpAndroidDataSource client, DataSourceRequest dataSourceRequest, HttpUriRequest request, HttpResponse response, Holder<Boolean> isCached) throws ParseException, IOException;
	
}