package by.istin.android.xcore.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;

public abstract class DBContentProvider extends ContentProvider {

    private IDBContentProviderSupport dbContentProviderSupport;

    @Override
	public boolean onCreate() {
        Context context = ContextHolder.getInstance().getContext();
        if (context == null) {
            ContextHolder.getInstance().setContext(getContext());
        }
        dbContentProviderSupport = getContentProviderSupport(getContext());
        return true;
	}

    protected IDBContentProviderSupport getContentProviderSupport(Context context) {
        DBContentProviderFactory dbContentProviderFactory = DBContentProviderFactory.getInstance();
        return dbContentProviderFactory.getDbContentProvider(context, DBContentProviderFactory.Type.SQLite, getEntities());
    };

    @Override
    public final String getType(Uri uri) {
        return dbContentProviderSupport.getType(uri);
    }

    @Override
    public final int delete(Uri uri, String where, String[] whereArgs) {
        return dbContentProviderSupport.delete(uri, where, whereArgs);
    }

    @Override
    public final Uri insert(Uri uri, ContentValues initialValues) {
        return dbContentProviderSupport.insertOrUpdate(uri, initialValues);
    }

	@Override
	public final int bulkInsert(Uri uri, ContentValues[] values) {
        return dbContentProviderSupport.bulkInsertOrUpdate(uri, values);
	}

    @Override
    public final Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return dbContentProviderSupport.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
	public final int update(Uri uri, ContentValues initialValues, String where,
			String[] whereArgs) {
		throw new UnsupportedOperationException("unsupported operation, please use insert method");
	}

    @Override
    public final ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        return dbContentProviderSupport.applyBatch(operations);
    }

    public abstract Class<?>[] getEntities();

    public static IDBConnection getWritableConnection(Context context, Class<?>[] entities) {
        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(context, entities);
        IDBConnection writableConnection = dbContentProvider.getDbSupport().createConnector(context).getWritableConnection();
        return writableConnection;
    }

    public static IDBConnection getReadableConnection(Context context, Class<?>[] entities) {
        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(context, entities);
        IDBConnection writableConnection = dbContentProvider.getDbSupport().createConnector(context).getReadableConnection();
        return writableConnection;
    }

    public static DBHelper getDBHelper(Context context, Class<?>[] entities) {
        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(context, entities);
        DBHelper dbHelper = dbContentProvider.getDbSupport().getOrCreateDBHelper(context);
        return dbHelper;
    }

}