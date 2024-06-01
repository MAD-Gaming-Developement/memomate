package com.deyang.memomate.tools;


import androidx.browser.customtabs.CustomTabsClient;

/**
 * Callback for events when connecting and disconnecting from CustomTabs Service
 */
public interface ServiceConnectionCallback {


    /**
     * Called when the service is connected
     * @param client client of a CustomTabsClient
     */
    void onServiceConnected(CustomTabsClient client);


    /**
     *  Called when service is disconnected
     */
    void onServiceDisconnected();

}
