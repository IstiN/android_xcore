package by.istin.android.xcore.processor.impl;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.gson.DBContentValuesAdapter;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.utils.IOUtils;

public abstract class AbstractGsonBatchProcessor<Result> extends AbstractGsonDBProcessor<Result, InputStream>{

	private Class<?> clazz;

	private Class<? extends Result> resultClassName;

    private IDBContentProviderSupport dbContentProviderSupport;

	public AbstractGsonBatchProcessor(Class<?> clazz, Class<? extends Result> resultClassName, IDBContentProviderSupport contentProviderSupport) {
		super();
		this.clazz = clazz;
		this.resultClassName = resultClassName;
        this.dbContentProviderSupport = contentProviderSupport;
	}


	@Override
	public Result execute(DataSourceRequest dataSourceRequest, IDataSource<InputStream> dataSource, InputStream inputStream) throws Exception {
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);
        DBContentValuesAdapter contentValuesAdapter = new DBContentValuesAdapter(clazz, dataSourceRequest, dbContentProviderSupport);
        Gson gson = buildGson(contentValuesAdapter);
        IDBConnection dbConnection = contentValuesAdapter.getDbConnection();
        dbConnection.beginTransaction();
        Result result = null;
		try {
            onStartProcessing(dataSourceRequest, dbConnection);
            result = process(gson, bufferedReader);
            onBeforeTransactionCommit(dataSourceRequest, result, dbConnection);
            dbConnection.setTransactionSuccessful();
		} catch (JsonSyntaxException exception){
            Throwable cause = exception.getCause();
            if (cause instanceof SocketTimeoutException) {
                throw new IOException(exception);
            }
		} catch (JsonIOException exception){
            throw new IOException(exception);
        } finally {
			IOUtils.close(inputStream);
			IOUtils.close(inputStreamReader);
			IOUtils.close(bufferedReader);
            dbConnection.endTransaction();
            onProcessingFinish(dataSourceRequest, result);
		}
        return result;
	}

    protected Gson buildGson(DBContentValuesAdapter contentValuesAdapter) {
        return createGsonWithContentValuesAdapter(contentValuesAdapter);
    }

    public DBHelper getDbHelper() {
        return dbContentProviderSupport.getDbSupport().getOrCreateDBHelper(ContextHolder.getInstance().getContext());
    }

    protected void onBeforeTransactionCommit(DataSourceRequest dataSourceRequest, Result result, IDBConnection dbConnection) {
        //do additional manipulations there
    }

    protected void onStartProcessing(DataSourceRequest dataSourceRequest, IDBConnection dbConnection) {
        //remove old data
    };

    protected void onProcessingFinish(DataSourceRequest dataSourceRequest, Result result) {
        //notify about finish or do more db operations
    };

    protected Result process(Gson gson, BufferedReader bufferedReader) {
        return (Result) gson.fromJson(bufferedReader, resultClassName);
	}

	public Class<?> getClazz() {
		return clazz;
	}

    @Override
    public final void cache(Context context, DataSourceRequest dataSourceRequest, Result result) throws Exception {
        //this processor can be used only for caching
    }
}
