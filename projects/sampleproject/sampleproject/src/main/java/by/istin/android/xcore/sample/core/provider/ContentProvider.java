package by.istin.android.xcore.sample.core.provider;

import by.istin.android.xcore.provider.DBContentProvider;

/**
 * Created by IstiN on 13.11.13.
 */
public class ContentProvider extends DBContentProvider {

    private static final Class<?>[] ENTITIES = new Class<?>[]{


    };

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
