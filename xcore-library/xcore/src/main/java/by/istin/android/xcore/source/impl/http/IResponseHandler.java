package by.istin.android.xcore.source.impl.http;

import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Holder;


public interface IResponseHandler {

    void statusHandle(HttpDataSource client, DataSourceRequest dataSourceRequest, HttpRequest request, HttpRequest response, Holder<Boolean> isCached) throws IOException;

}