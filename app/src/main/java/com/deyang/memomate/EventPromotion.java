package com.deyang.memomate;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

import com.deyang.memomate.tools.ActionBroadcastReceiver;
import com.deyang.memomate.tools.CustomTabsHelper;

/**
 * EventPromotion activity displays a promotional webpage in a WebView.
 * The URL of  the  promotional webpage is passed via an Intent.
 */
public class EventPromotion extends AppCompatActivity {

    private CustomTabsHelper mCustomTabsHelper;
    private String promotionURL;

    /**
     * Called when the activity is first created.
     * Initializes the WebView and loads the promotional URL.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_promotion);

        Intent inAppIntent = getIntent();
        promotionURL = inAppIntent.getStringExtra("promotionURL");

        if (TextUtils.isEmpty(promotionURL) || promotionURL == null) {
            finish();
        }

        mCustomTabsHelper = new CustomTabsHelper();

        Log.e("Loaded URL", promotionURL);

        openCTI(promotionURL);
    }

    private void openCTI(String launchURL)
    {
        CustomTabsIntent.Builder ctiBuilder = new CustomTabsIntent.Builder();

        // Custom Colors
        CustomTabColorSchemeParams defaultColors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(Color.argb(255, 200, 100, 100))
                .setSecondaryToolbarColor(Color.RED)
                .build();
        ctiBuilder.setDefaultColorSchemeParams(defaultColors);

        // Custom Menu Share
        String shareLabel = "Share";
        Bitmap shareIcon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_share);
        PendingIntent shareIntent = createPendingIntent(ActionBroadcastReceiver.ACTION_SHARE_BTN);

        ctiBuilder.setActionButton(shareIcon, shareLabel, shareIntent);

        // Policy
        String policyLabel = "Privacy Policy";
        Bitmap policyIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_policy);
        PendingIntent policyIntent = createPendingIntent(ActionBroadcastReceiver.ACTION_POLICY_BTN);

        ctiBuilder.setActionButton(policyIcon, policyLabel, policyIntent);

        // Custom Tab Intent
        ctiBuilder.setShowTitle(false);
        ctiBuilder.setUrlBarHidingEnabled(true);

        // Replace Back Button Icon
        ctiBuilder.setCloseButtonIcon(toBitmap(getDrawable(R.drawable.ic_back)));

        // Load URL in Custom Tab Intent
        CustomTabsHelper.openCustomTab(this, ctiBuilder.build(), Uri.parse(launchURL), new CustomTabsHelper.CustomTabFallback() {
            @Override
            public void openURL(Activity mActivity, Uri url) {
                // Open External
                Intent openExternal = new Intent(Intent.ACTION_VIEW);
                openExternal.putExtra("URL", url);
                startActivity(openExternal);
            }
        });
    }

    private Bitmap toBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Rect oldBounds = new Rect(drawable.getBounds());

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(new Canvas(bitmap));

        drawable.setBounds(oldBounds);
        return bitmap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCustomTabsHelper != null)
            mCustomTabsHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCustomTabsHelper != null)
            mCustomTabsHelper.unbindCustomTabsService(this);
    }

    private PendingIntent createPendingIntent(int actionSourceID)
    {
        Intent actionIntent = new Intent(this.getApplicationContext(), ActionBroadcastReceiver.class);
        actionIntent.putExtra(ActionBroadcastReceiver.KEY_ACTION_SOURCE, actionSourceID);
        return PendingIntent.getBroadcast(getApplicationContext(), actionSourceID, actionIntent, PendingIntent.FLAG_MUTABLE);
    }

}