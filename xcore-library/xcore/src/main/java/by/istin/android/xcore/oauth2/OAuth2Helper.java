package by.istin.android.xcore.oauth2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface OAuth2Helper {

    String getUrl() throws Exception;

    Credentials processUrl(String url) throws Exception;

    void sign(OAuth2Request httpUriRequest) throws Exception;

    boolean isExpired(Credentials credentials) throws Exception;

    boolean isRefreshTokenExpired(Credentials credentials) throws Exception;

    Credentials getCredentials();

    boolean isLogged();

    public static class Impl {

        private static Map<Configuration, OAuth2Helper> sCache = new ConcurrentHashMap<>();

        public static OAuth2Helper create(Configuration configuration) {
            return new DefaultOAuth2Helper(configuration);
        }

        ;

        public static OAuth2Helper getInstance(Configuration configuration) {
            OAuth2Helper oAuth2Helper = sCache.get(configuration);
            if (oAuth2Helper == null) {
                oAuth2Helper = create(configuration);
                sCache.put(configuration, oAuth2Helper);
            }
            return oAuth2Helper;
        }
    }
}
