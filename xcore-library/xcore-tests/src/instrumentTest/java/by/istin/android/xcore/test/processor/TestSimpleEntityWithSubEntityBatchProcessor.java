package by.istin.android.xcore.test.processor;


import by.isitn.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntity;
import by.istin.android.xcore.model.SimpleEntityWithParent;
import by.istin.android.xcore.model.SimpleEntityWithSubEntity;
import by.istin.android.xcore.processor.SimpleEntityBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntityBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityWithSubEntityBatchProcessor extends AbstractTestProcessor {

    public TestSimpleEntityWithSubEntityBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntityWithParent.class);
        clear(SimpleEntityWithSubEntity.class);

        testExecute(SimpleEntityWithSubEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithSubEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithParent.class, 2);
        checkCount(SimpleEntityWithSubEntity.class, 3);
        checkRequiredFields(SimpleEntityWithParent.class, SimpleEntityWithParent.ID, SimpleEntityWithParent.TITLE, SimpleEntityWithParent.ABOUT, SimpleEntityWithParent.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithSubEntity.class, SimpleEntityWithSubEntity.ID, SimpleEntityWithSubEntity.TITLE, SimpleEntityWithSubEntity.ABOUT, SimpleEntityWithSubEntity.IMAGE_URL);
        testExecute(SimpleEntityWithSubEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithSubEntity/sample_page_2.json?page=2");
        checkCount(SimpleEntityWithParent.class, 4);
        checkCount(SimpleEntityWithSubEntity.class, 6);
        checkRequiredFields(SimpleEntityWithParent.class, SimpleEntityWithParent.ID, SimpleEntityWithParent.TITLE, SimpleEntityWithParent.ABOUT, SimpleEntityWithParent.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithSubEntity.class, SimpleEntityWithSubEntity.ID, SimpleEntityWithSubEntity.TITLE, SimpleEntityWithSubEntity.ABOUT, SimpleEntityWithSubEntity.IMAGE_URL);
        testExecute(SimpleEntityWithSubEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithSubEntity/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithParent.class, 2);
        checkCount(SimpleEntityWithSubEntity.class, 3);
        checkRequiredFields(SimpleEntityWithParent.class, SimpleEntityWithParent.ID, SimpleEntityWithParent.TITLE, SimpleEntityWithParent.ABOUT, SimpleEntityWithParent.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithSubEntity.class, SimpleEntityWithSubEntity.ID, SimpleEntityWithSubEntity.TITLE, SimpleEntityWithSubEntity.ABOUT, SimpleEntityWithSubEntity.IMAGE_URL);
    }

}
