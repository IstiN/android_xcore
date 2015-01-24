package by.istin.android.xcore.test.db;

import android.app.Application;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.db.impl.sqlite.SQLiteSupport;
import by.istin.android.xcore.model.BigTestEntity;
import by.istin.android.xcore.model.BigTestSubEntity;
import by.istin.android.xcore.provider.impl.DBContentProviderSupport;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.CursorUtils;

public class TestSQLiteSupport extends ApplicationTestCase<Application> {

    private String TEST_ENTITY_CLASS;

    private String SUB_ENTITY_CLASS;

    public static final int THREAD_COUNT = 20;

    private DataSourceRequest DATA_SOURCE_REQUEST;

    private SQLiteSupport mSQLiteSupport;

    private DBContentProviderSupport dbContentProviderSupport;

    public TestSQLiteSupport() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
        mSQLiteSupport = new SQLiteSupport();
        mSQLiteSupport.create(getApplication(), new Class[]{BigTestEntity.class, BigTestSubEntity.class});
        TEST_ENTITY_CLASS = BigTestEntity.class.getCanonicalName();
        SUB_ENTITY_CLASS = BigTestSubEntity.class.getCanonicalName();
        DATA_SOURCE_REQUEST = new DataSourceRequest("http://anyurl.com/api");
        dbContentProviderSupport = new DBContentProviderSupport(getApplication(), mSQLiteSupport, new Class<?>[]{BigTestEntity.class, BigTestSubEntity.class});
        ContextHolder.set(getApplication());
	}

    public void testInsert() throws Exception {
        createAndClearTables();

        insertOneEntity();

        checkResults(1);
    }

    public void testBatch() throws Exception {
        createAndClearTables();

        ArrayList<ContentProviderOperation> batchInsert = applyInsertBatch();

        checkResults(MockStorage.SIZE);

        ArrayList<ContentProviderOperation> deleteAllBatchOperation = applyDeleteBatch();
        checkResults(0);

        ArrayList<ContentProviderOperation> all = new ArrayList<ContentProviderOperation>();
        all.addAll(batchInsert);
        all.addAll(deleteAllBatchOperation);
        dbContentProviderSupport.applyBatch(all);
        checkResults(0);
    }

    private ArrayList<ContentProviderOperation> applyDeleteBatch() throws OperationApplicationException {
        ArrayList<ContentProviderOperation> deleteAllBatchOperation = getDeleteAllBatchOperation();
        dbContentProviderSupport.applyBatch(deleteAllBatchOperation);
        return deleteAllBatchOperation;
    }

    private ArrayList<ContentProviderOperation> applyInsertBatch() throws OperationApplicationException {
        ArrayList<ContentProviderOperation> batchInsert = getBatchInsert();
        dbContentProviderSupport.applyBatch(batchInsert);
        return batchInsert;
    }

    private void insertOneEntity() {
        ContentValues contentValues = MockStorage.generateSingleEntity(0);
        mSQLiteSupport.updateOrInsert(DATA_SOURCE_REQUEST, TEST_ENTITY_CLASS, contentValues);
    }

    private ArrayList<ContentProviderOperation> getBatchInsert() {
        ContentValues[] contentValueses = MockStorage.generateArray();
        return DBContentProviderSupport.getContentProviderOperations(DATA_SOURCE_REQUEST, BigTestEntity.class, contentValueses);
    }

    private ArrayList<ContentProviderOperation> getDeleteAllBatchOperation() {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        operations.add(DBContentProviderSupport.getDeleteOperation(DATA_SOURCE_REQUEST, BigTestEntity.class));
        operations.add(DBContentProviderSupport.getDeleteOperation(DATA_SOURCE_REQUEST, BigTestSubEntity.class));
        return operations;
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
        mSQLiteSupport.delete(TEST_ENTITY_CLASS, BigTestEntity.ID + "= ?", new String[]{"0"});
        mSQLiteSupport.delete(SUB_ENTITY_CLASS, BigTestSubEntity.ID + "= ?", new String[]{"0"});
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
        Cursor cursor = queryTestEntity();
        if (count == 0) {
            assertTrue(CursorUtils.isEmpty(cursor));
        } else {
            assertEquals(count, cursor.getCount());
        }
        CursorUtils.close(cursor);
    }

    private Cursor queryTestEntity() {
        return mSQLiteSupport.query(TEST_ENTITY_CLASS, new String[]{BigTestEntity.ID}, null, null, null, null, null, null);
    }

    private void createAndClearTables() {
        mSQLiteSupport.delete(SUB_ENTITY_CLASS, null, null);
        mSQLiteSupport.delete(TEST_ENTITY_CLASS, null, null);
    }

    public void testThreadSafe() throws Exception {
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT*8);
        //8 operations
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
                queryTestEntity();
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
                queryTestEntity();
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                try {
                    applyDeleteBatch();
                } catch (OperationApplicationException e) {
                    throw new IllegalStateException(e);
                }
                latch.countDown();
                Log.d("thread_safe", "thread count " + latch.getCount());
            }
        });
        operations.add(new Runnable() {
            @Override
            public void run() {
                try {
                    applyInsertBatch();
                } catch (OperationApplicationException e) {
                    throw new IllegalStateException(e);
                }
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
