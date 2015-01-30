package by.istin.android.xcore.app;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.XCoreHelper;

/**
 * Created by Uladzimir_Klyshevich on 12/6/13.
 */
public class Application extends CoreApplication {

    public static final List<Class<? extends XCoreHelper.Module>> APP_MODULES;

    static {
        APP_MODULES = new ArrayList<>();
        APP_MODULES.add(AppModule.class);
    }

    @Override
    public List<Class<? extends XCoreHelper.Module>> getModules() {
        return APP_MODULES;
    }
}
