package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import by.istin.android.xcore.gson.AbstractValuesAdapter;
import by.istin.android.xcore.gson.ContentValuesAdapter;
import by.istin.android.xcore.gson.DBContentValuesAdapter;
import by.istin.android.xcore.gson.TypeContentValues;
import by.istin.android.xcore.gson.external.ArrayAdapterFactory;
import by.istin.android.xcore.processor.AbstractDBProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;

public abstract class AbstractGsonDBProcessor<Result, DataSourceResult> extends AbstractDBProcessor<Result, DataSourceResult> {

    public static Gson createGsonWithContentValuesAdapter(Class<?> clazz) {
        return createGsonWithContentValuesAdapter(new ContentValuesAdapter(clazz));
    }

    public static Gson createGsonWithDbContentValuesAdapter(Class<?> clazz, DataSourceRequest dataSourceRequest, IDBContentProviderSupport dbContentProvider) {
        return createGsonWithContentValuesAdapter(new DBContentValuesAdapter(clazz, dataSourceRequest, dbContentProvider));
    }

    public static Gson createGsonWithContentValuesAdapter(AbstractValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = initGsonBuilder(contentValuesAdapter);
        return gsonBuilder.create();
    }

    public static GsonBuilder initGsonBuilder(AbstractValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter);
        gsonBuilder.registerTypeAdapterFactory(new ArrayAdapterFactory(contentValuesAdapter));
        return gsonBuilder;
    }

}