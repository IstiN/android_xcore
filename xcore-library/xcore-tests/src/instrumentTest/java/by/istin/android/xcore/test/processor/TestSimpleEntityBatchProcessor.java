package by.istin.android.xcore.test.processor;


import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.processor.SimpleEntityBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityBatchProcessor extends AbstractTestProcessor {

    public TestSimpleEntityBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntity.class);

        testExecute(SimpleEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntity.class, 5);
        checkRequiredFields(SimpleEntity.class, SimpleEntity.ID, SimpleEntity.TITLE, SimpleEntity.ABOUT, SimpleEntity.IMAGE_URL);
        testExecute(SimpleEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_2.json?page=2");
        checkCount(SimpleEntity.class, 10);
        checkRequiredFields(SimpleEntity.class, SimpleEntity.ID, SimpleEntity.TITLE, SimpleEntity.ABOUT, SimpleEntity.IMAGE_URL);
        testExecute(SimpleEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkRequiredFields(SimpleEntity.class, SimpleEntity.ID, SimpleEntity.TITLE, SimpleEntity.ABOUT, SimpleEntity.IMAGE_URL);
        checkCount(SimpleEntity.class, 5);
    }

}
