package by.istin.android.xcore.processor.impl;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.gson.AbstractValuesAdapter;
import by.istin.android.xcore.gson.external.ArrayAdapterFactory;
import by.istin.android.xcore.processor.AbstractDBProcessor;

public abstract class AbstractGsonDBProcessor<Result, DataSourceResult> extends AbstractDBProcessor<Result, DataSourceResult> {

    public static Gson createGsonWithContentValuesAdapter(int listBufferSize, AbstractValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = initGsonBuilder(listBufferSize, contentValuesAdapter);
        return gsonBuilder.create();
    }

    public static GsonBuilder initGsonBuilder(int listBufferSize, AbstractValuesAdapter contentValuesAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(ContentValues.class, contentValuesAdapter);
        gsonBuilder.registerTypeAdapterFactory(new ArrayAdapterFactory(listBufferSize, contentValuesAdapter));
        return gsonBuilder;
    }

    public static Context getHolderContext() {
        return ContextHolder.get();
    }

}