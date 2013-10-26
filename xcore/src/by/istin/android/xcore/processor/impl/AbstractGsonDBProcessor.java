package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;
import by.istin.android.xcore.gson.ContentValuesAdapter;
import by.istin.android.xcore.gson.DBContentValuesAdapter;
import by.istin.android.xcore.gson.external.ArrayAdapterFactory;
import by.istin.android.xcore.processor.AbstractDBProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractGsonDBProcessor<Result, DataSourceResult> extends AbstractDBProcessor<Result, DataSourceResult> {

    public static Gson createGsonWithContentValuesAdapter(Class<?> clazz) {
        return createGsonWithContentValuesAdapter(new ContentValuesAdapter(clazz));
    }

    public static Gson createGsonWithDbContentValuesAdapter(Class<?> clazz, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider) {
        return createGsonWithContentValuesAdapter(new DBContentValuesAdapter(clazz, dataSourceRequest, dbContentProvider));
    }

    public static Gson createGsonWithContentValuesAdapter(ContentValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = initGsonBuilder(contentValuesAdapter);
        return gsonBuilder.create();
    }

    public static GsonBuilder initGsonBuilder(ContentValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter);
        gsonBuilder.registerTypeAdapterFactory(new ArrayAdapterFactory(contentValuesAdapter));
        return gsonBuilder;
    }

}