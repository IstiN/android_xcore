package by.istin.android.xcore.test.db;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ApplicationTestCase;
import by.istin.android.xcore.db.DBHelper;
import by.istin.android.xcore.db.IDBConnector;
import by.istin.android.xcore.db.impl.sqlite.SQLiteSupport;
import by.istin.android.xcore.test.bo.SubEntity;
import by.istin.android.xcore.test.bo.TestEntity;
import by.istin.android.xcore.utils.CursorUtils;

public class TestDbHelper extends ApplicationTestCase<Application> {

    private DBHelper dbHelper;

    public TestDbHelper() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
        IDBConnector connector = new SQLiteSupport().createConnector(getApplication());
        dbHelper = new DBHelper(connector);
	}

    public void testInsert() throws Exception {
        ContentValues contentValues = MockStorage.generateSingleEntity(0);

        createAndClearTables();

        dbHelper.updateOrInsert(TestEntity.class, contentValues);

        checkResults(1);
    }

    public void testDelete() throws Exception {
        testBulkInsert();

        dbHelper.delete(TestEntity.class, null, null);
        dbHelper.delete(SubEntity.class, null, null);

        checkResults(0);
    }

    public void testDeleteWithCondition() throws Exception {
        testBulkInsert();

        dbHelper.delete(TestEntity.class, TestEntity.ID + "= ?", new String[]{"0"});
        dbHelper.delete(SubEntity.class, SubEntity.ID + "= ?", new String[]{"0"});

        checkResults(MockStorage.SIZE-1);
    }

	public void testBulkInsert() throws Exception {
        ContentValues[] contentValues = MockStorage.generateArray();
        createAndClearTables();

		dbHelper.updateOrInsert(TestEntity.class, contentValues);

        checkResults(MockStorage.SIZE);
	}

    private void checkResults(int count) {
        Cursor cursor = dbHelper.query(SubEntity.class, new String[]{SubEntity.ID}, null, null, null, null, null, null);
        if (count == 0) {
            assertTrue(CursorUtils.isEmpty(cursor));
        } else {
            assertEquals(count, cursor.getCount());
        }
        CursorUtils.close(cursor);

        cursor = dbHelper.query(TestEntity.class, new String[]{TestEntity.ID}, null, null, null, null, null, null);
        if (count == 0) {
            assertTrue(CursorUtils.isEmpty(cursor));
        } else {
            assertEquals(count, cursor.getCount());
        }
        CursorUtils.close(cursor);
    }

    private void createAndClearTables() {
        dbHelper.createTablesForModels(SubEntity.class, TestEntity.class);
        dbHelper.delete(SubEntity.class, null, null);
        dbHelper.delete(TestEntity.class, null, null);
    }

}
