package by.istin.android.xcore.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import by.istin.android.xcore.XCoreHelper;

/**
 * Created by IstiN on 24.01.2015.
 */
public class XCursorModelLoader<T extends CursorModel> extends AsyncTaskLoader<T> {

    private final ForceLoadContentObserver mObserver;

    private String mContentProviderName;
    private Uri mUri;
    private String[] mProjection;
    private String mSelection;
    private String[] mSelectionArgs;
    private CursorModel.CursorModelCreator<T> mCursorModelCreator;
    private String mSortOrder;
    private T mCursor;

    /* Runs on a worker thread */
    @Override
    public T loadInBackground() {
        Cursor cursor = XCoreHelper.get().getContentProvider(mContentProviderName).query(mUri, mProjection, mSelection,
                mSelectionArgs, mSortOrder);
        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        if (cursor != null || mCursorModelCreator instanceof CursorModel.CursorModelCreator.NullSupport) {
            T cursorModel = mCursorModelCreator.create(cursor);
            cursorModel.doInBackground(getContext());
            return cursorModel;
        }
        return null;
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(T cursor) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (cursor != null) {
                cursor.close();
            }
            return;
        }
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    /**
     * Creates an empty unspecified CursorLoader.  You must follow this with
     * calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc
     * to specify the query to perform.
     */
    public XCursorModelLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Creates a fully-specified CursorLoader.  See
     * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    public XCursorModelLoader(Context context, CursorModel.CursorModelCreator<T> cursorModelCreator, String contentProviderName, Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder) {
        this(context);
        mContentProviderName = contentProviderName;
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
        mCursorModelCreator = cursorModelCreator;
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     * <p/>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(T cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }

    public CursorModel.CursorModelCreator<T> getCursorModelCreator() {
        return mCursorModelCreator;
    }

    public void setCursorModelCreator(CursorModel.CursorModelCreator<T> mCursorModelCreator) {
        this.mCursorModelCreator = mCursorModelCreator;
    }
}
