/**
 *
 */
package by.istin.android.xcore.source.impl.http;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.UriUtils;

/**
 * Class for load data from web.
 *
 * @author Uladzimir_Klyshevich
 */
public class HttpDataSource implements IDataSource<InputStream> {

    public static final String APP_SERVICE_KEY = "xcore:httpdatasource";

    private static final String ACCEPT_DEFAULT_VALUE = "*/*";

    private static final String USER_AGENT_KEY = "User-Agent";

    private static final String ACCEPT_KEY = "Accept";

    private static final String USER_AGENT_END = ") AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    private static final String USER_AGENT_BUILD = " Build/";

    private static final String SHTRICH = "-";

    private static final String USER_AGENT_DIVIDER = "; ";

    private static final String USER_AGENT_START = "Mozilla/5.0 (Linux; U; Android ";

    public static final int SO_TIMEOUT = 20000;

    /* Constant encoding for http client. */
    public static final String UTF_8 = "UTF-8";

    /* Constant Tag for logging. */
    private static final String TAG = HttpDataSource.class.getSimpleName();

    protected static final String sUserAgent;

    public static class DefaultHttpRequestBuilder implements IRequestBuilder {

        public static final String TYPE = "_httptype";

        protected static final String Q = "\\?";

        private static final String SPACE = " ";

        private static final String PLUS = "+";

        public static final String POST = "post";

        public static enum Type {
            GET, PUT, POST, DELETE
        }

        public static String getUrl(String url, Type type) {
            if (url.indexOf("?") > 0) {
                return url + "&" + TYPE + "=" + type.name();
            } else {
                return url + "?" + TYPE + "=" + type.name();
            }
        }

        @Override
        public HttpRequest build(DataSourceRequest dataSourceRequest) throws IOException {
            String url = dataSourceRequest.getUri();
            url = prepare(url, dataSourceRequest);
            HttpRequest request = null;
            Uri uri = Uri.parse(url);
            String typeParam = uri.getQueryParameter(TYPE);
            Type type = Type.GET;
            if (!TextUtils.isEmpty(typeParam)) {
                type = Type.valueOf(typeParam.toUpperCase());
            }
            switch (type) {
                case GET:
                    request = createGetRequest(dataSourceRequest, url, uri);
                    break;
                case POST:
                    request = createPostRequest(dataSourceRequest, url, uri);
                    break;
                case PUT:
                    request = createPutRequest(dataSourceRequest, url, uri);
                    break;
                case DELETE:
                    request = createDeleteRequest(dataSourceRequest, url, uri);
                    break;

                default:
                    break;
            }
            return request;
        }

        @Override
        public void postCreate(DataSourceRequest dataSourceRequest, HttpRequest request, Holder<Boolean> isCached) {

        }

        protected String prepare(String url, DataSourceRequest dataSourceRequest) {
            return url;
        }

        protected HttpRequest createDeleteRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            return HttpRequest.delete(url);
        }

