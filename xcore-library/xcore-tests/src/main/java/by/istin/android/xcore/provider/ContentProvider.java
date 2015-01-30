package by.istin.android.xcore.provider;

import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.issues.issue12.model.DayEntity;
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

/**
 * Created by Uladzimir_Klyshevich on 12/6/13.
 */
public class ContentProvider extends DBContentProvider {

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
    protected List<Class<? extends XCoreHelper.Module>> getModules() {
        return Application.APP_MODULES;
    }

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
