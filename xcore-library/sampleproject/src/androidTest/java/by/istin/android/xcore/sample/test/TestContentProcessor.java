package by.istin.android.xcore.sample.test;


import by.istin.android.xcore.sample.core.model.Content;
import by.istin.android.xcore.sample.core.processor.ContentEntityProcessor;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestContentProcessor extends AbstractTestProcessor {

    public void testStreamerProcessor() throws Exception {
        testExecute(ContentEntityProcessor.APP_SERVICE_KEY, "streamer.json");
        checkCount(Content.class, 80);
        checkRequiredFields(Content.class, Content.ID, Content.POSITION, Content.TIMESTAMP, Content.TIMESTAMP_FORMATTED);
    }

}
