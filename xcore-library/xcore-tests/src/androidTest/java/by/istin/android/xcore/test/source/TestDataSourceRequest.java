package by.istin.android.xcore.test.source;

import android.test.ApplicationTestCase;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.source.DataSourceRequest;

public class TestDataSourceRequest extends ApplicationTestCase<CoreApplication> {

    private static final String URI_1 = "uri1";
    private static final String URI_2 = "uri2";
    private static final String URI_3 = "uri3";
    private static final String URI_4 = "uri4";
    private static final String URI_5 = "uri5";
    private static final String URI_6 = "uri6";
    private static final String PROCESSOR_2 = "processor2";
    private static final String PROCESSOR_3 = "processor3";
    private static final String PROCESSOR_4 = "processor4";
    private static final String PROCESSOR_5 = "processor5";
    private static final String SOURCE_2 = "source2";
    private static final String SOURCE_3 = "source3";
    private static final String SOURCE_4 = "source4";

    public TestDataSourceRequest() {
        super(CoreApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        createApplication();
        super.setUp();
    }

    public void testJoinedRequestBuilder() throws Exception {
        DataSourceRequest dataSourceRequest = new DataSourceRequest(URI_1);
        DataSourceRequest dataSourceRequest2 = new DataSourceRequest(URI_2);
        DataSourceRequest dataSourceRequest3 = new DataSourceRequest(URI_3);
        DataSourceRequest dataSourceRequest4 = new DataSourceRequest(URI_4);
        DataSourceRequest dataSourceRequest5 = new DataSourceRequest(URI_5);
        DataSourceRequest dataSourceRequest6 = new DataSourceRequest(URI_6);
        DataSourceRequest result =
                new DataSourceRequest.JoinedRequestBuilder(dataSourceRequest)
                        .add(dataSourceRequest2, PROCESSOR_2, SOURCE_2)
                        .add(dataSourceRequest3, PROCESSOR_3, SOURCE_3)
                        .add(dataSourceRequest4, PROCESSOR_4, SOURCE_4)
                        .setDataSource(SOURCE_2)
                        .add(dataSourceRequest5, PROCESSOR_5)
                        .setProcessor(PROCESSOR_2)
                        .add(dataSourceRequest6)
                        .build();

        check(result);
    }

    private void check(DataSourceRequest result) {
        assertEquals(URI_1, result.getUri());

        DataSourceRequest request2 = result.getJoinedRequest();
        String dataSourceKey2 = result.getJoinedDataSourceKey();
        String processorKey2 = result.getJoinedProcessorKey();
        assertEquals(URI_2, request2.getUri());
        assertEquals(PROCESSOR_2, processorKey2);
        assertEquals(SOURCE_2, dataSourceKey2);

        DataSourceRequest request3 = request2.getJoinedRequest();
        String dataSourceKey3 = request2.getJoinedDataSourceKey();
        String processorKey3 = request2.getJoinedProcessorKey();
        assertEquals(URI_3, request3.getUri());
        assertEquals(PROCESSOR_3, processorKey3);
        assertEquals(SOURCE_3, dataSourceKey3);

        DataSourceRequest request4 = request3.getJoinedRequest();
        String dataSourceKey4 = request3.getJoinedDataSourceKey();
        String processorKey4 = request3.getJoinedProcessorKey();
        assertEquals(URI_4, request4.getUri());
        assertEquals(PROCESSOR_4, processorKey4);
        assertEquals(SOURCE_4, dataSourceKey4);

        DataSourceRequest request5 = request4.getJoinedRequest();
        String dataSourceKey5 = request4.getJoinedDataSourceKey();
        String processorKey5 = request4.getJoinedProcessorKey();
        assertEquals(URI_5, request5.getUri());
        assertEquals(PROCESSOR_5, processorKey5);
        assertEquals(SOURCE_2, dataSourceKey5);

        DataSourceRequest request6 = request5.getJoinedRequest();
        String dataSourceKey6 = request5.getJoinedDataSourceKey();
        String processorKey6 = request5.getJoinedProcessorKey();
        assertEquals(URI_6, request6.getUri());
        assertEquals(PROCESSOR_2, processorKey6);
        assertEquals(SOURCE_2, dataSourceKey6);

    }

}
