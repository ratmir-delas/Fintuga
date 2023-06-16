package com.tugasoft.fintuga.application;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;
import com.tugasoft.fintuga.BuildConfig;
import com.tugasoft.fintuga.R;
import com.tugasoft.fintuga.utils.MySharedPreferences;
import com.tugasoft.fintuga.ads.AdsConstants;
import com.tugasoft.fintuga.ads.AppOpenManager;

public class AppCore extends Application {

    private static Context context;
    public static InterstitialAd admobInterstitial = null;
    public static RewardedAd admobRewardAd = null;
    private static AppOpenManager appOpenManager = null;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        FirebaseApp.initializeApp(this);
        super.onCreate();

        AdsConstants.isBanner = getResources().getBoolean(R.bool.isBanner); // is banner Ads are on or off...
        AdsConstants.isInterstitial = getResources().getBoolean(R.bool.isInterstitial); // is interstitial Ads are on or off...
        AdsConstants.isNative = getResources().getBoolean(R.bool.isNative); // is native Ads are on or off...
        AdsConstants.isAppOpen = getResources().getBoolean(R.bool.isAppOpen); // is isAppOpen Ads are on or off...
        AdsConstants.isReward = getResources().getBoolean(R.bool.isReward); // is reward Ads are on or off...

        AdsConstants.ADMOB_BANNER_ID = getString(R.string.bannerId);
        AdsConstants.ADMOB_NATIVE_ID = getString(R.string.nativeId);
        AdsConstants.ADMOB_INTERSTITIAL_ID = getString(R.string.interstitialId);
        AdsConstants.ADMOB_APP_OPEN_ID = getString(R.string.appOpenId);
        AdsConstants.ADMOB_REWARDED_ID = getString(R.string.rewardId);


        MultiDex.install(this);
        context = getApplicationContext();
        MySharedPreferences.getInstance();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(BuildConfig.ONE_SIGNAL_APP_ID);


        MobileAds.initialize(this, initializationStatus -> {});

        if (AdsConstants.isInterstitial && AdsConstants.ADMOB_INTERSTITIAL_ID != null && !AdsConstants.ADMOB_INTERSTITIAL_ID.equals("")) {
            AppCore.loadAdmobInterstitial(this, AdsConstants.ADMOB_INTERSTITIAL_ID);
        }
        if (AdsConstants.isReward && AdsConstants.ADMOB_REWARDED_ID != null && !AdsConstants.ADMOB_REWARDED_ID.equals("")) {
            AppCore.loadAdmobRewarded(this, AdsConstants.ADMOB_REWARDED_ID);
        }
        if (AdsConstants.isAppOpen && appOpenManager == null && AdsConstants.ADMOB_APP_OPEN_ID != null && !AdsConstants.ADMOB_APP_OPEN_ID.equals("")) {
            appOpenManager = new AppOpenManager(this);
        }
    }

    @Override
    public void attachBaseContext(Context context2) {
        super.attachBaseContext(context2);
        MultiDex.install(this);
    }

    public static void loadAdmobInterstitial(Context context, String placement_id) {
        if (AppCore.admobInterstitial != null) {
            AppCore.admobInterstitial = null;
        }
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, placement_id, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                AppCore.admobInterstitial = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                AppCore.admobInterstitial = null;
            }
        });
    }

    public static void loadAdmobRewarded(Context context, String placement_id) {
        if (AppCore.admobRewardAd != null) {
            AppCore.admobRewardAd = null;
        }
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, placement_id, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                AppCore.admobRewardAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                AppCore.admobRewardAd = rewardedAd;
            }
        });
    }


    public static String replaceSpecialCharacters(String str) {

        return str.replace(":", "_")
                .replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace(".", "_")
                .replace("$", "_")
                .replace("@", "_")
                .replace("!", "_")
                .replace("%", "_")
                .replace("^", "_")
                .replace("*", "_")
                .replace("(", "_")
                .replace(")", "_")
                .replace("-", "_")
                .replace("?", "_")
                .replace(">", "_")
                .replace("<", "_")
                .replace("|", "_")
                .replace("]", "_")
                .replace("[", "_")
                .replace("{", "_")
                .replace("}", "_")
                .replace("=", "_")
                .replace("+", "_");
    }
}
