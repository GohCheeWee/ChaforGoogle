package com.jby.chafor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.ads.MobileAds;
import com.jby.chafor.exportFeature.category.ExportCategoryActivity;;
import com.jby.chafor.login.LoginActivity;
import com.jby.chafor.shareObject.AnimationUtility;
import com.jby.chafor.sharePreference.SharedPreferenceManager;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getResources().getString(R.string.ADMOB_APP_ID));

        image = findViewById(R.id.activity_splash_screen_image_view);
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
    }

    private void intentSetting(){
        String userID = SharedPreferenceManager.getUserID(this);
        if(!userID.equals("0"))
            startActivity(new Intent(this, ExportCategoryActivity.class));
        else
            startActivity(new Intent(this, LoginActivity.class));

        finish();
    }
}
