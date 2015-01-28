package by.istin.android.xcore.sample.core.provider;

import android.content.Context;

import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.provider.DBContentProvider;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.sample.Application;
import by.istin.android.xcore.sample.core.model.Content;
import by.istin.android.xcore.sample.core.model.SampleEntity;

/**
 * Created by IstiN on 13.11.13.
 */
public class ContentProvider extends DBContentProvider {

    private static final Class<?>[] ENTITIES = new Class<?>[]{

            SampleEntity.class,

            Content.class

    };

    @Override
    protected List<Class<? extends XCoreHelper.Module>> getModules() {
        return Application.APP_MODULES;
    }

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

    public static IDBContentProviderSupport getDBContentProviderSupport(Context context) {
        return DBContentProviderFactory.getDefaultDBContentProvider(context, ENTITIES);
    }
}
