package by.istin.android.xcore.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.provider.ModelContract.ModelColumns;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.DataSourceRequestEntity;
import by.istin.android.xcore.utils.StringUtil;

public abstract class ModelContentProvider extends ContentProvider {

	private UriMatcher mUriMatcher;

	private static final int MODELS = 1;

	private static final int MODELS_ID = 2;
	
	private static final int MODELS_ID_NEGOTIVE = 3;

	private DBHelper mDbHelper;

    private volatile Boolean isLock = false;

    private volatile Object mLock = new Object();

	@Override
	public boolean onCreate() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		String authority = ModelContract.getAuthority(getContext());
		mUriMatcher.addURI(authority, "*", MODELS);
		mUriMatcher.addURI(authority, "*/#", MODELS_ID);
		//for negotive number
		mUriMatcher.addURI(authority, "*/*", MODELS_ID_NEGOTIVE);
		mDbHelper = new DBHelper(getContext());
		Class<?>[] dbEntities = getDbEntities();
		mDbHelper.createTablesForModels(DataSourceRequestEntity.class);
		mDbHelper.createTablesForModels(dbEntities);
		return true;
	}

	public abstract Class<?>[] getDbEntities();
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		String className = uri.getLastPathSegment();
		try {
			String cleanerParameter = uri.getQueryParameter(ModelContract.PARAM_CLEANER);
            synchronized (mLock) {
                return bulkInsert(uri, values, className, cleanerParameter, mDbHelper);
            }
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

    private int bulkInsert(Uri uri, ContentValues[] values, String className, String cleanerParameter, DBHelper helper) throws ClassNotFoundException {
        int count = helper.updateOrInsert(getDataSourceRequestFromUri(uri), !StringUtil.isEmpty(cleanerParameter), Class.forName(className), values);
        if (count > 0) {
            if (StringUtil.isEmpty(uri.getQueryParameter(ModelContract.PARAM_NOT_NOTIFY_CHANGES))) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return count;
    }

    public static DataSourceRequest getDataSourceRequestFromUri(Uri uri) {
		String parameter = uri.getQueryParameter(ModelContract.DATA_SOURCE_REQUEST_PARAM);
		if (!StringUtil.isEmpty(parameter)) {
			return DataSourceRequest.fromUri(Uri.parse("content://temp?"+StringUtil.decode(parameter)));
		}
		return null;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case MODELS:
			return ModelContract.getContentType(uri.getLastPathSegment());
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        synchronized (mLock) {
            return deleteWithoutLockCheck(mDbHelper, uri, where, whereArgs, true);
        }
	}

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        synchronized (mLock) {
            return insertWithoutLockCheck(mDbHelper, uri, initialValues, true);
        }
    }

    private int deleteWithoutLockCheck(DBHelper helper, Uri uri, String where, String[] whereArgs, boolean isNotify) {
        List<String> pathSegments = uri.getPathSegments();
        String className = StringUtil.EMPTY;
        switch (mUriMatcher.match(uri)) {
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
            if (StringUtil.isEmpty(uri.getQueryParameter(ModelContract.PARAM_NOT_NOTIFY_CHANGES)) && isNotify) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return count;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Uri insertWithoutLockCheck(DBHelper helper, Uri uri, ContentValues initialValues, boolean isNotify) {
        if (mUriMatcher.match(uri) != MODELS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        String className = uri.getLastPathSegment();
        try {
            String cleanerParameter = uri.getQueryParameter(ModelContract.PARAM_CLEANER);
            DataSourceRequest dataSourceRequestFromUri = getDataSourceRequestFromUri(uri);
            boolean withCleaner = !StringUtil.isEmpty(cleanerParameter);
            Class<?> classOfModel = Class.forName(className);
            long rowId = helper.updateOrInsert(dataSourceRequestFromUri, withCleaner, classOfModel, initialValues);
            if (rowId != -1l) {
                Uri serializableModelUri = ContentUris.withAppendedId(uri, rowId);
                if (StringUtil.isEmpty(uri.getQueryParameter(ModelContract.PARAM_NOT_NOTIFY_CHANGES)) && isNotify) {
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
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String className = null;
		List<String> pathSegments = null;
		switch (mUriMatcher.match(uri)) {
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
		if (className.equals(ModelContract.SEGMENT_RAW_QUERY)) {
			Cursor c = mDbHelper.rawQuery(uri.getQueryParameter(ModelContract.SQL_PARAM), selectionArgs);
			if (c != null) {
				c.getCount();
				c.moveToFirst();
			}
			String encodedUri = uri.getQueryParameter(ModelContract.OBSERVER_URI_PARAM);
			if (!StringUtil.isEmpty(encodedUri)) {
				c.setNotificationUri(getContext().getContentResolver(), Uri.parse(StringUtil.decode(encodedUri)));
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
				Cursor c = mDbHelper.query(Class.forName(className), projection, selection, selectionArgs, null, null, sortOrder, limitParam);
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
                mDbHelper.lockTransaction();
                ContentProviderResult[] result = new ContentProviderResult[operations.size()];
                try {
                    Set<Uri> set = new HashSet<Uri>();
                    for(int i = 0; i < operations.size(); i++) {
                        ContentProviderOperation contentProviderOperation = operations.get(i);
                        Uri uri = contentProviderOperation.getUri();
                        result[i] = apply(contentProviderOperation, result, i);
                        set.add(uri);
                    }
                    mDbHelper.unlockTransaction();
                    for (Iterator<Uri> iterator = set.iterator(); iterator.hasNext(); ) {
                        Uri uri = iterator.next();
                        getContext().getContentResolver().notifyChange(Uri.parse(uri.toString().split("\\?")[0]), null);
                    }
                } catch (OperationApplicationException e1) {
                    mDbHelper.errorUnlockTransaction();
                    throw e1;
                } catch (Exception e) {
                    mDbHelper.errorUnlockTransaction();
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
            Uri newUri = insertWithoutLockCheck(mDbHelper, mUri, values, false);
            if (newUri == null) {
                throw new OperationApplicationException("insert failed");
            }
            return new ContentProviderResult(newUri);
        }

        int numRows = deleteWithoutLockCheck(mDbHelper, mUri, "?", selectionArgs, false);

        return new ContentProviderResult(numRows);
    }
	
}