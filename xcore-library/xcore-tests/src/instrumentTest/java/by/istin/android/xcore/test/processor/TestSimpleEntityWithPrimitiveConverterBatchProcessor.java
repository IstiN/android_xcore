package by.istin.android.xcore.test.processor;


import by.isitn.android.xcore.app.Application;
import by.istin.android.xcore.model.SimpleEntityWithCustomPrimitiveConverter;
import by.istin.android.xcore.model.SimpleEntityWithPrimitiveEntity;
import by.istin.android.xcore.model.TagEntity;
import by.istin.android.xcore.processor.SimpleEntityWithPrimitiveConverterBatchProcessor;
import by.istin.android.xcore.processor.SimpleEntityWithSubEntityBatchProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestSimpleEntityWithPrimitiveConverterBatchProcessor extends AbstractTestProcessor {

    public TestSimpleEntityWithPrimitiveConverterBatchProcessor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        clear(SimpleEntityWithCustomPrimitiveConverter.class);

        testExecute(SimpleEntityWithPrimitiveConverterBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithPrimitive/sample_page_1.json?page=1");

        checkCount(SimpleEntityWithCustomPrimitiveConverter.class, 3);

        checkRequiredFields(SimpleEntityWithCustomPrimitiveConverter.class, SimpleEntityWithCustomPrimitiveConverter.ID, SimpleEntityWithCustomPrimitiveConverter.TITLE, SimpleEntityWithCustomPrimitiveConverter.ABOUT, SimpleEntityWithCustomPrimitiveConverter.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithCustomPrimitiveConverter.class, 2, SimpleEntityWithCustomPrimitiveConverter.TAGS);

        testExecute(SimpleEntityWithPrimitiveConverterBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithPrimitive/sample_page_2.json?page=2");

        checkCount(SimpleEntityWithCustomPrimitiveConverter.class, 6);

        checkRequiredFields(SimpleEntityWithCustomPrimitiveConverter.class, SimpleEntityWithCustomPrimitiveConverter.ID, SimpleEntityWithCustomPrimitiveConverter.TITLE, SimpleEntityWithCustomPrimitiveConverter.ABOUT, SimpleEntityWithCustomPrimitiveConverter.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithCustomPrimitiveConverter.class, 4, SimpleEntityWithCustomPrimitiveConverter.TAGS);

        testExecute(SimpleEntityWithPrimitiveConverterBatchProcessor.APP_SERVICE_KEY, "simpleEntityWithPrimitive/sample_page_1.json?page=1");

        checkCount(SimpleEntityWithCustomPrimitiveConverter.class, 3);

        checkRequiredFields(SimpleEntityWithCustomPrimitiveConverter.class, SimpleEntityWithCustomPrimitiveConverter.ID, SimpleEntityWithCustomPrimitiveConverter.TITLE, SimpleEntityWithCustomPrimitiveConverter.ABOUT, SimpleEntityWithCustomPrimitiveConverter.IMAGE_URL);
        checkRequiredFields(SimpleEntityWithCustomPrimitiveConverter.class, 2, SimpleEntityWithCustomPrimitiveConverter.TAGS);
    }

}
