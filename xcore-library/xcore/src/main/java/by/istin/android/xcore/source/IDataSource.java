package by.istin.android.xcore.source;

import java.io.IOException;

import by.istin.android.xcore.XCoreHelper.IAppServiceKey;
import by.istin.android.xcore.utils.Holder;

public interface IDataSource<T> extends IAppServiceKey {

	T getSource(DataSourceRequest dataSourceRequest, Holder<Boolean> isCached) throws IOException;

    public static interface ICacheValidationSupport {

    }
}
