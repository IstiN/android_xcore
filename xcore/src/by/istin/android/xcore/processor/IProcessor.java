package by.istin.android.xcore.processor;

import java.io.InputStream;

import by.istin.android.xcore.source.DataSourceRequest;

public interface IProcessor<Result> {

	String getKey();
	
	Result execute(DataSourceRequest dataSourceRequest, InputStream inputStream);
	
}
