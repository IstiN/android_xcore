package by.istin.android.xcore.wearable;

/**
 * Created by IstiN on 14.09.2014.
 */
public class WearableContract {

    public static final String URI_EXECUTE_SEGMENT = "xcoreexecute";
    public static final String URI_RESULT_SEGMENT = "xcoreresult";

    public static final String URI_EXECUTE = "/" + URI_EXECUTE_SEGMENT;
    public static final String URI_RESULT = "/" + URI_RESULT_SEGMENT;

    public static final String PARAM_DATA_SOURCE_KEY = "p_data_source_key";

    public static final String PARAM_PROCESSOR_KEY = "p_processor_key";

    public static final String PARAM_DATA_SOURCE_REQUEST = "p_data_source_request";

    public static final String PARAM_RESULT = "p_result";

    public static final String PARAM_RESULT_TYPE = "p_result_type";

    public static final String PARAM_SELECTION_ARGS_KEY = "p_selection_args";

    public static final String PARAM_RESULT_URI_KEY = "p_uri";

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_CONTENT_VALUES = 0;
    public static final int TYPE_CONTENT_VALUES_ARRAY = 1;
    public static final int TYPE_SERIALIZABLE = 2;
    public static final int TYPE_ASSET = 3;
}
