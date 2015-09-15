package by.istin.android.xcore.oauth2;


import by.istin.android.xcore.source.impl.http.HttpDataSource;

public class Configuration {

    private final String preferenceKey;

    private String apiKey;

    private String apiSecret;

    private String redirectUrl;

    private String tokenServerUrl;

    private String authorizationServerUrl;

    private boolean isSave = true;

    private String responseType = "code";

    private String grandType = "authorization_code";

    public String getGrandType() {
        return grandType;
    }

    private HttpDataSource.DefaultHttpRequestBuilder.Type tokenRequestType = HttpDataSource.DefaultHttpRequestBuilder.Type.POST;

    private String grandTypeRefreshToken = "refresh_token";

    private String scope;

    public Configuration(String apiKey, String apiSecret, String redirectUrl, String tokenServerUrl, String authorizationServerUrl, String scope, boolean isSave, String preferenceKey) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.redirectUrl = redirectUrl;
        this.tokenServerUrl = tokenServerUrl;
        this.authorizationServerUrl = authorizationServerUrl;
        this.preferenceKey = preferenceKey;
        this.isSave = isSave;
        this.scope = scope;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getTokenServerUrl() {
        return tokenServerUrl;
    }

    public String getAuthorizationServerUrl() {
        return authorizationServerUrl;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getGrandTypeToken() {
        return grandType;
    }

    public HttpDataSource.DefaultHttpRequestBuilder.Type getTokenRequestType() {
        return tokenRequestType;
    }

    public boolean isSave() {
        return isSave;
    }

    public String getPreferenceKey() {
        return preferenceKey;
    }

    public String getGrandTypeRefreshToken() {
        return grandTypeRefreshToken;
    }

    public String getScope() {
        return scope;
    }

}
