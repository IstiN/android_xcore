package by.isitn.android.xcore.app;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.processor.SimpleEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityProcessor;
import by.istin.android.xcore.provider.ContentProvider;
import by.istin.android.xcore.provider.IDBContentProviderSupport;

/**
 * Created by Uladzimir_Klyshevich on 12/6/13.
 */
public class Application extends CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        IDBContentProviderSupport defaultDBContentProvider = getDefaultDBContentProvider(ContentProvider.ENTITIES);

        registerAppService(new SimpleEntityProcessor());
        registerAppService(new SimpleEntityBatchProcessor(defaultDBContentProvider));
    }
}
