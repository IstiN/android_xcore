package by.istin.android.xcore;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.XRecycler;

/**
 * Created by uladzimir_klyshevich on 8/10/15.
 */
public class ContentProvider {

    private interface ContentProviderAdapter {

        Cursor getCursor(QueryBuilder pQueryBuilder);

        int delete(QueryBuilder pQueryBuilder);

    }

    public static final class QueryBuilder {

        private ContentProviderAdapter mContentProviderAdapter;

        private Uri mUri;

        private String mTable;

        private String[] mColumns;

        private String mWhere;

        private String mOrder;

        private String mLimit;

        private String[] mWhereArgs;

        private QueryBuilder() {

        }

        public QueryBuilder uri(Uri pUri) {
            mUri = pUri;
            return this;
        }

        public QueryBuilder limit(String pLimit) {
            mLimit = pLimit;
            return this;
        }

        public QueryBuilder limit(int pLimit) {
            return limit(0, pLimit);
        }

        public QueryBuilder limit(int pStart, int pEnd) {
            return limit(pStart + ", " + pEnd);
        }

        public QueryBuilder where(String pWhere) {
            mWhere = pWhere;
            return this;
        }

        public QueryBuilder table(String pTable) {
            mTable = pTable;
            return this;
        }

        public QueryBuilder whereArgs(Object ... pWhereArgs) {
            if (pWhereArgs != null) {
                mWhereArgs = new String[pWhereArgs.length];
                for (int i = 0; i < mWhereArgs.length; i++) {
                    Object whereArg = pWhereArgs[i];
                    if (whereArg != null) {
                        mWhereArgs[i] = whereArg.toString();
                    }
                }
            }
            return this;
        }

        public QueryBuilder projection(String ... pColumns) {
            mColumns = pColumns;
            return this;
        }

        public QueryBuilder order(String pOrder) {
            mOrder = pOrder;
            return this;
        }

        public QueryBuilder desc(String pColumn) {
            return order(pColumn + " desc");
        }

        public QueryBuilder asc(String pColumn) {
            return order(pColumn + " asc");
        }

        public CursorModel cursor() {
            try {
                if (mUri == null && mTable == null) {
                    throw new IllegalArgumentException("you need to set uri or table before query");
                }
                Cursor cursor = mContentProviderAdapter.getCursor(this);
                if (!CursorUtils.isEmpty(cursor) && cursor.moveToFirst()) {
                    return new CursorModel(cursor);
                }
                return null;
            } finally {
                reset();
            }
        }

        private void reset() {
            mContentProviderAdapter = null;
            mUri = null;
            mTable = null;
            mColumns = null;
            mWhere = null;
            mOrder = null;
            mLimit = null;
            mWhereArgs = null;
            sQBuilderXRecycler.recycled(this);
        }

