package by.istin.android.xcore.source;

import java.io.IOException;
import java.io.InputStream;

public interface IDataSource {

	InputStream getSource(DataSourceRequest dataSourceRequest) throws IOException;
	
}
