package by.istin.android.xcore.provider.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;

/**
 * Created with IntelliJ IDEA.
 * User: Uladzimir_Klyshevich
 * Date: 10/15/13
 */
public class DBContentProviderSupport implements IDBContentProviderSupport {

    private static UriMatcher sUriMatcher;

    private static final int MODELS = 1;

    private static final int MODELS_ID = 2;

    private static final int MODELS_ID_NEGOTIVE = 3;

    private Context mContext;

    private Class<?>[] mEntities;

    public DBContentProviderSupport(Context context, Class<?> ... entities) {
        mContext = context;
        mEntities = entities;
        initUriMatcher();
    }

    private void initUriMatcher() {
        if (sUriMatcher == null) {
            sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            String authority = ModelContract.getAuthority(getContext());
            sUriMatcher.addURI(authority, "*", MODELS);
            sUriMatcher.addURI(authority, "*/#", MODELS_ID);
            //for negotive number
            sUriMatcher.addURI(authority, "*/*", MODELS_ID_NEGOTIVE);
        }
    }

    @Override
    public Class<?>[] getEntities() {
        return mEntities;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MODELS:
                return ModelContract.getContentType(uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        return 0;
    }

    @Override
    public Uri insertOrUpdate(Uri uri, ContentValues initialValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int bulkInsertOrUpdate(Uri uri, ContentValues[] values) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
