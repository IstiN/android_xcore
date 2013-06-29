package by.istin.android.xcore.source;

import java.io.IOException;

import by.istin.android.xcore.XCoreHelper.IAppServiceKey;

public interface IDataSource<T> extends IAppServiceKey {

	T getSource(DataSourceRequest dataSourceRequest) throws IOException;
	
}
