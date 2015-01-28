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
import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.utils.Log;

public abstract class DBContentProvider extends ContentProvider {

    private IDBContentProviderSupport dbContentProviderSupport;

    @Override
	public boolean onCreate() {
        Context context = getContext();
        XCoreHelper xCoreHelper = XCoreHelper.get();
        Log.xd(this, "xCoreHelper onCreate");
        xCoreHelper.onCreate(context, getModules());
        dbContentProviderSupport = getContentProviderSupport(context);
        return true;
	}

    protected abstract List<Class<? extends XCoreHelper.Module>> getModules();

    protected IDBContentProviderSupport getContentProviderSupport(Context context) {
        return DBContentProviderFactory.getDefaultDBContentProvider(context, getEntities());
    }

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
        return dbContentProvider.getDbSupport().createConnector(context).getWritableConnection();
    }

    public static IDBConnection getReadableConnection(Context context, Class<?>[] entities) {
        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(context, entities);
        return dbContentProvider.getDbSupport().createConnector(context).getReadableConnection();
    }

    public static DBHelper getDBHelper(Context context, Class<?>[] entities) {
        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(context, entities);
        return dbContentProvider.getDbSupport().getOrCreateDBHelper(context);
    }

}