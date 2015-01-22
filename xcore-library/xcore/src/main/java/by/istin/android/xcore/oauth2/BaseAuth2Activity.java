package by.istin.android.xcore.oauth2;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.UiUtil;

public abstract class BaseAuth2Activity extends FragmentActivity {

    private WebView mWebView;

    private Configuration mConfiguration;

    private OAuth2Helper mOAuth2Helper;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfiguration = getConfiguration();
        mOAuth2Helper = getOAuth2Helper();
        mWebView = initView();
        if (UiUtil.hasHoneycomb()) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new CustomWebViewClient());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String url = mOAuth2Helper.getUrl();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl(url);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleException(e);
                        }
                    });
                }
            }
        }).start();
    }

    protected WebView initView() {
        WebView webView = new WebView(this);
        setContentView(mWebView);
        return webView;
    }

    private void handleException(Exception e) {
        dismissProgress();
        Log.xe(this, e);
        e.printStackTrace();
    }

    protected OAuth2Helper getOAuth2Helper() {
        return OAuth2Helper.Impl.getInstance(mConfiguration);
    };

    protected abstract Configuration getConfiguration();

    private class CustomWebViewClient extends WebViewClient {

        public CustomWebViewClient() {
            super();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.xd(BaseAuth2Activity.this, "page started " + url);
            showProgress();
        }



        /* (non-Javadoc)
         * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            Log.xd(BaseAuth2Activity.this, "overr " + url);
            if (url.startsWith(mConfiguration.getRedirectUrl())) {
                mWebView.setVisibility(View.INVISIBLE);
                showProgress();
                handleRedirectUrl(url);
                return true;
            } else {
                mWebView.setVisibility(View.VISIBLE);
                return false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //showProgress("Error: " + description);
            Log.xd(BaseAuth2Activity.this, "error " + failingUrl);
            dismissProgress();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.xd(BaseAuth2Activity.this, "finish " + url);
            dismissProgress();
            if (url.startsWith(mConfiguration.getRedirectUrl())) {
                mWebView.setVisibility(View.INVISIBLE);
                showProgress();
                handleRedirectUrl(url);
            } else {
                mWebView.setVisibility(View.VISIBLE);
                dismissProgress();
            }
        }

    }

    private void handleRedirectUrl(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Credentials result = mOAuth2Helper.processUrl(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onSuccess(result);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleException(e);
                        }
                    });
                }
            }
        }).start();
    }

    protected void onSuccess(Credentials result) {
        setResult(RESULT_OK);
        finish();
    }

    protected void dismissProgress() {

    }

    protected void showProgress() {

    }
}
