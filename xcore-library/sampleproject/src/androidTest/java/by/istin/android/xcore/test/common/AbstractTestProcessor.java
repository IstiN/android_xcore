package by.istin.android.xcore.test.common;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.ApplicationTestCase;

import java.io.InputStream;

import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.provider.DBContentProvider;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.sample.Application;
import by.istin.android.xcore.sample.core.provider.ContentProvider;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.StringUtil;

public class AbstractTestProcessor extends ApplicationTestCase<Application> {

    private TestDataSource testDataSource;

    public AbstractTestProcessor() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        testDataSource = new TestDataSource();
        init();
    }

    private void init() {
        IDBContentProviderSupport dbContentProviderSupport = ContentProvider.getDBContentProviderSupport(getApplication());
        IDBConnection writableConnection = dbContentProviderSupport.getDbSupport().createConnector(getApplication()).getWritableConnection();
        writableConnection.beginTransaction();
        writableConnection.setTransactionSuccessful();
        writableConnection.endTransaction();
    }

    public void clear(Class<?> ... entities) {
        for (Class<?> entity : entities) {
            getApplication().getContentResolver().delete(ModelContract.getUri(entity), null, null);
        }
    }

    protected void checkRequiredFields(Class<?> classEntity, String ... fields) {
        Uri uri = ModelContract.getUri(classEntity);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        checkRequiredFields(uri, projection, selection, selectionArgs, sortOrder, fields);
    }

    protected void checkRequiredFields(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String... fields) {
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        CursorModel cursorModel = new CursorModel(cursor);
        for (int i = 0; i < cursorModel.size(); i++) {
            CursorModel entity = cursorModel.get(i);
            for (int j = 0; j < fields.length; j++) {
                String field = fields[j];
                String value = entity.getString(field);
                assertNotNull(field+ " is required",value);
                assertFalse(field+ " is required", StringUtil.isEmpty(value));
            }
        }
        CursorUtils.close(cursor);
    }

    protected void checkCount(Class<?> entity, int count) {
        Uri uri = ModelContract.getUri(entity);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        checkCount(count, uri, projection, selection, selectionArgs, sortOrder);
    }

    protected void checkCount(int count, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (CursorUtils.isEmpty(cursor)) {
            assertTrue(count != 0);
        } else {
            assertEquals(cursor.getCount(), count);
        }
        CursorUtils.close(cursor);
    }

    public Object testExecute(String processorKey, String feedUri) throws Exception {
        return testExecute(getApplication(), processorKey, feedUri);
    }

    public Object testExecute(Context context, String processorKey, String feedUri) throws Exception {
        IProcessor processor = AppUtils.get(context, processorKey);
        DataSourceRequest dataSourceRequest = new DataSourceRequest(feedUri);
        InputStream inputStream = testDataSource.getSource(dataSourceRequest, new Holder<Boolean>());
        return processor.execute(dataSourceRequest, testDataSource, inputStream);
    }
}
