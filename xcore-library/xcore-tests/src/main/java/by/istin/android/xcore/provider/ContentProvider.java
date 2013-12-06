package by.istin.android.xcore.provider;

import by.istin.android.xcore.model.SimpleEntity;

/**
 * Created by Uladzimir_Klyshevich on 12/6/13.
 */
public class ContentProvider extends DBContentProvider {

    public static final Class<?>[] ENTITIES = new Class<?>[]{
            SimpleEntity.class
    };

    @Override
    public Class<?>[] getEntities() {
        return ENTITIES;
    }

}
