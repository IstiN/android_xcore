package by.istin.android.xcore.sample;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.XCoreHelper;

/**
 * Created by IstiN on 13.11.13.
 */
public class Application extends CoreApplication {

    public static final List<Class<? extends XCoreHelper.Module>> APP_MODULES;

    static {
        APP_MODULES = new ArrayList<>();
        APP_MODULES.add(SimpleAppModule.class);
    }

    @Override
    public List<Class<? extends XCoreHelper.Module>> getModules() {
        return APP_MODULES;
    }
}