        public void cursor(final ISuccess<CursorModel> pCursorModelSuccess) {
            final Handler handler = new Handler();
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final CursorModel cursor = cursor();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pCursorModelSuccess.success(cursor);
                        }
                    });
                }
            });
        }

        public int count() {
            CursorModel cursor = cursor();
            try {
                if (cursor == null) {
                    return 0;
                }
                return cursor.size();
            } finally {
                CursorUtils.close(cursor);
            }
        }

        public int delete() {
            try {
                return mContentProviderAdapter.delete(this);
            } finally {
                reset();
            }
        }

        public List<ContentValues> values(CursorUtils.Converter pConverter) {
            CursorModel cursor = cursor();
            if (cursor != null) {
                List<ContentValues> contentValues = new ArrayList<>();
                CursorUtils.convertToContentValuesAndClose(cursor, contentValues, pConverter);
                return contentValues;
            } else {
                return null;
            }
        }

        public List<ContentValues> values() {
            return values(CursorUtils.Converter.get());
        }

        public void values(final ISuccess<List<ContentValues>> pISuccess) {
            values(CursorUtils.Converter.get(), pISuccess);
        }

        public void values(final CursorUtils.Converter pConverter, final ISuccess<List<ContentValues>> pISuccess) {
            final Handler handler = new Handler();
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final List<ContentValues> result = values(pConverter);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            pISuccess.success(result);
                        }
                    });
                }
            });
        }
    }

    private static String getSortOrder(QueryBuilder pQueryBuilder) {
        return !StringUtil.isEmpty(pQueryBuilder.mLimit) ?
                pQueryBuilder.mOrder + " limit " + pQueryBuilder.mLimit :
                pQueryBuilder.mOrder;
    }

    private static class ContentResolverAdapter implements ContentProviderAdapter {

        private ContentResolver mContentResolver;

        @Override
        public Cursor getCursor(QueryBuilder pQueryBuilder) {
            try {
                return mContentResolver.query(
                        pQueryBuilder.mUri,
                        pQueryBuilder.mColumns,
                        pQueryBuilder.mWhere,
                        pQueryBuilder.mWhereArgs,
                        getSortOrder(pQueryBuilder)
                );
            } finally {
                mContentResolver = null;
                sContentResolverAdapterXRecycler.recycled(this);
            }
        }

        @Override
        public int delete(QueryBuilder pQueryBuilder) {
            try {
                return mContentResolver.delete(pQueryBuilder.mUri, pQueryBuilder.mWhere, pQueryBuilder.mWhereArgs);
            } finally {
                mContentResolver = null;
                sContentResolverAdapterXRecycler.recycled(this);
            }
        }

    }

    private static class CoreConnectorAdapter implements ContentProviderAdapter {

            IDBConnector mConnector;

            @Override
            public Cursor getCursor(QueryBuilder pQueryBuilder) {
                try {
                    String table = getTable(pQueryBuilder);
                    return mConnector.getReadableConnection().query(
                            table,
                            pQueryBuilder.mColumns,
                            pQueryBuilder.mWhere,
                            pQueryBuilder.mWhereArgs,
                            null,
                            null,
                            pQueryBuilder.mOrder,
                            pQueryBuilder.mLimit
                    );
                } finally {
                    mConnector = null;
                    sCoreConnectorAdapterXRecycler.recycled(this);
                }
            }

            @Override
            public int delete(QueryBuilder pQueryBuilder) {
                try {
                    String table = getTable(pQueryBuilder);
                    return mConnector.getWritableConnection().delete(table, pQueryBuilder.mWhere,
                            pQueryBuilder.mWhereArgs);
                } finally {
                    mConnector = null;
                    sCoreConnectorAdapterXRecycler.recycled(this);
                }
            }


    }

    private static class CoreConnectionAdapter implements ContentProviderAdapter {

        IDBConnection mConnection;

        @Override
        public Cursor getCursor(QueryBuilder pQueryBuilder) {
            try {
                String table = getTable(pQueryBuilder);
                return mConnection.query(
                        table,
                        pQueryBuilder.mColumns,
                        pQueryBuilder.mWhere,
                        pQueryBuilder.mWhereArgs,
                        null,
                        null,
                        pQueryBuilder.mOrder,
                        pQueryBuilder.mLimit
                );
            } finally {
                mConnection = null;
                sCoreConnectionAdapterXRecycler.recycled(this);
            }
        }

        @Override
        public int delete(QueryBuilder pQueryBuilder) {
            try {
                String table = getTable(pQueryBuilder);
                return mConnection.delete(table, pQueryBuilder.mWhere, pQueryBuilder.mWhereArgs);
            } finally {
                mConnection = null;
                sCoreConnectionAdapterXRecycler.recycled(this);
            }
        }


    }

    private static XRecycler<ContentResolverAdapter> sContentResolverAdapterXRecycler = new XRecycler<>(new XRecycler.RecyclerElementCreator<ContentResolverAdapter>() {
        @Override
        public ContentResolverAdapter createNew(XRecycler pRecycler) {
            return new ContentResolverAdapter();
        }
    });

    private static XRecycler<CoreConnectorAdapter> sCoreConnectorAdapterXRecycler = new XRecycler<>(new XRecycler.RecyclerElementCreator<CoreConnectorAdapter>() {
        @Override
        public CoreConnectorAdapter createNew(XRecycler pRecycler) {
            return new CoreConnectorAdapter();
        }
    });

    private static XRecycler<CoreConnectionAdapter> sCoreConnectionAdapterXRecycler = new XRecycler<>(new XRecycler.RecyclerElementCreator<CoreConnectionAdapter>() {
        @Override
        public CoreConnectionAdapter createNew(XRecycler pRecycler) {
            return new CoreConnectionAdapter();
        }
    });

    private static XRecycler<QueryBuilder> sQBuilderXRecycler = new XRecycler<>(new XRecycler.RecyclerElementCreator<QueryBuilder>() {
        @Override
        public QueryBuilder createNew(XRecycler pRecycler) {
            return new QueryBuilder();
        }
    });

    public static QueryBuilder system() {
        final QueryBuilder queryBuilder = sQBuilderXRecycler.get();
        final ContentResolverAdapter contentResolverAdapter = sContentResolverAdapterXRecycler.get();
        contentResolverAdapter.mContentResolver = ContextHolder.get().getContentResolver();
        queryBuilder.mContentProviderAdapter = contentResolverAdapter;
        return queryBuilder;
    }

    public static IDBConnection readableConnection() {
        return readableConnection(ContextHolder.get().getPackageName());
    }

    public static IDBConnection readableConnection(String name) {
        return XCoreHelper.get().getContentProvider(name).getDbSupport().getConnector().getReadableConnection();
    }

    public static IDBConnection writableConnection() {
        return writableConnection(ContextHolder.get().getPackageName());
    }

    public static IDBConnection writableConnection(String name) {
        return XCoreHelper.get().getContentProvider(name).getDbSupport().getConnector().getWritableConnection();
    }

    public static QueryBuilder core() {
        return core(ContextHolder.get().getPackageName());
    }

    public static QueryBuilder core(final IDBConnection pIDBConnection) {
        final CoreConnectionAdapter coreConnectionAdapter = sCoreConnectionAdapterXRecycler.get();
        coreConnectionAdapter.mConnection = pIDBConnection;
        final QueryBuilder queryBuilder = sQBuilderXRecycler.get();
        queryBuilder.mContentProviderAdapter = coreConnectionAdapter;
        return queryBuilder;
    }

    private static String getTable(QueryBuilder pQueryBuilder) {
        Uri uri = pQueryBuilder.mUri;
        return uri == null ? pQueryBuilder.mTable : uri.getLastPathSegment();
    }

    public static QueryBuilder core(final String name) {
        final IDBConnector connector = XCoreHelper.get().getContentProvider(name).getDbSupport().getConnector();
        final CoreConnectorAdapter coreConnectorAdapter = sCoreConnectorAdapterXRecycler.get();
        coreConnectorAdapter.mConnector = connector;
        final QueryBuilder queryBuilder = sQBuilderXRecycler.get();
        queryBuilder.mContentProviderAdapter = coreConnectorAdapter;
        return queryBuilder;
    }

}
