package by.istin.android.xcore.processor.impl;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;
import by.istin.android.xcore.utils.Log;

public abstract class AbstractStringProcessor<Result> implements IProcessor<Result, InputStream> {

    public static final String UTF_8 = "UTF-8";

    @Override
    public Result execute(DataSourceRequest dataSourceRequest, IDataSource<InputStream> dataSource, InputStream inputStream) throws Exception {
        InputStreamReader inputStreamReader = null;
        BufferedReader streamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, UTF_8);
            streamReader = new BufferedReader(inputStreamReader);
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            String string = responseStrBuilder.toString();
            Log.xd(this, string);
            return convert(string);
        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(inputStreamReader);
            IOUtils.close(streamReader);
        }
    }

    protected abstract Result convert(String string) throws Exception;

    @Override
    public void cache(Context context, DataSourceRequest dataSourceRequest, Result result) throws Exception {

    }

}
