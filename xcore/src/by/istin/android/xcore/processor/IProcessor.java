package by.istin.android.xcore.processor;

import android.content.Context;

import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;

public interface IProcessor<Result, DataSourceResult> extends IAppServiceKey {

	Result execute(DataSourceRequest dataSourceRequest, IDataSource<DataSourceResult> dataSource, DataSourceResult dataSourceResult) throws Exception;
	
	void cache(Context context, DataSourceRequest dataSourceRequest, Result result) throws Exception;
	
}