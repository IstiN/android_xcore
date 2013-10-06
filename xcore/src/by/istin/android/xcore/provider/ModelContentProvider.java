package by.istin.android.xcore.provider;

import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.provider.ModelContract.ModelColumns;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.source.SyncDataSourceRequestEntity;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;

public abstract class ModelContentProvider extends ContentProvider {

    public static final String OLD_APP_VERSION = "oldAppVersion";
    private static UriMatcher sUriMatcher;

	private static final int MODELS = 1;

	private static final int MODELS_ID = 2;
	
	private static final int MODELS_ID_NEGOTIVE = 3;

	private static DBHelper sDbHelper;

    private volatile Boolean isLock = false;

    private static volatile Object mLock = new Object();

    @Override
	public boolean onCreate() {
        initUriMatcher();
        Log.init(getContext());
        Context context = ContextHolder.getInstance().getContext();
        if (context == null) {
            ContextHolder.getInstance().setContext(getContext());
        }
        synchronized (mLock) {
            if (sDbHelper != null) {
                //check for only one instance of helper
                //2.3 android issue, we can have 2 calls of this method
                return true;
            }
            sDbHelper = new DBHelper(getContext());
            if (Log.isOff) {
                int currentAppVersion = getAppVersion();
                int oldAppVersion = PreferenceHelper.getInt(OLD_APP_VERSION, 0);
                if (currentAppVersion == oldAppVersion) {
                    //return true;
                } else {
                    PreferenceHelper.set(OLD_APP_VERSION, currentAppVersion);
                    onUpgrade(oldAppVersion, currentAppVersion);
                }
            }
            Class<?>[] dbEntities = getDbEntities();
            sDbHelper.createTablesForModels(DataSourceRequestEntity.class);
            sDbHelper.createTablesForModels(SyncDataSourceRequestEntity.class);
            sDbHelper.createTablesForModels(dbEntities);
            return true;
        }
	}

    protected void onUpgrade(int oldAppVersion, int currentAppVersion) {
        Log.xd(this, "onUpgrade: from "+oldAppVersion + " to " + currentAppVersion);
    }

    public int getAppVersion() {
        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //can be ignored
        }
        return 0;
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

    public abstract Class<?>[] getDbEntities();

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
        synchronized (mLock) {
            Log.xd(this, " contentprovider " + this);
            return deleteWithoutLockCheck(sDbHelper, uri, where, whereArgs, true);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        synchronized (mLock) {
            return insertWithoutLockCheck(sDbHelper, uri, initialValues, true);
        }
    }

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		String className = uri.getLastPathSegment();
		try {
            synchronized (mLock) {
                return bulkInsert(uri, values, className, sDbHelper);
            }
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return queryWithoutLock(uri, projection, selection, selectionArgs, sortOrder);
    }

