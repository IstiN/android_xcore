package by.istin.android.xcore.oauth2;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;

public class OAuth2RequestBuilder extends HttpAndroidDataSource.DefaultHttpRequestBuilder {

    private OAuth2Helper mOAuth2Helper;

    public OAuth2RequestBuilder(Configuration configuration) {
        this.mOAuth2Helper = createOAuth2Helper(configuration);
    }

    public OAuth2RequestBuilder(OAuth2Helper oauth2Helper) {
        this.mOAuth2Helper = oauth2Helper;
    }

    protected OAuth2Helper createOAuth2Helper(Configuration configuration) {
        return OAuth2Helper.Impl.create(configuration);
    }

    @Override
    public HttpRequestBase build(DataSourceRequest dataSourceRequest) throws IOException {
        HttpRequestBase httpRequestBase = super.build(dataSourceRequest);
        try {
            mOAuth2Helper.sign(httpRequestBase);
        } catch (AuthorizationRequiredException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
        return httpRequestBase;
    }
}
