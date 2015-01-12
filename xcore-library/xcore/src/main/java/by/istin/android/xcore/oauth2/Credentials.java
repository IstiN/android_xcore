package by.istin.android.xcore.oauth2;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import by.istin.android.xcore.model.JSONModel;

public class Credentials extends JSONModel {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String EXPIRES_IN = "expires_in";

    private static final String TOKEN_TYPE = "token_type";

    private static final String REFRESH_TOKEN = "refresh_token";

    private static final String SAVED_TIME = "saved_time";

    private static final String CODE = "code";

    public Credentials() {
    }

    public Credentials(String json) throws JSONException {
        super(json);
    }

    public Credentials(JSONObject json) {
        super(json);
    }

    public Credentials(Parcel source) {
        super(source);
    }

    public String getAccessToken() {
        return getString(ACCESS_TOKEN);
    }

    public Long getExpiresIn() {
        return getLong(EXPIRES_IN);
    }

    public Long getSavedTime() {
        return getLong(SAVED_TIME);
    }

    public String getTokenType() {
        return getString(TOKEN_TYPE);
    }

    public String getRefreshToken() {
        return getString(REFRESH_TOKEN);
    }

    public void setSavedTime(long savedTimeInMillis) {
        set(SAVED_TIME, savedTimeInMillis);
    }

    public void setRefreshToken(String refreshToken) {
        set(REFRESH_TOKEN, refreshToken);
    }

    public void setCode(String code) {
        set(CODE, code);
    }

    public String getCode() {
        return getString(CODE);
    }
}
