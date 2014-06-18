package by.istin.android.xcore.source.impl.http;

import android.net.http.AndroidHttpClient;

import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.utils.IOUtils;

/**
 * Created by Uladzimir_Klyshevich on 6/2/2014.
 */
public class InputStreamHttpClientHelper {

    private String mUserAgent;

    private Object mLock = new Object();

    private Set<InputStream> mStreams = new HashSet<InputStream>();

    private boolean isAndroidHttpClient = false;

    public InputStreamHttpClientHelper(String userAgent) {
        this.mUserAgent = userAgent;
    }

    /* Apache client. */
    private HttpClient mClient;

    public HttpClient getClient() {
        synchronized (mLock) {
            if (mClient == null) {
                mClient = createHttpClient();
            }
            if (mClient instanceof AndroidHttpClient) {
                isAndroidHttpClient = true;
            }
            return mClient;
        }
    }

    public HttpClient createHttpClient() {
        return AndroidHttpClient.newInstance(mUserAgent);
    }

    public InputStream getInputStream(InputStream pInputStream, HttpClient client) {
        if (!isAndroidHttpClient) {
            return pInputStream;
        }
        synchronized (mLock) {
            InputStreamWrapper inputStreamWrapper = new InputStreamWrapper(pInputStream);
            mStreams.add(inputStreamWrapper);
            return inputStreamWrapper;
        }
    }

    private class InputStreamWrapper extends InputStream {

        private final InputStream mInputStream;


        private InputStreamWrapper(InputStream mInputStream) {
            this.mInputStream = mInputStream;
        }

        @Override
        public int read() throws IOException {
            return mInputStream.read();
        }

        @Override
        public int available() throws IOException {
            return mInputStream.available();
        }

        @Override
        public void close() throws IOException {
            synchronized (mLock) {
                try {
                    mInputStream.close();
                    super.close();
                } finally {
                    mStreams.remove(this);
                    if (mStreams.isEmpty()) {
                        ((AndroidHttpClient) mClient).close();
                        mClient = null;
                    }
                }
            }
        }

        @Override
        public void mark(int readlimit) {
            mInputStream.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return mInputStream.markSupported();
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            return mInputStream.read(buffer);
        }

        @Override
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            return mInputStream.read(buffer, byteOffset, byteCount);
        }

        @Override
        public synchronized void reset() throws IOException {
            mInputStream.reset();
        }

        @Override
        public long skip(long byteCount) throws IOException {
            return mInputStream.skip(byteCount);
        }

        @Override
        public boolean equals(Object o) {
            return mInputStream.equals(o);
        }

        @Override
        public int hashCode() {
            return mInputStream.hashCode();
        }

        @Override
        public String toString() {
            return mInputStream.toString();
        }
    }

}
