package by.istin.android.xcore.app;

import android.content.Context;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.issues.issue12.model.DayEntity;
import by.istin.android.xcore.issues.issue12.processor.DaysBatchProcessor;
import by.istin.android.xcore.model.BigTestEntity;
import by.istin.android.xcore.model.BigTestSubEntity;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.model.SimpleEntityWithCustomPrimitiveConverter;
import by.istin.android.xcore.model.SimpleEntityWithParent;
import by.istin.android.xcore.model.SimpleEntityWithParent1;
import by.istin.android.xcore.model.SimpleEntityWithParent2;
import by.istin.android.xcore.model.SimpleEntityWithPrimitiveEntity;
import by.istin.android.xcore.model.SimpleEntityWithSubEntities;
import by.istin.android.xcore.model.SimpleEntityWithSubEntity;
import by.istin.android.xcore.model.SimpleEntityWithSubJson;
import by.istin.android.xcore.model.SuperBigTestEntity;
import by.istin.android.xcore.model.TagEntity;
import by.istin.android.xcore.processor.SimpleEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithPrimitiveConverterBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithPrimitiveEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntitiesBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubJsonBatchProcessor;
import by.istin.android.xcore.processor.SuperBigEntityBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;

/**
 * Created by Uladzimir_Klyshevich on 1/30/2015.
 */
public class AppModule extends XCoreHelper.BaseModule {

    public static final Class<?>[] ENTITIES = new Class<?>[]{
            SimpleEntity.class,
            SimpleEntityWithSubEntity.class,
            SimpleEntityWithParent.class,
            SimpleEntityWithSubJson.class,
            SimpleEntityWithPrimitiveEntity.class,
            TagEntity.class,
            SimpleEntityWithCustomPrimitiveConverter.class,
            SimpleEntityWithSubEntities.class,
            SimpleEntityWithParent1.class,
            SimpleEntityWithParent2.class,

            BigTestEntity.class,
            BigTestSubEntity.class,

            SuperBigTestEntity.class,

            //issue12
            DayEntity.class
    };

    @Override
    protected void onCreate(Context context) {
        IDBContentProviderSupport defaultDBContentProvider = registerContentProvider(ENTITIES);

        registerAppService(new SimpleEntityBatchProcessor(defaultDBContentProvider));
        registerAppService(new SimpleEntityWithSubEntityBatchProcessor(defaultDBContentProvider));
        registerAppService(new SimpleEntityWithSubJsonBatchProcessor(defaultDBContentProvider));
        registerAppService(new SimpleEntityWithPrimitiveEntityBatchProcessor(defaultDBContentProvider));
        registerAppService(new SimpleEntityWithPrimitiveConverterBatchProcessor(defaultDBContentProvider));
        registerAppService(new SimpleEntityWithSubEntitiesBatchProcessor(defaultDBContentProvider));
        registerAppService(new SuperBigEntityBatchProcessor(defaultDBContentProvider));

        //issue 12
        registerAppService(new DaysBatchProcessor(defaultDBContentProvider));
    }

}
