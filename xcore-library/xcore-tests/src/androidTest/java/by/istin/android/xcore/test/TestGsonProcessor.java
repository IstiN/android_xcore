package by.istin.android.xcore.test;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ApplicationTestCase;

import java.io.InputStream;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.model.BigTestEntity;
import by.istin.android.xcore.model.BigTestSubEntity;
import by.istin.android.xcore.processor.impl.AbstractGsonBatchProcessor;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.CursorUtils;

public class TestGsonProcessor extends ApplicationTestCase<Application> {

	public TestGsonProcessor() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
	}

	public void testObjectLoad() throws Exception {
        ContextHolder.getInstance().setContext(getApplication());
        IDBContentProviderSupport defaultDBContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(getApplication(), BigTestEntity.class, BigTestSubEntity.class);
        Uri testEntityUri = ModelContract.getUri(BigTestEntity.class);
        defaultDBContentProvider.delete(testEntityUri, null, null);
        Uri testSubEntity = ModelContract.getUri(BigTestSubEntity.class);
        defaultDBContentProvider.delete(testSubEntity, null, null);

		HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://dl.dropboxusercontent.com/u/16403954/xcore/json_object.json");
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);
		inputStream.close();
	}
	
	public void testArrayLoadAndInsert() throws Exception {
        ContextHolder.getInstance().setContext(getApplication());
		HttpAndroidDataSource httpAndroidDataSource = new HttpAndroidDataSource();
		DataSourceRequest dataSourceRequest = new DataSourceRequest("https://dl.dropboxusercontent.com/u/16403954/xcore/json_array_big.json");
		InputStream inputStream = httpAndroidDataSource.getSource(dataSourceRequest);
        IDBContentProviderSupport defaultDBContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(getApplication(), BigTestEntity.class, BigTestSubEntity.class);
        Uri testEntityUri = ModelContract.getUri(BigTestEntity.class);
        defaultDBContentProvider.delete(testEntityUri, null, null);
        Uri testSubEntity = ModelContract.getUri(BigTestSubEntity.class);
        defaultDBContentProvider.delete(testSubEntity, null, null);
        ContentValues[] contentValues = new AbstractGsonBatchProcessor<ContentValues[]>(BigTestEntity.class, ContentValues[].class, defaultDBContentProvider){

            @Override
            public String getAppServiceKey() {
                return "test key";
            }

        }.execute(dataSourceRequest, httpAndroidDataSource, inputStream);
        Cursor entityCursor = defaultDBContentProvider.query(testEntityUri, null, null, null, null);
        //DatabaseUtils.dumpCursor(entityCursor);
        assertEquals(entityCursor.getCount(), 12600);
        Cursor subEntityCursor = defaultDBContentProvider.query(testSubEntity, null, null, null, null);
        //assertEquals(subEntityCursor.getCount(), 16);
        CursorUtils.close(entityCursor);
        CursorUtils.close(subEntityCursor);
    }

}
