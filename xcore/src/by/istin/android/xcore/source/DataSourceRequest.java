package by.istin.android.xcore.source;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UriUtils;

public class DataSourceRequest {

	public static final long CACHE_EXPIRATION_NONE = -1l;

	private static final String REQUEST_URI = "___ruri";

	private static final String REQUEST_PARENT_URI = "___parent_ruri";

	private static final String REQUEST_CACHEABLE = "___c";
	
	private static final String REQUEST_FORCE_UPDATE_DATA = "___fud";
	
	private static final String REQUEST_CACHE_EXPIRATION = "___exp";
	
	private static final String[] KEYS = new String[] {
		REQUEST_URI,
        REQUEST_PARENT_URI,
		REQUEST_CACHEABLE, 
		REQUEST_FORCE_UPDATE_DATA, 
		REQUEST_CACHE_EXPIRATION
	};
	
	private Bundle mBundle = new Bundle();
	
	public DataSourceRequest() {	
		
	}
	
	public DataSourceRequest(String requestDataUri) {
		setUri(requestDataUri);
	}

    public void setUri(String uri) {
        mBundle.putString(REQUEST_URI, uri);
    }

	public String getUri() {
		return mBundle.getString(REQUEST_URI);
	}
	
	public void setCacheable(boolean isCacheable) {
		mBundle.putString(REQUEST_CACHEABLE, String.valueOf(isCacheable));
	}
	
	public boolean isCacheable() {
		String value = mBundle.getString(REQUEST_CACHEABLE);
		return Boolean.valueOf(value);
	}

	public void setParentUri(String parentUri) {
		mBundle.putString(REQUEST_PARENT_URI, parentUri);
	}

	public String getRequestParentUri() {
		return mBundle.getString(REQUEST_PARENT_URI);
	}

	public void setForceUpdateData(boolean isForceFreshData) {
		mBundle.putString(REQUEST_FORCE_UPDATE_DATA, String.valueOf(isForceFreshData));
	}
	
	public boolean isForceUpdateData() {
		String value = mBundle.getString(REQUEST_FORCE_UPDATE_DATA);
		return Boolean.valueOf(value);
	}
	
	public void setCacheExpiration(long cacheExpirationInMillis) {
		mBundle.putString(REQUEST_CACHE_EXPIRATION, String.valueOf(cacheExpirationInMillis));
	}
	
	public long getCacheExpiration() {
		String value = mBundle.getString(REQUEST_CACHE_EXPIRATION);
		if (TextUtils.isEmpty(value)) {
			return CACHE_EXPIRATION_NONE;
		} else {
			return Long.parseLong(value);
		}
	}
	
	public void putParam(String key, String value) {
		checkIfParamIsNotRestricted(key);
		mBundle.putString(key, value);
	}
	
	public void putStringParams(Bundle bundle) {
		Set<String> params = bundle.keySet();
		for (String key : params) {
			checkIfParamIsNotRestricted(key);
		}
		mBundle.putAll(bundle);
	}
	
	private void checkIfParamIsNotRestricted(String key) {
		for (String privateKey : KEYS) {
			if (privateKey.equalsIgnoreCase(key)) {
				throw new IllegalArgumentException(key + " is reserved by DataSourceRequest class and can't be used.");
			}
		}
	}
	
	public String getParam(String key) {
        String value = mBundle.getString(key);
        if (value == null) {
            String uri = mBundle.getString(REQUEST_URI);
            return Uri.parse(uri).getQueryParameter(key);
        }
        return value;
	}
	
	public String toUriParams() {
		StringBuffer buffer = new StringBuffer();
		Set<String> keySet = mBundle.keySet();
        List<String> sortedKeys = new ArrayList<String>();
        sortedKeys.addAll(keySet);
        Collections.sort(sortedKeys);
		for (Iterator<String> iterator = sortedKeys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			buffer.append(key);
			buffer.append("=");
            //double encoding for correct decoding pagings uris
			buffer.append(StringUtil.encode(StringUtil.encode(mBundle.getString(key))));
			if (iterator.hasNext()) {
				buffer.append("&");	
			}
		}
		return buffer.toString();
	}
	
	public void toIntent(Intent intent) {
        ModelContract.dataSourceRequestToIntent(intent, mBundle);
	}

	public void toBundle(Bundle bundle) {
        ModelContract.dataSourceRequestToBundle(bundle, mBundle);
	}
	
	public static DataSourceRequest fromBundle(Bundle bundle) {
		DataSourceRequest data = new DataSourceRequest();
		data.mBundle = ModelContract.getDataSourceFromBundle(bundle);
		return data;
	}
	
	public static DataSourceRequest fromIntent(Intent intent) {
		DataSourceRequest data = new DataSourceRequest();
		data.mBundle = ModelContract.getDataSourceFromIntent(intent);
		return data;
	}
	
	public static DataSourceRequest fromUri(Uri uri) {
		DataSourceRequest requestData = new DataSourceRequest();
		Set<String> queryParameters = UriUtils.getQueryParameters(uri);
		for (String key : queryParameters) {
			String value = uri.getQueryParameter(key);
			if (!TextUtils.isEmpty(value)) {
				requestData.mBundle.putString(key, StringUtil.decode(value));
			}
		}
		return requestData;
	}
	
}