        protected HttpRequest createPutRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            HttpRequest putRequest = HttpRequest.put(url.split(Q)[0]);
            initEntity(uri, putRequest);
            return putRequest;
        }

        protected HttpRequest createPostRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            HttpRequest postRequest = HttpRequest.post(url.split(Q)[0]);
            initEntity(uri, postRequest);
            return postRequest;
        }

        protected HttpRequest createGetRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            return HttpRequest.get(url);
        }

        private void initEntity(Uri uri, HttpRequest postRequest) {
            Set<String> queryParameterNames = UriUtils.getQueryParameters(uri);
            for (String paramName : queryParameterNames) {
                if (paramName.equals(TYPE))
                    continue;
                String queryParameter = uri.getQueryParameter(paramName);
                if (Build.VERSION.SDK_INT < 16 && queryParameter != null) {
                    queryParameter = queryParameter.replace(PLUS, SPACE);
                }
                postRequest.form(paramName, queryParameter, UTF_8);
            }
        }

    }

    public static class DefaultResponseStatusHandler implements IResponseHandler {

        @Override
        public void statusHandle(HttpDataSource client, DataSourceRequest dataSourceRequest, HttpRequest request, HttpRequest response, Holder<Boolean> isCached) throws IOException {
            if (!response.ok()) {
                String entityValue = response.body();
                Log.e(TAG, response.message() + " " + entityValue);
                Log.xd(this, request);
                throw new IOStatusException(response.message(), response.code(), entityValue);
            }
        }
    }

    static {
        sUserAgent = USER_AGENT_START + Build.VERSION.RELEASE + USER_AGENT_DIVIDER + Locale.getDefault().getLanguage() + SHTRICH
                + Locale.getDefault().getCountry() + USER_AGENT_DIVIDER + Build.DEVICE + USER_AGENT_BUILD + Build.ID + USER_AGENT_END;
    }


    private final IRequestBuilder mRequestBuilder;

    private final IResponseHandler mResponseStatusHandler;

    //private final InputStreamHttpClientHelper mInputStreamHelper;

    public HttpDataSource() {
        this(new DefaultHttpRequestBuilder(), new DefaultResponseStatusHandler());
    }

    /* Default constructor. */
    public HttpDataSource(IRequestBuilder requestBuilder, IResponseHandler statusHandler) {
        mRequestBuilder = requestBuilder;
        mResponseStatusHandler = statusHandler;
      //  mInputStreamHelper = createInputStreamHttpClientHelper();
    }

   // protected InputStreamHttpClientHelper createInputStreamHttpClientHelper() {
     //   return new InputStreamHttpClientHelper(sUserAgent);
    //}

    public IRequestBuilder getRequestBuilder() {
        return mRequestBuilder;
    }

    /**
     * Gets instance {@link HttpDataSource}.
     *
     * @return http client
     */
    public static HttpDataSource get(Context ctx) {
        return (HttpDataSource) AppUtils.get(ctx, APP_SERVICE_KEY);
    }

    protected HttpRequest createRequest(DataSourceRequest request) throws IOException {
        return mRequestBuilder.build(request);
    }

    public IResponseHandler getResponseStatusHandler() {
        return mResponseStatusHandler;
    }

    public InputStream getInputSteam(DataSourceRequest dataSourceRequest, HttpRequest request, Holder<Boolean> isCached) throws IllegalStateException, IOException {
        request.accept(ACCEPT_DEFAULT_VALUE);
        request.header(USER_AGENT_KEY, sUserAgent);
        request.acceptGzipEncoding();
        request.followRedirects(true);
        request.ignoreCloseExceptions();
        request.uncompress(true);
        //AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
        Log.xd(this, request);
        mRequestBuilder.postCreate(dataSourceRequest, request, isCached);
        //HttpClient client = null;
        //try {
           // client = mInputStreamHelper.getClient();
            //HttpResponse response = client.execute(request);
            //int statusCode = response.getStatusLine().getStatusCode();
            /*boolean isRedirect = isRedirect(statusCode);
            if (isRedirect) {
                Header firstHeader = response.getFirstHeader("Location");
                if (firstHeader != null) {
                    String value = firstHeader.getValue();
                    if (!StringUtil.isEmpty(value) && !value.equals(request.getURI().toString())) {
                        return createRedirectRequest(dataSourceRequest, request, response, value, isCached);
                    }
                }
            }*/
            if (mResponseStatusHandler != null) {
                mResponseStatusHandler.statusHandle(this, dataSourceRequest, request, request, isCached);
                if (isCached != null && !isCached.isNull() && isCached.get()) {
                    return null;
                }
            }
            //HttpEntity httpEntity = response.getEntity();
            //InputStream ungzippedContent = AndroidHttpClient.getUngzippedContent(httpEntity);

            return request.stream();
        //} finally {
          //  mInputStreamHelper.releaseClient(client);
        //}
    }

   /* protected InputStream createRedirectRequest(DataSourceRequest dataSourceRequest, HttpUriRequest request, HttpResponse response, String value, Holder<Boolean> isCached) throws IOException {
        Log.xd(this, "redirect " + value);
        if (!URLUtil.isNetworkUrl(value)) {
            Log.xd(this, "redirect current request ");
            value = request.getURI().getScheme() + "://" + request.getURI().getHost() + value;
            Log.xd(this, "redirect current request " + value);
        }
        HttpGet redirectUri = new HttpGet(value);
        request.abort();
        return getInputSteam(dataSourceRequest, redirectUri, isCached);
    }

    protected boolean isRedirect(int statusCode) {
        return statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY;
    }*/

    @Override
    public InputStream getSource(DataSourceRequest dataSourceRequest, Holder<Boolean> isCached) throws IOException {
        return getInputSteam(dataSourceRequest, createRequest(dataSourceRequest), isCached);
    }

    @Override
    public String getAppServiceKey() {
        return APP_SERVICE_KEY;
    }

}
