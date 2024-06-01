package com.deyang.memomate.tools;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.deyang.memomate.R;

public class ActionBroadcastReceiver extends BroadcastReceiver {
    public static final String KEY_ACTION_SOURCE = "com.deyang.memomate.ACTION_SOURCE";
    public static final int ACTION_SHARE_BTN = 1;
    public static final int ACTION_POLICY_BTN = 2;
    public static final int ACTION_ABOUT = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if(url != null)
        {
            String toastText = getToastText(context, intent.getIntExtra(KEY_ACTION_SOURCE, -1), url);
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StringFormatInvalid")
    private String getToastText(Context mContext, int actionID, String url)
    {
        switch(actionID)
        {
            case ACTION_SHARE_BTN:
                // Additional activity or action
                return mContext.getString(R.string.action_share, url);
            case ACTION_POLICY_BTN:
                return mContext.getString(R.string.action_policy, url);
            case ACTION_ABOUT:
                return mContext.getString(R.string.action_about, url);

            default:
                return mContext.getString(R.string.action_invalid, url);
        }
    }
}
