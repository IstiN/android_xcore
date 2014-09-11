package by.istin.android.xcore.oauth2;

import android.net.Uri;
import android.text.format.DateUtils;

import org.json.JSONException;

import java.io.InputStream;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.processor.impl.AbstractStringProcessor;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UrlBuilder;

public class DefaultOAuth2Helper implements OAuth2Helper {

    private Credentials mCredentials;

    private Configuration mConfiguration;

    public DefaultOAuth2Helper(Configuration configuration) {
        mConfiguration = configuration;
        if (mConfiguration.isSave()) {
            restore();
        }
    }

    @Override
    public String getUrl() throws Exception {
        return UrlBuilder.uri(mConfiguration.getAuthorizationServerUrl()).
                param("response_type", mConfiguration.getResponseType()).
                param("client_id", mConfiguration.getApiKey()).build();
    }

    @Override
    public Credentials processUrl(String url) throws Exception {
        Uri uri = Uri.parse(url);
        String code = uri.getQueryParameter("code");
        String tokenServerUrl = mConfiguration.getTokenServerUrl();
        String tokenUrl = UrlBuilder.uri(tokenServerUrl).
                param("grant_type", mConfiguration.getGrandTypeToken()).
                param("client_id", mConfiguration.getApiKey()).
                param("client_secret", mConfiguration.getApiSecret()).
                param("redirect_uri", mConfiguration.getRedirectUrl()).
                param("code", code).build();
        Credentials credentials = getCredentials(tokenUrl);
        return credentials;
    }

    public Credentials getCredentials(String tokenUrl) throws Exception {
        String typeUrl = HttpAndroidDataSource.DefaultHttpRequestBuilder.getUrl(tokenUrl, mConfiguration.getTokenRequestType());
        DataSourceRequest dataSourceRequest = new DataSourceRequest(typeUrl);
        dataSourceRequest.setCacheable(false);
        dataSourceRequest.setForceUpdateData(true);
        HttpAndroidDataSource httpAndroidDataSource = HttpAndroidDataSource.get(ContextHolder.get());
        InputStream source = httpAndroidDataSource.getSource(dataSourceRequest, new Holder<Boolean>());
        Credentials credentials = new AbstractStringProcessor<Credentials>() {

            @Override
            protected Credentials convert(String string) throws Exception {
                return new Credentials(string);
            }

            @Override
            public String getAppServiceKey() {
                return null;
            }
        }.execute(dataSourceRequest, httpAndroidDataSource, source);
        if (mConfiguration.isSave()) {
            credentials.setSavedTime(System.currentTimeMillis());
            PreferenceHelper.set(mConfiguration.getPreferenceKey(), credentials.toString());
        }
        return credentials;
    }

    @Override
    public void sign(OAuth2Request request) throws Exception {
        if (mCredentials == null) {
            restore();
            if (mCredentials == null) {
                throw new AuthorizationRequiredException();
            }
        }
        if (isExpired(mCredentials)) {
            if (!isRefreshTokenExpired(mCredentials)) {
                String tokenServerUrl = mConfiguration.getTokenServerUrl();
                String tokenUrl = UrlBuilder.uri(tokenServerUrl).
                        param("grant_type", mConfiguration.getGrandTypeRefreshToken()).
                        param("client_id", mConfiguration.getApiKey()).
                        param("client_secret", mConfiguration.getApiSecret()).
                        param("refresh_token", mCredentials.getRefreshToken()).build();
                mCredentials = getCredentials(tokenUrl);
            } else {
                throw new AuthorizationRequiredException();
            }
        }
        request.sign(request.getRequest(), "Authorization", "Bearer " + mCredentials.getAccessToken());
    }

    private void restore() {
        String savedValue = PreferenceHelper.getString(mConfiguration.getPreferenceKey(), null);
        if (!StringUtil.isEmpty(savedValue)) {
            try {
                mCredentials = new Credentials(savedValue);
            } catch (JSONException e) {
                //can be ignored
                Log.e("DefaultOAuth2Helper", e);
            }
        }
    }

    @Override
    public boolean isExpired(Credentials credentials) {
        return (System.currentTimeMillis() - credentials.getSavedTime()) > credentials.getExpiresIn() * DateUtils.SECOND_IN_MILLIS;
    }

    @Override
    public boolean isRefreshTokenExpired(Credentials credentials) throws Exception {
        return (System.currentTimeMillis() - credentials.getSavedTime()) > 60 * DateUtils.DAY_IN_MILLIS;
    }

    @Override
    public Credentials getCredentials() {
        if (mCredentials != null) {
            return mCredentials;
        }
        restore();
        return mCredentials;
    }

    @Override
    public boolean isLogged() {
        return getCredentials() != null;
    }
}
