package by.istin.android.xcore.source;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UriUtils;

public class DataSourceRequest {

    public static final long CACHE_EXPIRATION_NONE = -1l;

    private static final String REQUEST_URI = "___ruri";

    private static final String REQUEST_PARENT_URI = "___parent_ruri";

    private static final String REQUEST_CACHEABLE = "___c";

    private static final String REQUEST_FORCE_UPDATE_DATA = "___fud";

    private static final String REQUEST_CACHE_EXPIRATION = "___exp";

    private static final String JOIN_REQUEST = "___joinrequest";

    private static final String JOIN_PROCESSOR_KEY = "___joinprocessor";

    private static final String JOIN_DATASOURCE_KEY = "___joindatasource";

    private static final String REQUEST_PROCESSOR_KEY = "___processorkey";

    private static final String REQUEST_DATA_SOURCE_KEY = "___datasourcekey";

    private static final String[] KEYS = new String[]{
            REQUEST_URI,
            REQUEST_PARENT_URI,
            REQUEST_CACHEABLE,
            REQUEST_FORCE_UPDATE_DATA,
            REQUEST_CACHE_EXPIRATION,
            REQUEST_PROCESSOR_KEY,
            REQUEST_DATA_SOURCE_KEY
    };

    private Bundle mBundle = new Bundle();

    private Map<String, Holder<String>> mParamsCache = new ConcurrentHashMap<>();

    private Uri mCacheUri = null;

    public DataSourceRequest() {

    }

    public DataSourceRequest(String requestDataUri) {
        setUri(requestDataUri);
    }

    public void setUri(String uri) {
        mCacheUri = null;
        mBundle.putString(REQUEST_URI, uri);
    }

    public String getUri() {
        return mBundle.getString(REQUEST_URI);
    }

    public Uri getRequestUri() {
        if (mCacheUri == null) {
            mCacheUri = Uri.parse(getUri());
        }
        return mCacheUri;
    }

    public void setCacheable(boolean isCacheable) {
        mBundle.putString(REQUEST_CACHEABLE, String.valueOf(isCacheable));
    }

    public boolean isCacheable() {
        String value = mBundle.getString(REQUEST_CACHEABLE);
        return Boolean.valueOf(value);
    }

    public void setProcessorKey(String processorKey) {
        mBundle.putString(REQUEST_PROCESSOR_KEY, processorKey);
    }

    public String getProcessorKey() {
        return mBundle.getString(REQUEST_PROCESSOR_KEY);
    }

    public void setDataSourceKey(String dataSourceKey) {
        mBundle.putString(REQUEST_DATA_SOURCE_KEY, dataSourceKey);
    }

    public String getDataSourceKey() {
        return mBundle.getString(REQUEST_DATA_SOURCE_KEY);
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
        if (!Log.isDebug()) return;
        for (String privateKey : KEYS) {
            if (privateKey.equalsIgnoreCase(key)) {
                throw new IllegalArgumentException(key + " is reserved by DataSourceRequest class and can't be used.");
            }
        }
    }

    public String getParam(String key) {
        String value = mBundle.getString(key);
        if (value == null) {
            Holder<String> stringHolder = mParamsCache.get(key);
            value = stringHolder == null ? null : stringHolder.get();
            if (value == null) {
                String queryParameter = getRequestUri().getQueryParameter(key);
                mParamsCache.put(key, new Holder<>(queryParameter));
                return queryParameter;
            }
        }
        return value;
    }

