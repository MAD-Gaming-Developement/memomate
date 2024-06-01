package com.deyang.memomate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.onesignal.inAppMessages.IInAppMessageClickEvent;
import com.onesignal.inAppMessages.IInAppMessageClickListener;

/**
 * SplashScreen activity displays a splash video and initializes OneSignal for push notifications and in-app messages.
 */
public class SplashScreen extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "9d3e6c8d-8f24-4236-a5a7-fc7fa3afecd2";

    /**
     * Called  when the activity is first created.
     * Initializes OneSignal and sets up the splash video.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        OneSignalInitialization();

        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);

        videoView.setVideoURI(videoUri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        videoView.start();
    }

    /**
     * Initializes OneSignal for push notifications and in-app messages.
     */
    private void OneSignalInitialization() {
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // OneSignal.getNotifications().requestPermission(false, Continue.none());

        OneSignal.getInAppMessages().addClickListener(new IInAppMessageClickListener() {
            @Override
            public void onClick(@NonNull IInAppMessageClickEvent iInAppMessageClickEvent) {
                Log.w("OneSignal", iInAppMessageClickEvent.getResult().getActionId());

                String[] inAppEvent = iInAppMessageClickEvent.getResult().getActionId().split("\\|");

                Intent openAppPromotion = new Intent(SplashScreen.this, EventPromotion.class);
                openAppPromotion.putExtra("promotionURL", inAppEvent[1]);
                startActivity(openAppPromotion);
            }
        });
    }
}
