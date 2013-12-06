package by.istin.android.xcore.test.processor;


import by.isitn.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.processor.SimpleEntityProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityProcessor extends AbstractTestProcessor {

    public TestSimpleEntityProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntity.class);

        testExecute(SimpleEntityProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntity.class, 5);
        checkRequiredFields(SimpleEntity.class, SimpleEntity.ID, SimpleEntity.TITLE, SimpleEntity.ABOUT, SimpleEntity.IMAGE_URL);
        testExecute(SimpleEntityProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_2.json?page=2");
        checkCount(SimpleEntity.class, 10);
        checkRequiredFields(SimpleEntity.class, SimpleEntity.ID, SimpleEntity.TITLE, SimpleEntity.ABOUT, SimpleEntity.IMAGE_URL);
        testExecute(SimpleEntityProcessor.APP_SERVICE_KEY, "simpleEntity/sample_page_1.json?page=1");
        checkRequiredFields(SimpleEntity.class, SimpleEntity.ID, SimpleEntity.TITLE, SimpleEntity.ABOUT, SimpleEntity.IMAGE_URL);
        checkCount(SimpleEntity.class, 5);
    }

}
