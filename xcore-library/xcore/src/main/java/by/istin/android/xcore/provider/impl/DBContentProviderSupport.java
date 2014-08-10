package by.istin.android.xcore.provider.impl;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import by.istin.android.xcore.db.IDBSupport;
import by.istin.android.xcore.db.operation.IDBBatchOperationSupport;
import by.istin.android.xcore.db.operation.IDBDeleteOperationSupport;
import by.istin.android.xcore.db.operation.IDBInsertOrUpdateOperationSupport;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;

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

    private final Context mContext;

    private final Class<?>[] mEntities;

    private final IDBSupport mDbSupport;

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
        return internalDelete(mDbSupport, uri, where, whereArgs);
    }

    private int internalDelete(IDBDeleteOperationSupport writeOperationSupport, Uri uri, String where, String[] whereArgs) {
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
                where = where + BaseColumns._ID + " = " + uri.getLastPathSegment();
                break;
            case MODELS_ID_NEGOTIVE:
                className = pathSegments.get(pathSegments.size()-2);
                if (where == null) {
                    where = StringUtil.EMPTY;
                }
                where = where + BaseColumns._ID + " = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = writeOperationSupport.delete(className, where, whereArgs);
        if (ModelContract.isNotify(uri)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public Uri insertOrUpdate(Uri uri, ContentValues initialValues) {
        return internalInsertOrUpdate(mDbSupport, uri, initialValues);
    }

    private Uri internalInsertOrUpdate(IDBInsertOrUpdateOperationSupport writeOperationSupport, Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != MODELS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String className = uri.getLastPathSegment();
        DataSourceRequest dataSourceRequest = ModelContract.getDataSourceRequestFromUri(uri);
        long rowId = writeOperationSupport.updateOrInsert(dataSourceRequest, className, initialValues);
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
        String className;
        List<String> pathSegments;
        switch (sUriMatcher.match(uri)) {
            case MODELS:
                className = uri.getLastPathSegment();
                break;
            case MODELS_ID:
                pathSegments = uri.getPathSegments();
                className = pathSegments.get(pathSegments.size()-2);
                if (StringUtil.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + uri.getLastPathSegment();
                } else {
                    selection = selection + BaseColumns._ID + " = " + uri.getLastPathSegment();
                }
                break;
            case MODELS_ID_NEGOTIVE:
                pathSegments = uri.getPathSegments();
                className = pathSegments.get(pathSegments.size()-2);
                if (StringUtil.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + uri.getLastPathSegment();
                } else {
                    selection = selection + BaseColumns._ID + " = " + uri.getLastPathSegment();
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
                Uri observerUri = ModelContract.getObserverUri(uri);
                if (observerUri != null) {
                    c.setNotificationUri(getContext().getContentResolver(), observerUri);
                }
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
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        Set<Uri> set = new HashSet<Uri>();
        IDBBatchOperationSupport batchOperationConnection = mDbSupport.getConnectionForBatchOperation();
        try {
            batchOperationConnection.beginTransaction();
            for(int i = 0; i < operations.size(); i++) {
                ContentProviderOperation contentProviderOperation = operations.get(i);
                Uri uri = contentProviderOperation.getUri();
                if (ModelContract.isNotify(uri)) {
                    set.add(uri);
                }
                result[i] = apply(batchOperationConnection, contentProviderOperation, result, i);
            }
            batchOperationConnection.setTransactionSuccessful();
        } finally {
            batchOperationConnection.endTransaction();
        }
        for (Uri uri : set) {
            getContext().getContentResolver().notifyChange(Uri.parse(uri.toString().split("\\?")[0]), null);
        }
        return result;
    }

    public ContentProviderResult apply(IDBBatchOperationSupport batchOperationConnection, ContentProviderOperation contentProviderOperation, ContentProviderResult[] backRefs,
                                       int numBackRefs) throws OperationApplicationException {
        ContentValues values = contentProviderOperation.resolveValueBackReferences(backRefs, numBackRefs);
        String[] selectionArgs = contentProviderOperation.resolveSelectionArgsBackReferences(backRefs, numBackRefs);
        Uri uri = contentProviderOperation.getUri();
        if (values != null) {
            Uri newUri = internalInsertOrUpdate(batchOperationConnection, uri, values);
            if (newUri == null) {
                throw new OperationApplicationException("insert failed");
            }
            return new ContentProviderResult(newUri);
        }

        int numRows = internalDelete(batchOperationConnection, uri, selectionArgs == null ? null : "?", selectionArgs);

        return new ContentProviderResult(numRows);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public IDBSupport getDbSupport() {
        return mDbSupport;
    }

    //helper's methods for batch operations
    public static ContentProviderOperation getDeleteOperation(DataSourceRequest dataSourceRequest, Class<?> clazz) {
        return getDeleteOperation(dataSourceRequest, clazz, null);
    }

    public static ContentProviderOperation getDeleteOperation(DataSourceRequest dataSourceRequest, Class<?> clazz, String selection) {
        return ContentProviderOperation.
                newDelete(ModelContract.getUri(dataSourceRequest, clazz)).
                withSelection("?", selection == null ? null : new String[]{selection}).
                build();
    }
    public static ArrayList<ContentProviderOperation> getContentProviderOperations(DataSourceRequest dataSourceRequest, Class<?> clazz, ContentValues[] array) {
        List<ContentValues> list = new ArrayList<ContentValues>();
        Collections.addAll(list, array);
        return  getContentProviderOperations(dataSourceRequest, clazz, list);
    }

    public static ArrayList<ContentProviderOperation> getContentProviderOperations(DataSourceRequest dataSourceRequest, Class<?> clazz, List<ContentValues> array) {
        ArrayList<ContentProviderOperation> list = new ArrayList<ContentProviderOperation>();
        if (array != null) {
            for (ContentValues contentValues : array) {
                Uri uri = ModelContract.getUri(dataSourceRequest, clazz);
                list.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
            }
        }
        return list;
    }
}
