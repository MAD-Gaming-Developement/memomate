package com.deyang.memomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


import androidx.appcompat.app.AppCompatActivity;

/**
 * EventPromotion activity displays a promotional webpage in a WebView.
 * The URL of  the  promotional webpage is passed via an Intent.
 */
public class EventPromotion extends AppCompatActivity {

    private GlobalWebSetting inAppPromotion;
    private String promotionURL;

    /**
     * Called when the activity is first created.
     * Initializes the WebView and loads the promotional URL.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_promotion);

        Intent inAppIntent = getIntent();
        promotionURL = inAppIntent.getStringExtra("promotionURL");

        if (TextUtils.isEmpty(promotionURL) || promotionURL == null) {
            finish();
        }

        inAppPromotion = findViewById(R.id.promotionView);
        inAppPromotion.loadUrl(promotionURL);
    }
}