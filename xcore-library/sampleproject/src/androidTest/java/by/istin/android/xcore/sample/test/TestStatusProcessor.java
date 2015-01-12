package by.istin.android.xcore.sample.test;


import by.istin.android.xcore.sample.core.model.SampleEntity;
import by.istin.android.xcore.sample.core.processor.SampleEntityProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestStatusProcessor extends AbstractTestProcessor {

    public void testSampleProcessor() throws Exception {
        testExecute(SampleEntityProcessor.APP_SERVICE_KEY, "sample_page_1.json?page=1");
        checkCount(SampleEntity.class, 5);
        checkRequiredFields(SampleEntity.class, SampleEntity.ID, SampleEntity.TITLE, SampleEntity.ABOUT, SampleEntity.IMAGE_URL);
        testExecute(SampleEntityProcessor.APP_SERVICE_KEY, "sample_page_2.json?page=2");
        checkCount(SampleEntity.class, 10);
        checkRequiredFields(SampleEntity.class, SampleEntity.ID, SampleEntity.TITLE, SampleEntity.ABOUT, SampleEntity.IMAGE_URL);
    }

}
