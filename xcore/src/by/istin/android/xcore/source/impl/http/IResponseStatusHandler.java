package by.istin.android.xcore.source.impl.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpUriRequest;

import by.istin.android.xcore.source.impl.http.exception.IOStatusException;


public interface IResponseStatusHandler {
	
	void statusHandle(HttpDataSource client, HttpUriRequest request, HttpResponse response) throws IOStatusException, ParseException, IOException;
	
}