    //TODO refactoring query parameters to path segments
    private Cursor queryWithoutLock(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
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
                    selection = ModelColumns._ID + " = " + uri.getLastPathSegment();
                } else {
                    selection = selection + ModelColumns._ID + " = " + uri.getLastPathSegment();
                }
                break;
            case MODELS_ID_NEGOTIVE:
                pathSegments = uri.getPathSegments();
                className = pathSegments.get(pathSegments.size()-2);
                if (StringUtil.isEmpty(selection)) {
                    selection = ModelColumns._ID + " = " + uri.getLastPathSegment();
                } else {
                    selection = selection + ModelColumns._ID + " = " + uri.getLastPathSegment();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (ModelContract.isSqlUri(className)) {
            Cursor c = sDbHelper.rawQuery(ModelContract.getSqlParam(uri), selectionArgs);
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
            try {
                String offsetParameter = uri.getQueryParameter("offset");
                String sizeParameter = uri.getQueryParameter("size");
                String limitParam = null;
                if (!StringUtil.isEmpty(offsetParameter) && !StringUtil.isEmpty(sizeParameter)) {
                    limitParam = StringUtil.format("%s,%s",offsetParameter, sizeParameter);
                }
                Cursor c = sDbHelper.query(Class.forName(className), projection, selection, selectionArgs, null, null, sortOrder, limitParam);
                if (c != null) {
                    c.setNotificationUri(getContext().getContentResolver(), uri);
                    c.getCount();
                    c.moveToFirst();
                }
                return c;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }


    private int bulkInsert(Uri uri, ContentValues[] values, String className, DBHelper helper) throws ClassNotFoundException {
        int count = helper.updateOrInsert(getDataSourceRequestFromUri(uri), Class.forName(className), values);
        if (count > 0) {
            if (ModelContract.isNotify(uri)) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return count;
    }

    public static DataSourceRequest getDataSourceRequestFromUri(Uri uri) {
		String dataSourceRequest = ModelContract.getDataSourceRequest(uri);;
		if (!StringUtil.isEmpty(dataSourceRequest)) {
			return DataSourceRequest.fromUri(Uri.parse("content://temp?"+StringUtil.decode(dataSourceRequest)));
		}
		return null;
	}

    private int deleteWithoutLockCheck(DBHelper helper, Uri uri, String where, String[] whereArgs, boolean isNotify) {
        List<String> pathSegments = uri.getPathSegments();
        String className = StringUtil.EMPTY;
        switch (sUriMatcher.match(uri)) {
        case MODELS:
            className = pathSegments.get(pathSegments.size()-1);
            break;
        case MODELS_ID:
            className = pathSegments.get(pathSegments.size()-2);
            if (where == null) {
                where = StringUtil.EMPTY;
            }
            where = where + ModelColumns._ID + " = " + uri.getLastPathSegment();
            break;
        case MODELS_ID_NEGOTIVE:
            className = pathSegments.get(pathSegments.size()-2);
            if (where == null) {
                where = StringUtil.EMPTY;
            }
            where = where + ModelColumns._ID + " = " + uri.getLastPathSegment();
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            int count = helper.delete(Class.forName(className), where, whereArgs);
            if (ModelContract.isNotify(uri) && isNotify) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return count;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private Uri insertWithoutLockCheck(DBHelper helper, Uri uri, ContentValues initialValues, boolean isNotify) {
        if (sUriMatcher.match(uri) != MODELS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String className = uri.getLastPathSegment();
        try {
            DataSourceRequest dataSourceRequestFromUri = getDataSourceRequestFromUri(uri);
            Class<?> classOfModel = Class.forName(className);
            long rowId = helper.updateOrInsert(dataSourceRequestFromUri, classOfModel, initialValues);
            if (rowId != -1l) {
                Uri serializableModelUri = ContentUris.withAppendedId(uri, rowId);
                if (ModelContract.isNotify(uri) && isNotify) {
                    getContext().getContentResolver().notifyChange(
                            serializableModelUri, null);
                }
                return serializableModelUri;
            } else {
                throw new IllegalArgumentException(uri + ": " + initialValues.toString());
            }
            //TODO need check throw new SQLException("Failed to insert row into " + uri);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
	public int update(Uri uri, ContentValues initialValues, String where,
			String[] whereArgs) {
		throw new UnsupportedOperationException("unsupported operation, please use insert method");
	}


	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
        synchronized (isLock) {
            isLock = true;
        }
        try {
            synchronized (mLock) {
                isLock = true;
                sDbHelper.lockTransaction();
                ContentProviderResult[] result = new ContentProviderResult[operations.size()];
                try {
                    Set<Uri> set = new HashSet<Uri>();
                    for(int i = 0; i < operations.size(); i++) {
                        ContentProviderOperation contentProviderOperation = operations.get(i);
                        Uri uri = contentProviderOperation.getUri();
                        result[i] = apply(contentProviderOperation, result, i);
                        set.add(uri);
                    }
                    sDbHelper.unlockTransaction();
                    for (Iterator<Uri> iterator = set.iterator(); iterator.hasNext(); ) {
                        Uri uri = iterator.next();
                        getContext().getContentResolver().notifyChange(Uri.parse(uri.toString().split("\\?")[0]), null);
                    }
                } catch (OperationApplicationException e1) {
                    sDbHelper.errorUnlockTransaction();
                    throw e1;
                } catch (Exception e) {
                    sDbHelper.errorUnlockTransaction();
                    throw new IllegalArgumentException(e);
                }
                return result;
            }
        } finally {
            synchronized (isLock) {
                isLock = false;
            }
        }
    }


    public ContentProviderResult apply(ContentProviderOperation contentProviderOperation, ContentProviderResult[] backRefs,
                                       int numBackRefs) throws OperationApplicationException {
        ContentValues values = contentProviderOperation.resolveValueBackReferences(backRefs, numBackRefs);
        String[] selectionArgs = contentProviderOperation.resolveSelectionArgsBackReferences(backRefs, numBackRefs);
        Uri mUri = contentProviderOperation.getUri();
        if (values != null) {
            Uri newUri = insertWithoutLockCheck(sDbHelper, mUri, values, false);
            if (newUri == null) {
                throw new OperationApplicationException("insert failed");
            }
            return new ContentProviderResult(newUri);
        }

        int numRows = deleteWithoutLockCheck(sDbHelper, mUri, "?", selectionArgs, false);

        return new ContentProviderResult(numRows);
    }
	
}