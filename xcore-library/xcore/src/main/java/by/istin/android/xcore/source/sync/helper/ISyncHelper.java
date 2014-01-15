package by.istin.android.xcore.source.sync.helper;

import by.istin.android.xcore.XCoreHelper;

/**
 * Created by Uladzimir_Klyshevich on 1/13/14.
 */
public interface ISyncHelper extends XCoreHelper.IAppServiceKey {

    public static final String APP_SERVICE_KEY = "xcore:synchelper";

    void addSyncAccount();

    void removeSyncAccount();

    public void removeAccount();

}
