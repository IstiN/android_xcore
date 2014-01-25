package by.istin.android.xcore.test.issues;


import by.istin.android.xcore.app.Application;
import by.istin.android.xcore.issues.issue12.model.DayEntity;
import by.istin.android.xcore.issues.issue12.processor.DaysBatchProcessor;
import by.istin.android.xcore.issues.issue12.response.DaysResponse;
import by.istin.android.xcore.test.common.AbstractTestProcessor;

public class TestIssue12Processor extends AbstractTestProcessor {

    public TestIssue12Processor() {
        super(Application.class);
    }

    public void testSampleProcessor() throws Exception {
        DaysResponse result = (DaysResponse) testExecute(DaysBatchProcessor.APP_SERVICE_KEY, "issues/12.json");
        assertEquals("success", result.getStatus());
        assertEquals("OK", result.getMessage());
        checkCount(DayEntity.class, 2);
        checkRequiredFields(DayEntity.class, DayEntity.VALUE);
    }

}
