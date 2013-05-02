package by.istin.android.xcore.provider;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.provider.ModelContract.ModelColumns;
import by.istin.android.xcore.utils.StringUtil;

public abstract class ModelContentProvider extends ContentProvider {

	private UriMatcher mUriMatcher;

	private static final int MODELS = 1;

	private static final int MODELS_ID = 2;

	private DBHelper dbHelper;
	
	@Override
	public boolean onCreate() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		String authority = ModelContract.getAuthority(getContext());
		mUriMatcher.addURI(authority, "*", MODELS);
		mUriMatcher.addURI(authority, "*/#", MODELS_ID);
		dbHelper = new DBHelper(getContext());
		return true;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		String className = uri.getLastPathSegment();
		try {
			int count = dbHelper.updateOrInsert(Class.forName(className), values);
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
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
		List<String> pathSegments = uri.getPathSegments();
		String className = StringUtil.EMPTY;
		switch (mUriMatcher.match(uri)) {
		case MODELS:
			className = pathSegments.get(pathSegments.size()-1);
			break;
		case MODELS_ID:
			className = pathSegments.get(pathSegments.size()-2);
			where = where + ModelColumns._ID + " = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		try {
			int count = dbHelper.delete(Class.forName(className), where, whereArgs);
			getContext().getContentResolver().notifyChange(uri, null);
			return count;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (mUriMatcher.match(uri) != MODELS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String className = uri.getLastPathSegment();
		try {
			long rowId = dbHelper.updateOrInsert(null, Class.forName(className), initialValues);
			if (rowId > 0) {
				Uri serializableModelUri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(
						serializableModelUri, null);
				return serializableModelUri;
			}
			throw new SQLException("Failed to insert row into " + uri);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public int update(Uri uri, ContentValues initialValues, String where,
			String[] whereArgs) {
		throw new UnsupportedOperationException("unsypported operation, please use insert method");
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (mUriMatcher.match(uri)) {
		case MODELS:
			break;
		case MODELS_ID:
			selection = selection + ModelColumns._ID + " = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String className = uri.getLastPathSegment();
		try {
			Cursor c = dbHelper.query(Class.forName(className), projection, selection, selectionArgs, null,
					null, sortOrder, String.format("%s,%s",uri.getQueryParameter("offset"), uri.getQueryParameter("size")));
			if (c != null) {
				c.setNotificationUri(getContext().getContentResolver(), uri);
			}
			return c;	
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
}