    public String toUriParams() {
        StringBuilder buffer = new StringBuilder();
        Set<String> keySet = mBundle.keySet();
        List<String> sortedKeys = new ArrayList<>();
        sortedKeys.addAll(keySet);
        Collections.sort(sortedKeys);
        for (Iterator<String> iterator = sortedKeys.iterator(); iterator.hasNext(); ) {
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
        ModelContract.dataSourceRequestToBundle(bundle, toUriParams());
    }

    public void joinRequest(DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey) {
        if (dataSourceRequest == null) {
            throw new IllegalArgumentException("dataSourceRequest can not be null");
        }
        if (processorKey == null) {
            throw new IllegalArgumentException("processorKey can not be null");
        }
        if (dataSourceKey == null) {
            throw new IllegalArgumentException("dataSourceKey can not be null");
        }
        mBundle.putString(JOIN_REQUEST, dataSourceRequest.toUriParams());
        mBundle.putString(JOIN_PROCESSOR_KEY, processorKey);
        mBundle.putString(JOIN_DATASOURCE_KEY, dataSourceKey);
    }

    public DataSourceRequest getJoinedRequest() {
        String joinedRequest = mBundle.getString(JOIN_REQUEST);
        if (StringUtil.isEmpty(joinedRequest)) {
            return null;
        }
        return fromUri(Uri.parse("content://temp?" + joinedRequest));
    }

    public String getJoinedProcessorKey() {
        return mBundle.getString(JOIN_PROCESSOR_KEY);
    }

    public String getJoinedDataSourceKey() {
        return mBundle.getString(JOIN_DATASOURCE_KEY);
    }

    public static DataSourceRequest fromBundle(Bundle bundle) {
        return ModelContract.getDataSourceFromBundle(bundle);
    }

    public static DataSourceRequest fromIntent(Intent intent) {
        DataSourceRequest data = new DataSourceRequest();
        Bundle dataSourceFromIntent = ModelContract.getDataSourceFromIntent(intent);
        if (dataSourceFromIntent != null) {
            data.mBundle = dataSourceFromIntent;
        }
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

    public static class JoinedRequestBuilder {

        private static class RequestConfig {

            private String processorKey;

            private String dataSourceKey;

            private DataSourceRequest dataSourceRequest;
        }

        private final List<RequestConfig> dataSourceRequests = new ArrayList<>();

        private String dataSourceKey;

        private String processorKey;

        public JoinedRequestBuilder(DataSourceRequest dataSourceRequest) {
            RequestConfig requestConfig = new RequestConfig();
            requestConfig.dataSourceRequest = dataSourceRequest;
            dataSourceRequests.add(requestConfig);
        }

        public JoinedRequestBuilder setDataSource(String dataSourceKey) {
            this.dataSourceKey = dataSourceKey;
            return this;
        }

        public JoinedRequestBuilder setProcessor(String processorKey) {
            this.processorKey = processorKey;
            return this;
        }

        public String getDataSourceKey() {
            return dataSourceKey;
        }

        public String getProcessorKey() {
            return processorKey;
        }

        public JoinedRequestBuilder add(DataSourceRequest dataSourceRequest) {
            if (StringUtil.isEmpty(dataSourceKey)) {
                throw new IllegalStateException("default dataSource is not set, call JoinedRequestBuilder.setDataSource to set");
            }
            if (StringUtil.isEmpty(processorKey)) {
                throw new IllegalStateException("default processorKey is not set, call JoinedRequestBuilder.setProcessor to set");
            }
            return add(dataSourceRequest, processorKey, dataSourceKey);
        }

        public JoinedRequestBuilder add(DataSourceRequest dataSourceRequest, String processorKey) {
            if (StringUtil.isEmpty(dataSourceKey)) {
                throw new IllegalStateException("default dataSource is not set, call JoinedRequestBuilder.setDataSource to set");
            }
            return add(dataSourceRequest, processorKey, dataSourceKey);
        }

        public JoinedRequestBuilder add(DataSourceRequest dataSourceRequest, String processorKey, String dataSourceKey) {
            RequestConfig requestConfig = new RequestConfig();
            requestConfig.dataSourceRequest = dataSourceRequest;
            requestConfig.processorKey = processorKey;
            requestConfig.dataSourceKey = dataSourceKey;
            dataSourceRequests.add(requestConfig);
            return this;
        }

        public DataSourceRequest build() {
            if (dataSourceRequests.isEmpty()) {
                return null;
            }
            for (int i = dataSourceRequests.size() - 1; i > 0; i--) {
                RequestConfig requestConfig = dataSourceRequests.get(i);
                RequestConfig prevRequest = dataSourceRequests.get(i - 1);
                prevRequest.dataSourceRequest.joinRequest(requestConfig.dataSourceRequest, requestConfig.processorKey, requestConfig.dataSourceKey);

            }
            return dataSourceRequests.get(0).dataSourceRequest;
        }
    }
}