package by.istin.android.xcore.provider;

import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.app.Application;

/**
 * Created by Uladzimir_Klyshevich on 12/6/13.
 */
public class ContentProvider extends DBContentProvider {

    @Override
    protected List<Class<? extends XCoreHelper.Module>> getModules() {
        return Application.APP_MODULES;
    }

}
