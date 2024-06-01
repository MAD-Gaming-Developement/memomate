package com.deyang.memomate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * GlobalWebSetting is a custom WebView with pre-configured settings.
 * It handles specific URL schemes and provides enhanced web browsing features.
 */
public class GlobalWebSetting  extends WebView {


    /**
     * Constructor to initialize GlobalWebSetting with a context.
     *
     * @param context the context in which the WebView is running.
     */
    public GlobalWebSetting(Context context) {
        super(context);
        initWebViewSettings();
    }

    /**
     * Constructor to initialize GlobalWebSetting with a context and attribute set.
     * @param context the context in which the WebView is running.
     * @param attrs the attribute set from the XML layout.
     */
    public GlobalWebSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebViewSettings();
    }

    /**
     * Constructor to initialize GlobalWebSetting with a context, attribute set, and default style.
     * @param context context  the context in which the WebView is running.
     * @param attrs the attribute set from the XML layout.
     * @param defStyle the default style to apply to this view.
     */
    public GlobalWebSetting(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWebViewSettings();
    }

    /**
     * Initializes the settings for the WebView.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSettings() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        setWebViewClient(new CustomWebClient(getContext()));
        setWebChromeClient(new CustomWebChromeClient());

    }

    /**
     * Custom WebViewClient to handle URL loading and special URL schemes.
     */
    private class CustomWebClient extends WebViewClient {

        private Context context;

        /**
         * Constructor to initialize CustomWebClient with a context.
         * @param context the context in which the WebViewClient is running.
         */
        public CustomWebClient(Context context) {
            this.context = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return handleHttpUrl(url);
            } else {
                return handleCustomUrl(url);
            }
        }

        /**
         * Handles HTTP and HTTPS URLs by checking for specific domains and opening them in external apps if needed.
         * @param url the URL to handle.
         * @return true if the URL was handled, false otherwise.
         */
        private boolean handleHttpUrl(String url) {
            if (url.contains("t.me") || url.contains("whatsapp.com") ||
                    url.contains("facebook.com") || url.contains("instagram.com") ||
                    url.contains("twitter.com") || url.contains("chat.ichatlink.net")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
            return false;
        }

        /**
         * Handles custom URLs by checking for specific schemes and opening them in external apps if needed.
         * @param url the URL to handle
         * @return true if  the URL  was handled, false otherwise.
         */
        private boolean handleCustomUrl(String url) {
            try {
                // Handle Facebook URLs
                if (url.startsWith("https://www.facebook.com/")) {
                    // Open the Facebook profile using the username
                    String username = url.substring(url.lastIndexOf("/") + 1);
                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + username));
                    context.startActivity(facebookIntent);
                    return true;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            } catch (Exception e) {
                Log.e("YourTagHere", "Failed to handle URL: " + url, e);
                return false;
            }
        }
    }

    /**
     * Custom WebChromeClient to handle additional features such as opening new windows.
     */
    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
            WebView newWebView = new WebView(getContext());
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.canGoBack()) {
            this.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

