package by.istin.android.xcore.wearable;

import java.util.Collections;
import java.util.List;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.XCoreHelper;

/**
 * Created by IstiN on 14.09.2014.
 */
public class WearableApplication extends CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        registerAppService(new CoreWearable(this));
    }

    @Override
    public List<Class<? extends XCoreHelper.Module>> getModules() {
        return Collections.emptyList();
    }
}
