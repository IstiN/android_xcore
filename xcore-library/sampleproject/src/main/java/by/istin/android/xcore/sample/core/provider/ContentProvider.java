package by.istin.android.xcore.sample.core.provider;

import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.provider.DBContentProvider;
import by.istin.android.xcore.sample.Application;

/**
 * Created by IstiN on 13.11.13.
 */
public class ContentProvider extends DBContentProvider {

    @Override
    protected List<Class<? extends XCoreHelper.Module>> getModules() {
        return Application.APP_MODULES;
    }

}
