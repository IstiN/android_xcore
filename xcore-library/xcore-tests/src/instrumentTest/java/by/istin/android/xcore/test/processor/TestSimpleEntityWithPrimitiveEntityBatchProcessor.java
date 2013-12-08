package by.istin.android.xcore.test.processor;


import by.isitn.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntityWithParent;
import by.istin.android.xcore.model.SimpleEntityWithPrimitiveEntity;
import by.istin.android.xcore.model.SimpleEntityWithSubEntity;
import by.istin.android.xcore.model.TagEntity;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntityBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityWithPrimitiveEntityBatchProcessor extends AbstractTestProcessor {

    public TestSimpleEntityWithPrimitiveEntityBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntityWithPrimitiveEntity.class);
        clear(TagEntity.class);

        testExecute(SimpleEntityWithSubEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithPrimitive/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithPrimitiveEntity.class, 3);
        checkCount(TagEntity.class, 6);
        checkRequiredFields(SimpleEntityWithPrimitiveEntity.class, SimpleEntityWithPrimitiveEntity.ID, SimpleEntityWithPrimitiveEntity.TITLE, SimpleEntityWithPrimitiveEntity.ABOUT, SimpleEntityWithPrimitiveEntity.IMAGE_URL);
        checkRequiredFields(TagEntity.class, TagEntity.ID, TagEntity.VALUE, TagEntity.SIMPLE_ENTITY_PARENT);
        testExecute(SimpleEntityWithSubEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithPrimitive/sample_page_2.json?page=2");
        checkCount(SimpleEntityWithPrimitiveEntity.class, 6);
        checkCount(TagEntity.class, 15);
        checkRequiredFields(SimpleEntityWithPrimitiveEntity.class, SimpleEntityWithPrimitiveEntity.ID, SimpleEntityWithPrimitiveEntity.TITLE, SimpleEntityWithPrimitiveEntity.ABOUT, SimpleEntityWithPrimitiveEntity.IMAGE_URL);
        checkRequiredFields(TagEntity.class, TagEntity.ID, TagEntity.VALUE, TagEntity.SIMPLE_ENTITY_PARENT);
        testExecute(SimpleEntityWithSubEntityBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithPrimitive/sample_page_1.json?page=1");
        checkCount(SimpleEntityWithPrimitiveEntity.class, 3);
        checkCount(TagEntity.class, 6);
        checkRequiredFields(SimpleEntityWithPrimitiveEntity.class, SimpleEntityWithPrimitiveEntity.ID, SimpleEntityWithPrimitiveEntity.TITLE, SimpleEntityWithPrimitiveEntity.ABOUT, SimpleEntityWithPrimitiveEntity.IMAGE_URL);
        checkRequiredFields(TagEntity.class, TagEntity.ID, TagEntity.VALUE, TagEntity.SIMPLE_ENTITY_PARENT);
    }

}
