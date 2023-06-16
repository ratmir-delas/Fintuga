package com.tugasoft.fintuga.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tugasoft.fintuga.MainActivity;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.ads.AdsProvider;


public class SplashActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundContentColor));
        getWindow().setFlags(1024, 1024);
        setContentView((int) R.layout.activity_splash);

        CallIntent();
    }


    private void CallIntent() {

//        AdsProvider.getInstance().showInterstitialAd(this, () -> {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
//            finish();
//        });

        new Handler().postDelayed(new Runnable() {
            public final void run() {
                lambda$CallIntent$0$Splash_Activity();
            }
        }, 2000);
    }

    public void lambda$CallIntent$0$Splash_Activity() {
        AdsProvider.getInstance().showInterstitialAd(this, () -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });
    }


}
