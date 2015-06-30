package by.istin.android.xcore.source.impl.http;


import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.Holder;

public interface IRequestBuilder {

    HttpRequest build(DataSourceRequest dataSourceRequest) throws IOException;

    void postCreate(DataSourceRequest dataSourceRequest, HttpRequest request, Holder<Boolean> isCached);
}
