package by.istin.android.xcore.provider.impl;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import by.istin.android.xcore.db.IDBSupport;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;

import java.util.List;

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

    private IDBSupport mDbSupport;

    public DBContentProviderSupport(Context context, IDBSupport dbSupport, Class<?> ... entities) {
        mContext = context;
        mEntities = entities;
        mDbSupport = dbSupport;
        mDbSupport.create(context, entities);
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
        List<String> pathSegments = uri.getPathSegments();
        String className;
        switch (sUriMatcher.match(uri)) {
            case MODELS:
                className = pathSegments.get(pathSegments.size()-1);
                break;
            case MODELS_ID:
                className = pathSegments.get(pathSegments.size()-2);
                if (where == null) {
                    where = StringUtil.EMPTY;
                }
                where = where + ModelContract.ModelColumns._ID + " = " + uri.getLastPathSegment();
                break;
            case MODELS_ID_NEGOTIVE:
                className = pathSegments.get(pathSegments.size()-2);
                if (where == null) {
                    where = StringUtil.EMPTY;
                }
                where = where + ModelContract.ModelColumns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = mDbSupport.delete(className, where, whereArgs);
        if (ModelContract.isNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public Uri insertOrUpdate(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != MODELS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String className = uri.getLastPathSegment();
        DataSourceRequest dataSourceRequest = ModelContract.getDataSourceRequestFromUri(uri);
        long rowId = mDbSupport.updateOrInsert(dataSourceRequest, className, initialValues);
        if (rowId != -1l) {
            Uri serializableModelUri = ContentUris.withAppendedId(uri, rowId);
            if (ModelContract.isNotify(uri)) {
                getContext().getContentResolver().notifyChange(
                        serializableModelUri, null);
            }
            return serializableModelUri;
        } else {
            throw new IllegalArgumentException(uri + ": " + initialValues.toString());
        }
    }

    @Override
    public int bulkInsertOrUpdate(Uri uri, ContentValues[] values) {
        String className = uri.getLastPathSegment();
        int count = mDbSupport.updateOrInsert(ModelContract.getDataSourceRequestFromUri(uri), className, values);
        if (count > 0) {
            if (ModelContract.isNotify(uri)) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String className = null;
        List<String> pathSegments = null;
        switch (sUriMatcher.match(uri)) {
            case MODELS:
                className = uri.getLastPathSegment();
                break;
            case MODELS_ID:
                pathSegments = uri.getPathSegments();
                className = pathSegments.get(pathSegments.size()-2);
                if (StringUtil.isEmpty(selection)) {
                    selection = ModelContract.ModelColumns._ID + " = " + uri.getLastPathSegment();
                } else {
                    selection = selection + ModelContract.ModelColumns._ID + " = " + uri.getLastPathSegment();
                }
                break;
            case MODELS_ID_NEGOTIVE:
                pathSegments = uri.getPathSegments();
                className = pathSegments.get(pathSegments.size()-2);
                if (StringUtil.isEmpty(selection)) {
                    selection = ModelContract.ModelColumns._ID + " = " + uri.getLastPathSegment();
                } else {
                    selection = selection + ModelContract.ModelColumns._ID + " = " + uri.getLastPathSegment();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (ModelContract.isSqlUri(className)) {
            Cursor c = mDbSupport.rawQuery(ModelContract.getSqlParam(uri), selectionArgs);
            if (c != null) {
                c.getCount();
                c.moveToFirst();
            }
            Uri observerUri = ModelContract.getObserverUri(uri);
            if (observerUri != null) {
                c.setNotificationUri(getContext().getContentResolver(), observerUri);
            }
            return c;
        } else {
            String limitParam = ModelContract.getLimitParam(uri);
            Cursor c = mDbSupport.query(className, projection, selection, selectionArgs, null, null, sortOrder, limitParam);
            if (c != null) {
                c.setNotificationUri(getContext().getContentResolver(), uri);
                c.getCount();
                c.moveToFirst();
            }
            return c;
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
