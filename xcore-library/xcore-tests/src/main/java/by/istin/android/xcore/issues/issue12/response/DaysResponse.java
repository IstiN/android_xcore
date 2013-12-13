package by.istin.android.xcore.issues.issue12.response;

import android.content.ContentValues;

/**
 * Created by IstiN on 13.12.13.
 */
public class DaysResponse {

    private String message;

    private String status;

    private ContentValues[] days;

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
