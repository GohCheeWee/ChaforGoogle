package com.jby.chaforgoogle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.jby.chaforgoogle.exportFeature.file.ExportFileActivity;
import com.jby.chaforgoogle.login.LoginActivity;
import com.jby.chaforgoogle.shareObject.AnimationUtility;
import com.jby.chaforgoogle.sharePreference.SharedPreferenceManager;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView image;
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.activity_splash_screen_image_view);
        label = findViewById(R.id.activity_splash_screen_label);

        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(4*1000);
                    intentSetting();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
        new AnimationUtility().fastFadeInVisible(SplashScreenActivity.this, image);
        new AnimationUtility().fadeInVisible(SplashScreenActivity.this, label);
    }

    private void intentSetting(){
        String userID = SharedPreferenceManager.getUserID(this);
        if(!userID.equals("0"))
            startActivity(new Intent(this, ExportFileActivity.class));
        else
            startActivity(new Intent(this, LoginActivity.class));

        finish();
    }
}
