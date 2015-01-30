package by.istin.android.xcore.test.common;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.ApplicationTestCase;

import java.io.InputStream;
import java.util.HashMap;

import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.processor.impl.AbstractGsonProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.StringUtil;

public abstract class AbstractTestProcessor<A extends Application> extends ApplicationTestCase<A> {

    private TestDataSource testDataSource;

    public AbstractTestProcessor(Class<A> applicationClass) {
        super(applicationClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        createApplication();
        testDataSource = new TestDataSource();
    }

    public void clear(Class<?>... entities) {
        for (Class<?> entity : entities) {
            getApplication().getContentResolver().delete(ModelContract.getUri(entity), null, null);
        }
    }

    protected void checkRequiredFields(Class<?> classEntity, String... fields) {
        Uri uri = ModelContract.getUri(classEntity);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        checkRequiredFields(uri, projection, selection, selectionArgs, sortOrder, fields);
    }

    protected void checkRequiredFields(Class<?> classEntity, int countRequiredValues, String... fields) {
        Uri uri = ModelContract.getUri(classEntity);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        checkRequiredFields(uri, projection, selection, selectionArgs, sortOrder, countRequiredValues, fields);
    }

    protected void checkRequiredFields(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String... fields) {
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        CursorModel cursorModel = new CursorModel(cursor);
        for (int i = 0; i < cursorModel.size(); i++) {
            CursorModel entity = cursorModel.get(i);
            for (int j = 0; j < fields.length; j++) {
                String field = fields[j];
                String value = entity.getString(field);
                assertNotNull(field + " is required", value);
                assertFalse(field + " is required", StringUtil.isEmpty(value));
            }
        }
        CursorUtils.close(cursor);
    }

    protected void checkRequiredFields(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int count, String... fields) {
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        CursorModel cursorModel = new CursorModel(cursor);
        HashMap<String, Integer> fieldsCount = new HashMap<String, Integer>();
        for (int i = 0; i < fields.length; i++) {
            fieldsCount.put(fields[i], count);
        }
        for (int i = 0; i < cursorModel.size(); i++) {
            CursorModel entity = cursorModel.get(i);
            for (int j = 0; j < fields.length; j++) {
                String field = fields[j];
                String value = entity.getString(field);
                if (value != null) {
                    fieldsCount.put(field, fieldsCount.get(field) - 1);
                }
            }
        }
        CursorUtils.close(cursor);

        for (int i = 0; i < fields.length; i++) {
            int value = fieldsCount.get(fields[i]);
            assertEquals(fields[i] + " is required, " + value + " null values found", 0, value);
        }
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
            assertTrue(uri + " need to be " + count, count == 0);
        } else {
            assertEquals(count, cursor.getCount());
        }
        CursorUtils.close(cursor);
    }

    public Object testExecute(String processorKey, String feedUri) throws Exception {
        return testExecute(getApplication(), processorKey, feedUri);
    }

    public Object testExecute(Context context, String processorKey, String feedUri) throws Exception {
        IProcessor processor = (IProcessor) AppUtils.get(context, processorKey);
        DataSourceRequest dataSourceRequest = new DataSourceRequest(feedUri);
        InputStream inputStream = testDataSource.getSource(dataSourceRequest, new Holder<Boolean>(false));
        Object executeResult = processor.execute(dataSourceRequest, testDataSource, inputStream);
        processor.cache(context, dataSourceRequest, executeResult);
        return executeResult;
    }
}
