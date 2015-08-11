package by.istin.android.xcore;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;

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

        private QueryBuilder(ContentProviderAdapter pContentProviderAdapter) {
            mContentProviderAdapter = pContentProviderAdapter;
        }

        private Uri mUri;

        private String mTable;

        private String[] mColumns;

        private String mWhere;

        private String mOrder;

        private String mLimit;

        private String[] mWhereArgs;

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
            if (mUri == null && mTable == null) {
                throw new IllegalArgumentException("you need to set uri or table before query");
            }
            Cursor cursor = mContentProviderAdapter.getCursor(this);
            if (!CursorUtils.isEmpty(cursor) && cursor.moveToFirst()) {
                return new CursorModel(cursor);
            }
            return null;
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
            return mContentProviderAdapter.delete(this);
        }
    }

    private static String getSortOrder(QueryBuilder pQueryBuilder) {
        return !StringUtil.isEmpty(pQueryBuilder.mLimit) ?
                pQueryBuilder.mOrder + " " + pQueryBuilder.mLimit :
                pQueryBuilder.mOrder;
    }

    public static QueryBuilder system() {
        final ContentResolver contentResolver = ContextHolder.get().getContentResolver();
        return new QueryBuilder(new ContentProviderAdapter() {

            @Override
            public Cursor getCursor(QueryBuilder pQueryBuilder) {
                return contentResolver.query(
                        pQueryBuilder.mUri,
                        pQueryBuilder.mColumns,
                        pQueryBuilder.mWhere,
                        pQueryBuilder.mWhereArgs,
                        getSortOrder(pQueryBuilder)
                );
            }

            @Override
            public int delete(QueryBuilder pQueryBuilder) {
                return contentResolver.delete(pQueryBuilder.mUri, pQueryBuilder.mWhere, pQueryBuilder.mWhereArgs);
            }

        });
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
        return new QueryBuilder(new ContentProviderAdapter() {

            @Override
            public Cursor getCursor(QueryBuilder pQueryBuilder) {
                String table = getTable(pQueryBuilder);
                return pIDBConnection.query(
                        table,
                        pQueryBuilder.mColumns,
                        pQueryBuilder.mWhere,
                        pQueryBuilder.mWhereArgs,
                        null,
                        null,
                        pQueryBuilder.mOrder,
                        pQueryBuilder.mLimit
                );
            }

            @Override
            public int delete(QueryBuilder pQueryBuilder) {
                String table = getTable(pQueryBuilder);
                return pIDBConnection.delete(table, pQueryBuilder.mWhere, pQueryBuilder.mWhereArgs);
            }

        });
    }

    private static String getTable(QueryBuilder pQueryBuilder) {
        Uri uri = pQueryBuilder.mUri;
        return uri == null ? pQueryBuilder.mTable : uri.getLastPathSegment();
    }

    public static QueryBuilder core(final String name) {
        final IDBConnector connector = XCoreHelper.get().getContentProvider(name).getDbSupport().getConnector();
        return new QueryBuilder(new ContentProviderAdapter() {

            @Override
            public Cursor getCursor(QueryBuilder pQueryBuilder) {
                String table = getTable(pQueryBuilder);
                return connector.getReadableConnection().query(
                        table,
                        pQueryBuilder.mColumns,
                        pQueryBuilder.mWhere,
                        pQueryBuilder.mWhereArgs,
                        null,
                        null,
                        pQueryBuilder.mOrder,
                        pQueryBuilder.mLimit
                );
            }

            @Override
            public int delete(QueryBuilder pQueryBuilder) {
                String table = getTable(pQueryBuilder);
                return connector.getWritableConnection().delete(table, pQueryBuilder.mWhere,
                        pQueryBuilder.mWhereArgs);
            }

        });
    }

}
