package by.istin.android.xcore.db.impl.sqlite;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;

/**
 * Created with IntelliJ IDEA.
 * User: IstiN
 * Date: 18.10.13
 */
public class SQLiteConnector extends SQLiteOpenHelper implements IDBConnector {

    private static final String TAG = SQLiteConnector.class.getSimpleName();

    private static final String DATABASE_NAME_TEMPLATE = "%s.main.xcore.db";

    private static final int DATABASE_VERSION = 1;

    /**
     * The Constant CREATE_TABLE_SQL.
     */
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS  %1$s  ("
            + BaseColumns._ID + " INTEGER PRIMARY KEY ASC)";


    public static final String CREATE_INDEX_SQL = "CREATE INDEX fk_%1$s_%2$s ON %1$s (%2$s ASC);";

    public static final String CREATE_COLUMN_SQL = "ALTER TABLE %1$s ADD %2$s %3$s;";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public SQLiteConnector(Context context, String name) {
        super(context, StringUtil.format(DATABASE_NAME_TEMPLATE, name), null, DATABASE_VERSION);
        if (UiUtil.hasJellyBean()) {
            setWriteAheadLoggingEnabled(true);
        }
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
        if (writableDatabase != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1 && !UiUtil.hasJellyBean()) {
                writableDatabase.setLockingEnabled(false);
                writableDatabase.enableWriteAheadLogging();
            }
        }
        return writableDatabase;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase readableDatabase = super.getReadableDatabase();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1 && !UiUtil.hasJellyBean()) {
            readableDatabase.setLockingEnabled(false);
        }
        return readableDatabase;
    }

    @Override
    public IDBConnection getWritableConnection() {
        return new SQLiteConnection(getWritableDatabase());
    }

    @Override
    public IDBConnection getReadableConnection() {
        return new SQLiteConnection(getReadableDatabase());
    }

    @Override
    public String getCreateTableSQLTemplate(String table) {
        return StringUtil.format(CREATE_TABLE_SQL, table);
    }

    @Override
    public String getCreateIndexSQLTemplate(String table, String name) {
        return StringUtil.format(CREATE_INDEX_SQL, table, name);
    }

    @Override
    public String getCreateColumnSQLTemplate(String table, String name, String type) {
        return StringUtil.format(CREATE_COLUMN_SQL, table, name, type);
    }

}
