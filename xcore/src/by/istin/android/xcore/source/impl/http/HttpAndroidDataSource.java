/**
 * 
 */
package by.istin.android.xcore.source.impl.http;

import android.content.Context;
import android.net.ParseException;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.UriUtils;

/**
 * Class for load data from web.
 * 
 * @author Uladzimir_Klyshevich
 * 
 */
public class HttpAndroidDataSource implements IDataSource<InputStream> {

	public static final String SYSTEM_SERVICE_KEY = "xcore:httpdatasource";

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
	private static final String TAG = HttpAndroidDataSource.class.getSimpleName();

	protected static String sUserAgent;

	public static class DefaultHttpRequestBuilder implements IHttpRequestBuilder {

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
		public HttpRequestBase build(DataSourceRequest dataSourceRequest) {
            String url = dataSourceRequest.getUri();
			HttpRequestBase request = null;
			Uri uri = Uri.parse(url);
			String typeParam = uri.getQueryParameter(TYPE);
			Type type = Type.GET;
			if (!TextUtils.isEmpty(typeParam)) {
				type = Type.valueOf(typeParam.toUpperCase());
			}
			switch (type) {
			case GET:
                request = creteGetRequest(dataSourceRequest, url, uri);
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

        protected HttpRequestBase createDeleteRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            HttpDelete httpDelete = new HttpDelete(url);
            return httpDelete;
        }

        protected HttpRequestBase createPutRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            HttpPut putRequest = new HttpPut(url.split(Q)[0]);
            initEntity(uri, putRequest);
            return putRequest;
        }

        protected HttpRequestBase createPostRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            HttpPost postRequest = new HttpPost(url.split(Q)[0]);
            initEntity(uri, postRequest);
            return postRequest;
        }

        protected HttpRequestBase creteGetRequest(DataSourceRequest dataSourceRequest, String url, Uri uri) {
            HttpGet httpGet = new HttpGet(url);
            return httpGet;
        }

        private void initEntity(Uri uri, HttpEntityEnclosingRequestBase postRequest) {
			Set<String> queryParameterNames = UriUtils.getQueryParameters(uri);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(queryParameterNames.size());
			for (Iterator<String> iterator = queryParameterNames.iterator(); iterator.hasNext();) {
				String paramName = (String) iterator.next();
				if (paramName.equals(TYPE))
					continue;
				String queryParameter = uri.getQueryParameter(paramName);
				if (Build.VERSION.SDK_INT < 16 && queryParameter != null) {
					queryParameter = queryParameter.replace(PLUS, SPACE);
				}
				nameValuePairs.add(new BasicNameValuePair(paramName, queryParameter));
			}
			try {
				postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, UTF_8));
			} catch (UnsupportedEncodingException e) {
				// can be ignored
			}
		}

	}

	public static class DefaultResponseStatusHandler implements IResponseStatusHandler {

		@Override
		public void statusHandle(HttpAndroidDataSource dataSource, HttpUriRequest request, HttpResponse response) throws IOStatusException, ParseException,
				IOException {
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();
			if (statusCode != HttpStatus.SC_OK) {
				String entityValue = EntityUtils.toString(httpEntity);
				Log.e(TAG, response.getStatusLine().getReasonPhrase() + " " + entityValue);
				throw new IOStatusException(response.getStatusLine().getReasonPhrase(), statusCode, entityValue);
			}
		}

	}

	static {
		sUserAgent = USER_AGENT_START + android.os.Build.VERSION.RELEASE + USER_AGENT_DIVIDER + Locale.getDefault().getLanguage() + SHTRICH
				+ Locale.getDefault().getCountry() + USER_AGENT_DIVIDER + android.os.Build.DEVICE + USER_AGENT_BUILD + android.os.Build.ID + USER_AGENT_END;
	}

	/* Apache client. */
	private HttpClient mClient;

	private IHttpRequestBuilder mRequestBuilder;

	private IResponseStatusHandler mResponseStatusHandler;

	public HttpAndroidDataSource() {
		this(new DefaultHttpRequestBuilder(), new DefaultResponseStatusHandler());
	}

	/* Default constructor. */
	public HttpAndroidDataSource(IHttpRequestBuilder requestBuilder, IResponseStatusHandler statusHandler) {
		mRequestBuilder = requestBuilder;
		mResponseStatusHandler = statusHandler;
		mClient = createHttpClient();
	}

    public HttpClient createHttpClient() {
        return AndroidHttpClient.newInstance(sUserAgent);
    }

    public IHttpRequestBuilder getRequestBuilder(){
        return mRequestBuilder;
    }
	/**
	 * Gets instance {@link HttpAndroidDataSource}.
	 * 
	 * @return http client
	 */
	public static HttpAndroidDataSource get(Context ctx) {
		return (HttpAndroidDataSource) AppUtils.get(ctx, SYSTEM_SERVICE_KEY);
	}

	protected HttpRequestBase createRequest(DataSourceRequest request) {
		return mRequestBuilder.build(request);
	}

	public InputStream getInputSteam(HttpUriRequest request) throws IllegalStateException, IOException {
		request.setHeader(ACCEPT_KEY, ACCEPT_DEFAULT_VALUE);
		request.setHeader(USER_AGENT_KEY, sUserAgent);
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
		Log.xd(this, request);
		HttpResponse response = mClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
			Header firstHeader = response.getFirstHeader("Location");
			if (firstHeader != null) {
				HttpGet redirectUri = new HttpGet(firstHeader.getValue());
				/*Header[] allHeaders = response.getAllHeaders();
				for (Header resHeader : allHeaders) {
					redirectUri.addHeader(resHeader);	
				}*/
				request.abort();
				return getInputSteam(redirectUri);
			}
		}
		Log.xd(this, request);
		if (mResponseStatusHandler != null) {
			mResponseStatusHandler.statusHandle(this, request, response);
		}
		/*Header contentEncoding = response.getFirstHeader(CONTENT_ENCODING);
		boolean isGzipResponse = false;
		if (contentEncoding != null) {
			isGzipResponse = contentEncoding != null && GZIP.equalsIgnoreCase(contentEncoding.getValue());
		}*/
		HttpEntity httpEntity = response.getEntity();
		return AndroidHttpClient.getUngzippedContent(httpEntity);
		/*
		content = httpEntity.getContent();
		if (isGzipResponse) {
			content = new GZIPInputStream(content);
		}
		return content;*/
	}

	@Override
	public InputStream getSource(DataSourceRequest dataSourceRequest) throws IOException {
		return getInputSteam(createRequest(dataSourceRequest));
	}

	@Override
	public String getAppServiceKey() {
		return SYSTEM_SERVICE_KEY;
	}

}
