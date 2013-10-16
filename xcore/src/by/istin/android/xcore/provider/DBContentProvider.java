package by.istin.android.xcore.provider;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;

import java.util.ArrayList;

public abstract class DBContentProvider extends ContentProvider {

    private IDBContentProviderSupport dbContentProviderSupport;

    @Override
	public final boolean onCreate() {
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
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        return dbContentProviderSupport.applyBatch(operations);
    }

    public abstract Class<?> getEntities();
}