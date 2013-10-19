package by.istin.android.xcore.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.CursorLoader;
import by.istin.android.xcore.utils.Log;

/**
 * Created by Uladzimir_Klyshevich on 8/30/13.
 */
public class CursorModelLoader extends CursorLoader {
    final CursorModel.CursorModelCreator mCursorModelCreator;

    public static interface ILoading {

        void onCursorLoaderStartLoading();

        void onCursorLoaderStopLoading();

    }

    private ILoading mLoading;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {
        Cursor cursor = super.loadInBackground();
        if (cursor != null) {
            cursor = mCursorModelCreator.create(cursor);
        }
        return cursor;
    }

    public CursorModelLoader(Context context, ILoading loading) {
        this(context, CursorModel.CursorModelCreator.DEFAULT, loading);
    }

    /**
     * Creates an empty unspecified CursorLoader.  You must follow this with
     * calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc
     * to specify the query to perform.
     */
    public CursorModelLoader(Context context, CursorModel.CursorModelCreator cursorModelCreator, ILoading loading) {
        super(context);
        this.mCursorModelCreator = cursorModelCreator;
        this.mLoading = loading;
    }

    public CursorModelLoader(Context context, ILoading loading, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        this(context, CursorModel.CursorModelCreator.DEFAULT, loading, uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Creates a fully-specified CursorLoader.  See
     * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)
     * ContentResolver.query()} for documentation on the meaning of the
     * parameters.  These will be passed as-is to that call.
     */
    public CursorModelLoader(Context context, CursorModel.CursorModelCreator cursorModelCreator, ILoading loading, Uri uri, String[] projection, String selection,
                             String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        this.mCursorModelCreator = cursorModelCreator;
        this.mLoading = loading;
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.xd(this, "onForceLoad");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sendStartLoadingEvent();
            }
        });
    }

    @Override
    protected Cursor onLoadInBackground() {
        Log.xd(this, "onLoadInBackground");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sendStartLoadingEvent();
            }
        });
        return super.onLoadInBackground();
    }

    @Override
    public void onContentChanged() {
        Log.xd(this, "onContentChanged");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sendStartLoadingEvent();
            }
        });
        super.onContentChanged();
    }

    @Override
    public void forceLoad() {
        Log.xd(this, "forceLoad");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sendStartLoadingEvent();
            }
        });
        super.forceLoad();
    }

    @Override
    protected void onStartLoading() {
        Log.xd(this, "onStartLoading");
        super.onStartLoading();
        sendStartLoadingEvent();
    }

    private void sendStartLoadingEvent() {
        if (mLoading != null) {
            mLoading.onCursorLoaderStartLoading();
        }
    }

    @Override
    protected void onStopLoading() {
        Log.xd(this, "onStopLoading");
        super.onStopLoading();
        sendEndLoadingEvent();
    }

    private void sendEndLoadingEvent() {
        if (mLoading != null) {
            mLoading.onCursorLoaderStopLoading();
        }
    }
}