package by.istin.android.xcore.test.processor;


import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.model.SuperBigTestEntity;
import by.istin.android.xcore.processor.SuperBigEntityBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSuperBigEntityBatchProcessor extends AbstractTestProcessor {

    public TestSuperBigEntityBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SuperBigTestEntity.class);
        SuperBigEntityBatchProcessor.Response response = (SuperBigEntityBatchProcessor.Response) testExecute(SuperBigEntityBatchProcessor.APP_SERVICE_KEY, "test_big_feed.json");
        assertEquals(0, response.listings == null ? 0 : response.listings.size());
        //checkCount(SuperBigTestEntity.class, 111084);
        //checkRequiredFields(SuperBigTestEntity.class, SuperBigTestEntity.ID, SuperBigTestEntity.ID_AS_STRING);
    }

}
