package by.istin.android.xcore.test.processor;


import by.isitn.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntityWithParent;
import by.istin.android.xcore.model.SimpleEntityWithParent1;
import by.istin.android.xcore.model.SimpleEntityWithParent2;
import by.istin.android.xcore.model.SimpleEntityWithSubEntities;
import by.istin.android.xcore.model.SimpleEntityWithSubEntity;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntitiesBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntityBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityWithSubEntitiesBatchProcessor extends AbstractTestProcessor {

    public TestSimpleEntityWithSubEntitiesBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntityWithParent1.class);
        clear(SimpleEntityWithParent2.class);
        clear(SimpleEntityWithSubEntities.class);

        testExecute(SimpleEntityWithSubEntitiesBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithMultiSubEntities/sample_page_1.json?page=1");

        checkCount(SimpleEntityWithParent1.class, 3);
        checkCount(SimpleEntityWithParent2.class, 2);
        checkCount(SimpleEntityWithSubEntities.class, 3);

        checkRequiredFields(SimpleEntityWithSubEntities.class, SimpleEntityWithSubEntities.ID, SimpleEntityWithSubEntities.TITLE, SimpleEntityWithSubEntities.ABOUT, SimpleEntityWithSubEntities.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithParent1.class, SimpleEntityWithParent1.ID, SimpleEntityWithParent1.TITLE, SimpleEntityWithParent1.ABOUT, SimpleEntityWithParent1.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithParent2.class, SimpleEntityWithParent2.ID, SimpleEntityWithParent2.TITLE, SimpleEntityWithParent2.ABOUT, SimpleEntityWithParent2.IMAGE_URL);

        testExecute(SimpleEntityWithSubEntitiesBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithMultiSubEntities/sample_page_2.json?page=2");
        checkCount(SimpleEntityWithParent1.class, 4);
        checkCount(SimpleEntityWithParent2.class, 3);
        checkCount(SimpleEntityWithSubEntities.class, 6);

        checkRequiredFields(SimpleEntityWithSubEntities.class, SimpleEntityWithSubEntities.ID, SimpleEntityWithSubEntities.TITLE, SimpleEntityWithSubEntities.ABOUT, SimpleEntityWithSubEntities.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithParent1.class, SimpleEntityWithParent1.ID, SimpleEntityWithParent1.TITLE, SimpleEntityWithParent1.ABOUT, SimpleEntityWithParent1.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithParent2.class, SimpleEntityWithParent2.ID, SimpleEntityWithParent2.TITLE, SimpleEntityWithParent2.ABOUT, SimpleEntityWithParent2.IMAGE_URL);

        testExecute(SimpleEntityWithSubEntitiesBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithMultiSubEntities/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithParent1.class, 3);
        checkCount(SimpleEntityWithParent2.class, 2);
        checkCount(SimpleEntityWithSubEntities.class, 3);

        checkRequiredFields(SimpleEntityWithSubEntities.class, SimpleEntityWithSubEntities.ID, SimpleEntityWithSubEntities.TITLE, SimpleEntityWithSubEntities.ABOUT, SimpleEntityWithSubEntities.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithParent1.class, SimpleEntityWithParent1.ID, SimpleEntityWithParent1.TITLE, SimpleEntityWithParent1.ABOUT, SimpleEntityWithParent1.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithParent2.class, SimpleEntityWithParent2.ID, SimpleEntityWithParent2.TITLE, SimpleEntityWithParent2.ABOUT, SimpleEntityWithParent2.IMAGE_URL);
    }

}
