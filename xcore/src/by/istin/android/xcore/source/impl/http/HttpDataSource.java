/**
 * 
 */
package by.istin.android.xcore.source.impl.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
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

import android.content.Context;
import android.net.ParseException;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.IDataSource;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.UriUtils;

/**
 * Class for load data from web.
 * 
 * @author Uladzimir_Klyshevich
 * 
 */
public class HttpDataSource implements IDataSource {

	public static final String SYSTEM_SERVICE_NAME = "framework:httpdatasource";

	private static final String ACCEPT_DEFAULT_VALUE = "*/*";

	private static final String USER_AGENT_KEY = "User-Agent";

	private static final String ACCEPT_KEY = "Accept";

	private static final String GZIP = "gzip";

	private static final String CONTENT_ENCODING = "Content-Encoding";

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

	private static String sUserAgent;

	public static class DefaultHttpRequestBuilder implements IHttpRequestBuilder {

		public static final String TYPE = "_httptype";

		private static final String Q = "\\?";

		private static final String SPACE = " ";

		private static final String PLUS = "+";

		public static final String POST = "post";

		public static enum Type {
			GET, PUT, POST, DELETE
		}

		public static String getTypedUrl(String url, Type type) {
			if (url.indexOf("?") > 0) {
				return url + "&" + TYPE + "=" + type.name();
			} else {
				return url + "?" + TYPE + "=" + type.name();
			}
		}

		@Override
		public HttpRequestBase build(String url) {
			HttpRequestBase request = null;
			Uri uri = Uri.parse(url);
			String typeParam = uri.getQueryParameter(TYPE);
			Type type = Type.GET;
			if (!TextUtils.isEmpty(typeParam)) {
				type = Type.valueOf(typeParam.toUpperCase());
			}
			switch (type) {
			case GET:
				HttpGet httpGet = new HttpGet(url);
				request = httpGet;
				break;
			case POST:
				HttpPost postRequest = new HttpPost(url.split(Q)[0]);
				initEntity(uri, postRequest);
				request = postRequest;
				break;
			case PUT:
				HttpPut putRequest = new HttpPut(url.split(Q)[0]);
				initEntity(uri, putRequest);
				request = putRequest;
				break;
			case DELETE:
				HttpDelete httpDelete = new HttpDelete(url);
				request = httpDelete;
				break;

			default:
				break;
			}
			return request;
		}

		private void initEntity(Uri uri, HttpEntityEnclosingRequestBase postRequest) {
			Set<String> queryParameterNames = UriUtils.getQueryParameters(uri);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(queryParameterNames.size());
			for (Iterator<String> iterator = queryParameterNames.iterator(); iterator.hasNext();) {
				String paramName = (String) iterator.next();
				if (paramName.equals(POST))
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
		public void statusHandle(HttpDataSource dataSource, HttpUriRequest request, HttpResponse response) throws IOStatusException, ParseException,
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
	private AndroidHttpClient mClient;

	private IHttpRequestBuilder mRequestBuilder;

	private IResponseStatusHandler mResponseStatusHandler;

	public HttpDataSource() {
		this(new DefaultHttpRequestBuilder(), new DefaultResponseStatusHandler());
	}

	/* Default constructor. */
	public HttpDataSource(IHttpRequestBuilder requestBuilder, IResponseStatusHandler statusHandler) {
		mRequestBuilder = requestBuilder;
		mResponseStatusHandler = statusHandler;
		mClient = AndroidHttpClient.newInstance(sUserAgent);
		mClient.enableCurlLogging(TAG, Log.VERBOSE);
	}

	/**
	 * Gets instance {@link HttpDataSource}.
	 * 
	 * @return http client
	 */
	public static HttpDataSource get(Context ctx) {
		return (HttpDataSource) AppUtils.get(ctx, SYSTEM_SERVICE_NAME);
	}

	protected HttpRequestBase createRequest(DataSourceRequest request) {
		return mRequestBuilder.build(request.getUrl());
	}

	public InputStream getInputSteam(HttpUriRequest request) throws IllegalStateException, IOException {
		request.setHeader(ACCEPT_KEY, ACCEPT_DEFAULT_VALUE);
		request.setHeader(USER_AGENT_KEY, sUserAgent);
		InputStream content = null;
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
		HttpResponse response = mClient.execute(request);
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

}
