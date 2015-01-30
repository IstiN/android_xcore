package by.istin.android.xcore.processor.impl;

import android.content.Context;

import java.io.InputStream;

import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

public class EmptyProcessor implements IProcessor<Void, InputStream> {

    public static final String APP_SERVICE_KEY = "xcore:core:emptyprocessor";

    @Override
    public Void execute(DataSourceRequest dataSourceRequest, IDataSource<InputStream> dataSource, InputStream inputStream) throws Exception {
        IOUtils.close(inputStream);
        return null;
    }

    @Override
    public void cache(Context context, DataSourceRequest dataSourceRequest, Void aVoid) throws Exception {

    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }
}
