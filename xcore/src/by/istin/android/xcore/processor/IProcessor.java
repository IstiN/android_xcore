package by.istin.android.xcore.processor;

import java.io.InputStream;

import android.content.Context;
import by.istin.android.xcore.CoreApplication.IAppServiceKey;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;

public interface IProcessor<Result> extends IAppServiceKey {

	Result execute(DataSourceRequest dataSourceRequest, IDataSource dataSource, InputStream inputStream) throws Exception;
	
	void cache(Context context, DataSourceRequest dataSourceRequest, Result result);
	
}