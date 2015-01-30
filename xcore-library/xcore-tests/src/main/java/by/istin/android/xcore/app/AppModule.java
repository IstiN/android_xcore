package by.istin.android.xcore.app;

import android.content.Context;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.issues.issue12.processor.DaysBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithPrimitiveConverterBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithPrimitiveEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntitiesBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubJsonBatchProcessor;
import by.istin.android.xcore.processor.SuperBigEntityBatchProcessor;
import by.istin.android.xcore.provider.ContentProvider;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;

/**
 * Created by Uladzimir_Klyshevich on 1/30/2015.
 */
public class AppModule implements XCoreHelper.Module {
    @Override
    public void onCreate(Context context, XCoreHelper coreHelper) {
        IDBContentProviderSupport defaultDBContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(context, ContentProvider.ENTITIES);

        coreHelper.registerAppService(new SimpleEntityBatchProcessor(defaultDBContentProvider));
        coreHelper.registerAppService(new SimpleEntityWithSubEntityBatchProcessor(defaultDBContentProvider));
        coreHelper.registerAppService(new SimpleEntityWithSubJsonBatchProcessor(defaultDBContentProvider));
        coreHelper.registerAppService(new SimpleEntityWithPrimitiveEntityBatchProcessor(defaultDBContentProvider));
        coreHelper.registerAppService(new SimpleEntityWithPrimitiveConverterBatchProcessor(defaultDBContentProvider));
        coreHelper.registerAppService(new SimpleEntityWithSubEntitiesBatchProcessor(defaultDBContentProvider));
        coreHelper.registerAppService(new SuperBigEntityBatchProcessor(defaultDBContentProvider));

        //issue 12
        coreHelper.registerAppService(new DaysBatchProcessor(defaultDBContentProvider));
    }
}
