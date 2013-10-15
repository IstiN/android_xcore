package by.istin.android.xcore.test.db;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ApplicationTestCase;
import android.util.Log;
import by.istin.android.xcore.db.impl.SQLiteSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.test.bo.SubEntity;
import by.istin.android.xcore.test.bo.TestEntity;
import by.istin.android.xcore.utils.CursorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TestSQLiteSupport extends ApplicationTestCase<Application> {

    private static final String TEST_ENTITY_CLASS = TestEntity.class.getCanonicalName();

    private static final String SUB_ENTITY_CLASS = SubEntity.class.getCanonicalName();
    public static final int THREAD_COUNT = 20;

    private static DataSourceRequest DATA_SOURCE_REQUEST = new DataSourceRequest("http://anyurl.com/api");

    private SQLiteSupport mSQLiteSupport;

    public TestSQLiteSupport() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
        mSQLiteSupport = new SQLiteSupport();
        mSQLiteSupport.create(getApplication(), new Class[]{TestEntity.class, SubEntity.class});
	}

    public void testInsert() throws Exception {
        createAndClearTables();

        insertOneEntity();

        checkResults(1);
    }

    private void insertOneEntity() {
        ContentValues contentValues = MockStorage.generateSingleEntity(0);
        mSQLiteSupport.updateOrInsert(DATA_SOURCE_REQUEST, TEST_ENTITY_CLASS, contentValues);
    }

    public void testDelete() throws Exception {
        testBulkInsert();

        deleteAll();

        checkResults(0);
    }

    private void deleteAll() {
        mSQLiteSupport.delete(TEST_ENTITY_CLASS, null, null);
        mSQLiteSupport.delete(SUB_ENTITY_CLASS, null, null);
    }

    public void testDeleteWithCondition() throws Exception {
        testBulkInsert();

        deleteWithCondition();

        checkResults(MockStorage.SIZE-1);
    }

    private void deleteWithCondition() {
        mSQLiteSupport.delete(TEST_ENTITY_CLASS, TestEntity.ID + "= ?", new String[]{"0"});
        mSQLiteSupport.delete(SUB_ENTITY_CLASS, SubEntity.ID + "= ?", new String[]{"0"});
    }

    public void testBulkInsert() throws Exception {
        createAndClearTables();
        bulkInsertTestEntity();
        checkResults(MockStorage.SIZE);
	}

    private void bulkInsertTestEntity() {
        ContentValues[] contentValues = MockStorage.generateArray();
        mSQLiteSupport.updateOrInsert(DATA_SOURCE_REQUEST, TEST_ENTITY_CLASS, contentValues);
    }

    private void checkResults(int count) {
        Cursor cursor = querySubEntity();
        if (count == 0) {
            assertTrue(CursorUtils.isEmpty(cursor));
        } else {
            assertEquals(count, cursor.getCount());
        }
        CursorUtils.close(cursor);

        cursor = queryTestEntity();
        if (count == 0) {
            assertTrue(CursorUtils.isEmpty(cursor));
        } else {
            assertEquals(count, cursor.getCount());
        }
        CursorUtils.close(cursor);
    }

    private Cursor querySubEntity() {
        return mSQLiteSupport.query(SUB_ENTITY_CLASS, new String[]{SubEntity.ID}, null, null, null, null, null, null);
    }

    private Cursor queryTestEntity() {
        return mSQLiteSupport.query(TEST_ENTITY_CLASS, new String[]{TestEntity.ID}, null, null, null, null, null, null);
    }

    private void createAndClearTables() {
        mSQLiteSupport.delete(SUB_ENTITY_CLASS, null, null);
        mSQLiteSupport.delete(TEST_ENTITY_CLASS, null, null);
    }

    public void testThreadSafe() throws Exception {
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT*10);
        //10 operations
        List<Runnable> operations = new ArrayList<Runnable>();
        operations.add(new Runnable() {
            @Override
            public void run() {
                bulkInsertTestEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                insertOneEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                deleteAll();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                deleteWithCondition();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                querySubEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                queryTestEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                querySubEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                queryTestEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                querySubEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                queryTestEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });

        int size = operations.size();
        for (int i = 0; i < THREAD_COUNT*size; i++) {
            final int threadNumber = i;
            Runnable runner = operations.get(i % size);
            new Thread(runner, "TestThread"+i).start();
        }
        /* all threads are waiting on the latch. */
        latch.await(); // release the latch
        // all threads are now running concurrently.
    }

}
