package by.istin.android.xcore.oauth2;


import java.io.IOException;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpDataSource;
import by.istin.android.xcore.source.impl.http.HttpRequest;
import by.istin.android.xcore.utils.Holder;

public class OAuth2RequestBuilder extends HttpDataSource.DefaultHttpRequestBuilder {

    private OAuth2Helper mOAuth2Helper;

    public OAuth2RequestBuilder(Configuration configuration) {
        this.mOAuth2Helper = getOAuth2Helper(configuration);
    }

    public OAuth2RequestBuilder(OAuth2Helper oauth2Helper) {
        this.mOAuth2Helper = oauth2Helper;
    }

    protected OAuth2Helper getOAuth2Helper(Configuration configuration) {
        return OAuth2Helper.Impl.getInstance(configuration);
    }

    @Override
    public void postCreate(DataSourceRequest dataSourceRequest, HttpRequest request, Holder<Boolean> isCached) throws IOException {
        super.postCreate(dataSourceRequest, request, isCached);
        try {
            mOAuth2Helper.sign(new OAuth2Request<HttpRequest>(request) {
                @Override
                public void sign(HttpRequest requestBase, String header, String value) {
                    requestBase.header(header, value);
                }
            });
        } catch (AuthorizationRequiredException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
