package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.gson.ContentValuesAdapter;
import by.istin.android.xcore.gson.DBContentValuesAdapter;
import by.istin.android.xcore.gson.external.ArrayAdapterFactory;
import by.istin.android.xcore.processor.AbstractDBProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;

public abstract class AbstractGsonDBProcessor<Result, DataSourceResult> extends AbstractDBProcessor<Result, DataSourceResult> {

    public static Gson createGsonWithContentValuesAdapter(int listBufferSize, Class<?> clazz) {
        return createGsonWithContentValuesAdapter(listBufferSize, new ContentValuesAdapter(clazz));
    }

    public static Gson createGsonWithDbContentValuesAdapter(Class<?> clazz, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider, int listBufferSize) {
        return createGsonWithContentValuesAdapter(listBufferSize, new DBContentValuesAdapter(clazz, dataSourceRequest, dbContentProvider));
    }

    public static Gson createGsonWithContentValuesAdapter(int listBufferSize, ContentValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = initGsonBuilder(listBufferSize, contentValuesAdapter);
        return gsonBuilder.create();
    }

    public static GsonBuilder initGsonBuilder(int listBufferSize, ContentValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter);
        gsonBuilder.registerTypeAdapterFactory(new ArrayAdapterFactory(listBufferSize, contentValuesAdapter));
        return gsonBuilder;
    }

    public static Context getHolderContext() {
        return ContextHolder.getInstance().getContext();
    }

}