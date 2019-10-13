package by.istin.android.xcore.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.XCoreHelper;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Log;

public abstract class DBContentProvider extends ContentProvider {

    private XCoreHelper xCoreHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        xCoreHelper = XCoreHelper.get();
        Log.xd(this, "xCoreHelper onCreate");
        xCoreHelper.onCreate(context, getModules(), getBuildConfigClass());
        return true;
    }

    protected Class<?> getBuildConfigClass() {
        return null;
    }

    protected abstract List<Class<? extends XCoreHelper.Module>> getModules();

    protected String getName() {
        return getContext().getPackageName();
    }

    @Override
    public final String getType(Uri uri) {
        return xCoreHelper.getContentProvider(getName()).getType(uri);
    }

    @Override
    public final int delete(Uri uri, String where, String[] whereArgs) {
        return xCoreHelper.getContentProvider(getName()).delete(uri, where, whereArgs);
    }

    @Override
    public final Uri insert(Uri uri, ContentValues initialValues) {
        return xCoreHelper.getContentProvider(getName()).insertOrUpdate(uri, initialValues);
    }

    @Override
    public final int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        return xCoreHelper.getContentProvider(getName()).bulkInsertOrUpdate(uri, values);
    }

    @Override
    public final Cursor query(Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder) {
        return xCoreHelper.getContentProvider(getName()).query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public final int update(Uri uri, ContentValues initialValues, String where,
                            String[] whereArgs) {
        throw new UnsupportedOperationException("unsupported operation, please use insert method");
    }

    @Override
    public final ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        return xCoreHelper.getContentProvider(getName()).applyBatch(operations);
    }

    public static IDBConnection getWritableConnection(Context context) {
        IDBContentProviderSupport dbContentProvider = AppUtils.get(context, XCoreHelper.getContentProviderKey(context.getPackageName()));
        return dbContentProvider.getDbSupport().getConnector().getWritableConnection();
    }

    public static IDBConnection getReadableConnection(Context context) {
        IDBContentProviderSupport dbContentProvider = AppUtils.get(context, XCoreHelper.getContentProviderKey(context.getPackageName()));
        return dbContentProvider.getDbSupport().getConnector().getReadableConnection();
    }

    public static DBHelper getDBHelper(Context context) {
        IDBContentProviderSupport dbContentProvider = AppUtils.get(context, XCoreHelper.getContentProviderKey(context.getPackageName()));
        return dbContentProvider.getDbSupport().getDBHelper();
    }

}