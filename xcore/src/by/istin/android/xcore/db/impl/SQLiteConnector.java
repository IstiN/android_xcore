package by.istin.android.xcore.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 18.10.13
 */
public class SQLiteConnector extends SQLiteOpenHelper implements IDBConnector {

    private static final String TAG = SQLiteConnector.class.getSimpleName();

    private static final String DATABASE_NAME_TEMPLATE = "%s.main.xcore.db";

    private static final int DATABASE_VERSION = 1;

    /** The Constant CREATE_TABLE_SQL. */
    public static final String CREATE_FILES_TABLE_SQL = "CREATE TABLE IF NOT EXISTS  %1$s  ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY ASC)";

    public static final String FOREIGN_KEY_TEMPLATE = "ALTER TABLE %1$s ADD CONSTRAINT fk_%1$s_%2$s " +
            " FOREIGN KEY (%3$s_id) " +
            " REFERENCES %2$s(id);";

    public SQLiteConnector(Context context) {
        super(context, StringUtil.format(DATABASE_NAME_TEMPLATE, context.getPackageName()), null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase writableDatabase = super.getWritableDatabase();
        if (Build.VERSION.SDK_INT > 7 && Build.VERSION.SDK_INT < 16) {
            if (writableDatabase != null) {
                writableDatabase.setLockingEnabled(false);
            }
        }
        if (Build.VERSION.SDK_INT > 10) {
            writableDatabase.enableWriteAheadLogging();
        }
        return writableDatabase;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase readableDatabase = super.getReadableDatabase();
        if (Build.VERSION.SDK_INT > 7 && Build.VERSION.SDK_INT < 16) {
            readableDatabase.setLockingEnabled(false);
        }
        if (Build.VERSION.SDK_INT > 10) {
            readableDatabase.enableWriteAheadLogging();
        }
        return readableDatabase;
    }

    @Override
    public IDBConnection getWritableConnection() {
        return new SQLiteConnection(getReadableDatabase());  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IDBConnection getReadableConnection() {
        return new SQLiteConnection(getWritableDatabase());
    }

    @Override
    public String getCreateFilesTableSQLTemplate(String table) {
        return StringUtil.format(CREATE_FILES_TABLE_SQL, table);
    }

    @Override
    public void execSQL(String sql) {

    }

    @Override
    public void beginTransaction() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTransactionSuccessful() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void endTransaction() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int delete(String className, String where, String[] whereArgs) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long updateOrInsert(DataSourceRequest dataSourceRequest, String className, ContentValues initialValues) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
