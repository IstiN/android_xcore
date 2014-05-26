package by.istin.android.xcore.test.provider;

import android.app.Application;
import android.net.Uri;
import android.test.ApplicationTestCase;
import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.model.BigTestEntity;

public class ModelContractTest extends ApplicationTestCase<Application> {

	private static final String SELECT_FROM_TABLE = "SELECT * FROM TABLE";

	public ModelContractTest() {
		super(Application.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
	}

	
	public void testSqlUri() throws Exception {
		Application application = getApplication();
		assertNotNull(application);
		ContextHolder.getInstance().setContext(application);
		final Uri REFRESH_URI = ModelContract.getUri(BigTestEntity.class);
		Uri sqlQueryUri = ModelContract.getSQLQueryUri(SELECT_FROM_TABLE, REFRESH_URI);
		String sqlParam = ModelContract.getSqlParam(sqlQueryUri);
		assertEquals(sqlParam, SELECT_FROM_TABLE);
	}
	
	public void testNotNotify() throws Exception {
		Application application = getApplication();
		assertNotNull(application);
		ContextHolder.getInstance().setContext(application);
		final Uri REFRESH_URI = ModelContract.getUri(BigTestEntity.class);
		Uri sqlQueryUri = ModelContract.getSQLQueryUri(SELECT_FROM_TABLE, REFRESH_URI);
		final Uri resultUri = new ModelContract.UriBuilder(sqlQueryUri).notNotifyChanges().build();
		String sqlParam = ModelContract.getSqlParam(resultUri);
		assertEquals(sqlParam, SELECT_FROM_TABLE);
		assertFalse(ModelContract.isNotify(resultUri));
	}

	
	
	
}
