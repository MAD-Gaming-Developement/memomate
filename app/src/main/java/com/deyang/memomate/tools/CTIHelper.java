package com.deyang.memomate.tools;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CTIHelper {

    //region [ Variables ]
    private static final String TAG = "CTIHelper";
    static final String STABLE_PACKAGE = "com.android.chrome";
    static final String BETA_PACKAGE = "com.chrome.beta";
    static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";

    private static final String EXTRA_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE";
    private static final String EXTRA_CTI_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private static String packageNameUse;
    //endregion

    private CTIHelper() {}


    /**
     * Keeps the curent intent of context alive when session or service is connected
     * @param mContext host context of activity
     * @param intent the intent filter to call
     */
    public static void addKeepAlive(Context mContext, Intent intent)
    {
        Intent keepAliveIntent = new Intent().setClassName(mContext.getPackageName(), KeepAliveService.class.getCanonicalName());
        intent.putExtra(EXTRA_KEEP_ALIVE, keepAliveIntent);
    }

    /**
     * Get all apps that handle VIEW intents (Browsers, Special Apps (FaceBook, Telegram, Messenger etc...) and a warmup service.
     * Picks the one chosen by user if there is one, otherwise return a valid option by default.
     *
     * @param mContext calling host activity to be used for the intent
     * @return the packagename recommended to use for connecting to Custom Tabs Intent related components.
     */
    public static String getPackageCTI(Context mContext)
    {
        if(packageNameUse != null) return packageNameUse;

        PackageManager pm = mContext.getPackageManager();
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com"));
        ResolveInfo defaultHandler = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandler = null;

        if(defaultHandler != null)
            defaultViewHandler = defaultHandler.activityInfo.packageName;

        List<ResolveInfo> resolveActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packageSupportCTI = new ArrayList<>();

        // Check available system browsers that support Custom Tabs Intent according to packagename
        for(ResolveInfo info : resolveActivityList)
        {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(EXTRA_CTI_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if(pm.resolveService(serviceIntent, 0) != null)
                packageSupportCTI.add(info.activityInfo.packageName);
        }

        if(packageSupportCTI.isEmpty())
            packageNameUse = null;
        else if (!TextUtils.isEmpty(defaultViewHandler)
                && !hasSpecialIntent(mContext, activityIntent)
                && packageSupportCTI.contains(defaultViewHandler))
            packageNameUse = defaultViewHandler;
        else
            packageNameUse = packageSupportCTI.get(0);

        return packageNameUse;
    }

    /**
     * Used to check whether it is a specialized intent or app handler for a given intent.
     * @param mContext the host activity
     * @param activityIntent the intent to check
     * @return whether there is a specialized app handler for the given intent
     */
    private static boolean hasSpecialIntent(Context mContext, Intent activityIntent)
    {
        try
        {
            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(activityIntent, PackageManager.GET_RESOLVED_FILTER);
            if(handlers.size() == 0)
                return false;

            for(ResolveInfo resolveInfo : handlers)
            {
                IntentFilter filter = resolveInfo.filter;
                if(filter == null) continue;
                if(filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue;
                if(resolveInfo.activityInfo == null) continue;
                return true;
            }
        }
        catch (RuntimeException e)
        {
            Log.e(TAG, "Runtime exception while getting specialized Intent filter apps. (Facebook, Telegram, Messenger etc.");
        }

        return false;
    }

    /**
     * @return All possible chrome package that provides custom tab feature
     */
    public static String[] getPackages() {
        return new String[]{
                "",
                STABLE_PACKAGE,
                BETA_PACKAGE,
                LOCAL_PACKAGE
        };
    }

}
