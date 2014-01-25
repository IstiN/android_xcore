package by.istin.android.xcore.test.processor;


import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntityWithSubJson;
import by.istin.android.xcore.processor.SimpleEntityWithSubJsonBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityWithSubJsonBatchProcessor extends AbstractTestProcessor {

    public TestSimpleEntityWithSubJsonBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntityWithSubJson.class);

        testExecute(SimpleEntityWithSubJsonBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithSubEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithSubJson.class, 3);
        checkRequiredFields(SimpleEntityWithSubJson.class, SimpleEntityWithSubJson.ID, SimpleEntityWithSubJson.TITLE, SimpleEntityWithSubJson.ABOUT, SimpleEntityWithSubJson.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithSubJson.class, 2, SimpleEntityWithSubJson.SUB_ID, SimpleEntityWithSubJson.SUB_ABOUT, SimpleEntityWithSubJson.SUB_IMAGE_URL, SimpleEntityWithSubJson.SUB_TITLE);
        testExecute(SimpleEntityWithSubJsonBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithSubEntity/sample_page_2.json?page=2");
        checkCount(SimpleEntityWithSubJson.class, 6);
        checkRequiredFields(SimpleEntityWithSubJson.class, SimpleEntityWithSubJson.ID, SimpleEntityWithSubJson.TITLE, SimpleEntityWithSubJson.ABOUT, SimpleEntityWithSubJson.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithSubJson.class, 4, SimpleEntityWithSubJson.SUB_ID, SimpleEntityWithSubJson.SUB_ABOUT, SimpleEntityWithSubJson.SUB_IMAGE_URL, SimpleEntityWithSubJson.SUB_TITLE);
        //
        testExecute(SimpleEntityWithSubJsonBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithSubEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithSubJson.class, 3);
        checkRequiredFields(SimpleEntityWithSubJson.class, SimpleEntityWithSubJson.ID, SimpleEntityWithSubJson.TITLE, SimpleEntityWithSubJson.ABOUT, SimpleEntityWithSubJson.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithSubJson.class, 2, SimpleEntityWithSubJson.SUB_ID, SimpleEntityWithSubJson.SUB_ABOUT, SimpleEntityWithSubJson.SUB_IMAGE_URL, SimpleEntityWithSubJson.SUB_TITLE);
    }

}
