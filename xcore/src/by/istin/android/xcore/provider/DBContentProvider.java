package by.istin.android.xcore.provider;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;

public abstract class DBContentProvider extends ContentProvider {

    private IDBContentProviderSupport dbContentProviderSupport;

    @Override
	public final boolean onCreate() {
        dbContentProviderSupport = getContentProviderSupport(getContext());
        return true;
	}

    protected abstract IDBContentProviderSupport getContentProviderSupport(Context context);

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


}