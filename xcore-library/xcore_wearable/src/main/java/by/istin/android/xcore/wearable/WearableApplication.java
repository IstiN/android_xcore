package by.istin.android.xcore.wearable;

import by.istin.android.xcore.CoreApplication;

/**
 * Created by IstiN on 14.09.2014.
 */
public class WearableApplication extends CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        registerAppService(new CoreWearable(this));
    }
}
