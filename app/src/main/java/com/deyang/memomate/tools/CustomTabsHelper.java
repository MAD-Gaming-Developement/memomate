package com.deyang.memomate.tools;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

import java.util.List;

/**
 *  This is the Helper class to manage connection to the Custom Tabs Service
 */
public class CustomTabsHelper implements ServiceConnectionCallback {

    private CustomTabsSession mCTSession;
    private CustomTabsClient mCTClient;
    private CustomTabsServiceConnection mCTConnection;
    private ConnectionCallback mConnectionCallback;

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        mCTClient = client;
        mCTClient.warmup(0L);
        if(mConnectionCallback != null) mConnectionCallback.onCustomTabsConnected();
    }

    @Override
    public void onServiceDisconnected() {
        mCTClient = null;
        mCTSession = null;
        if(mConnectionCallback != null) mConnectionCallback.onCustomTabsDisconnected();
    }

    /**
     * Opens the URL on a CustomTabsIntent if possible, otherwise fallback to external browser
     *
     * @param mActivity host of activity
     * @param customTabsIntent a CustomTabsIntent to be used if Chrome Tabs is available
     * @param uri the URL to be opened
     * @param fallback a custom fallback if no Chrome Tabs is available
     */
    public static void openCustomTab(Activity mActivity, CustomTabsIntent customTabsIntent, Uri uri, CustomTabFallback fallback)
    {
        String packageName = CTIHelper.getPackageCTI(mActivity);

        // If cannot find a packagename, it means there is no browser that supports Chrome Tabs, fall back to System Browser
        if(packageName == null)
        {
            if(fallback != null)
                fallback.openURL(mActivity, uri);
        }
        else
        {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(mActivity, uri);
        }
    }

    /**
     * Unbinds the Activity from the CustomTabsIntent service
     * @param mActivity
     */
    public void unbindCustomTabsService(Activity mActivity)
    {
        if(mCTConnection == null) return;

        mActivity.unbindService(mCTConnection);
        mCTClient = null;
        mCTSession = null;
        mCTConnection = null;
    }

    /**
     * Binds the host Activity to the CustomTabsService
     * @param mActivity the host activity to bind to service
     */
    public void bindCustomTabsService(Activity mActivity)
    {
        if(mCTClient != null) return;

        String packageName = CTIHelper.getPackageCTI(mActivity);
        if(packageName == null) return;

        mCTConnection = new ServiceConnection(this);
        CustomTabsClient.bindCustomTabsService(mActivity, packageName, mCTConnection);
    }

    /**
     * Creates or retrieves an existing CustomTabsSession within the connected service
     * @return a CustomTabsSession
     */
    public CustomTabsSession getSession() {
        if (mCTClient == null) {
            mCTSession = null;
        } else if (mCTSession == null)
            mCTSession = mCTClient.newSession(null);

        return mCTSession;
    }

    /**
     * Register a Callback from activity when connected or disconnected from Custom Tabs Service
     * @param connectionCallback
     */
    public void setConnectionCallback(ConnectionCallback connectionCallback)
    {
        this.mConnectionCallback = connectionCallback;
    }

    /**
     * Check if the URL can be connected to or not
     * @return true if call to mayLaunchURL was accepted or valid
     */
    public boolean mayLaunchURL(Uri uri, Bundle extras, List<Bundle> otherBundles)
    {
        if(mCTClient == null) return false;

        CustomTabsSession session = getSession();
        if(session == null) return false;

        return session.mayLaunchUrl(uri, extras, otherBundles);
    }


    /**
     * A callback for when a service is connected or disconnected and update the CustomTabsIntent UI on changes.
     */
    public interface ConnectionCallback {
        void onCustomTabsConnected();

        void onCustomTabsDisconnected();
    }


    /**
     * To be used as a fallback to open URL when Custom Tabs is not available.
     */
    public interface CustomTabFallback {
        void openURL (Activity mActivity, Uri url);
    }
}
