package by.istin.android.xcore.processor;

import java.io.InputStream;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;

public interface IProcessor<Result> {

	String getKey();
	
	Result execute(DataSourceRequest dataSourceRequest, IDataSource dataSource, InputStream inputStream) throws Exception;
	
}
