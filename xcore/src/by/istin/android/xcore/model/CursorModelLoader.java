package by.istin.android.xcore.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by Uladzimir_Klyshevich on 8/30/13.
 */
public class CursorModelLoader extends CursorLoader {
    final CursorModel.CursorModelCreator mCursorModelCreator;


    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = super.loadInBackground();
        if (cursor != null) {
            cursor = mCursorModelCreator.create(cursor);
        }
        return cursor;
    }

    public CursorModelLoader(Context context) {
        this(context, CursorModel.CursorModelCreator.DEFAULT);
    }

    /**
     * Creates an empty unspecified CursorLoader.  You must follow this with
     * calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc
     * to specify the query to perform.
     */
    public CursorModelLoader(Context context, CursorModel.CursorModelCreator cursorModelCreator) {
        super(context);
        this.mCursorModelCreator = cursorModelCreator;
    }

    public CursorModelLoader(Context context, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        this(context, CursorModel.CursorModelCreator.DEFAULT, uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Creates a fully-specified CursorLoader.  See
     * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    public CursorModelLoader(Context context, CursorModel.CursorModelCreator cursorModelCreator, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        this.mCursorModelCreator = cursorModelCreator;
    }

}