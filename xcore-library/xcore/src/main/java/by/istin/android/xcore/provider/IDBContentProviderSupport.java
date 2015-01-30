package by.istin.android.xcore.provider;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;

import by.istin.android.xcore.db.IDBSupport;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Uladzimir_Klyshevich
 * Date: 10/15/13
 */
public interface IDBContentProviderSupport {

    //Class<?>[] getEntities();

    String getType(Uri uri);

    int delete(Uri uri, String where, String[] whereArgs);

    Uri insertOrUpdate(Uri uri, ContentValues initialValues);

    int bulkInsertOrUpdate(Uri uri, ContentValues[] values);

    Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

    Context getContext();

    IDBSupport getDbSupport();

    ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException;
}
