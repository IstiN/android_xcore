package by.istin.android.xcore.test.common;

import java.io.IOException;
import java.io.InputStream;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.Holder;

/**
 * Created by IstiN on 3.11.13.
 */
public class TestDataSource implements IDataSource<InputStream> {

    @Override
    public InputStream getSource(DataSourceRequest dataSourceRequest, Holder<Boolean> isCached) throws IOException {
        String path = dataSourceRequest.getUri().split("\\?")[0];
        return getInputStream("assets/feeds/" + path);
    }

    private InputStream getInputStream(String uri) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(uri);
    }

    @Override
    public String getAppServiceKey() {
        return HttpAndroidDataSource.SYSTEM_SERVICE_KEY;
    }
}
