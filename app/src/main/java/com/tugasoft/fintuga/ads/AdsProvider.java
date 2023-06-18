package com.tugasoft.fintuga.ads;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.tugasoft.fintuga.application.AppCore;
import com.tugasoft.fintuga.R;

public class AdsProvider {

    private static AdsProvider instance = null;
    private AdsCallback callback = null;
    private AdsPreferences preferences = null;
    private NativeAd nativeAd00 = null;

    public static synchronized AdsProvider getInstance() {
        AdsProvider application;
        synchronized (AdsProvider.class) {
            application = new AdsProvider();
        }
        return application;
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true; // connected to wifi
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;// connected to the mobile provider's data plan
            }
        }
        return false;
    }

    public boolean addBanner(Context context, View customView) {
        boolean returnStatement = false;
        if (context != null && customView != null) {
            if (checkConnection(context) &&
                    AdsConstants.isBanner &&
                    AdsConstants.ADMOB_BANNER_ID != null &&
                    !AdsConstants.ADMOB_BANNER_ID.equals("")) {
                returnStatement = true;

                AdView mAdView = new AdView(context);
                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(AdsConstants.ADMOB_BANNER_ID);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

                if (customView instanceof LinearLayout) {
                    LinearLayout layout = (LinearLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                } else if (customView instanceof RelativeLayout) {
                    RelativeLayout layout = (RelativeLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                } else if (customView instanceof FrameLayout) {
                    FrameLayout layout = (FrameLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                }

                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // TODO Auto-generated method stub
                        customView.setVisibility(View.VISIBLE);
                        super.onAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }
                });

            }
        }
        return returnStatement;
    }

    public void showInterstitialAd(Activity activity, AdsCallback callback) {
        this.callback = callback;
        this.preferences = new AdsPreferences(activity);

        if (checkConnection(activity) &&
                AdsConstants.isInterstitial &&
                AdsConstants.ADMOB_INTERSTITIAL_ID != null &&
                !AdsConstants.ADMOB_INTERSTITIAL_ID.equals("")) {

            if (AppCore.admobInterstitial != null && preferences.getCount() >= (AdsConstants.INTERSTITIAL_AFTER - 1)) {
                preferences.resetCount();

                AppCore.admobInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        AppCore.loadAdmobInterstitial(activity, AdsConstants.ADMOB_INTERSTITIAL_ID);
                        callListener();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AppCore.loadAdmobInterstitial(activity, AdsConstants.ADMOB_INTERSTITIAL_ID);
                        callListener();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {

                    }
                });
                AppCore.admobInterstitial.show(activity);
            } else {
                preferences.addCount();
                callListener();
            }
        } else {
            callListener();
        }
    }

    public void showRewardedAd(Activity activity, AdsCallback adsCallback) {

        if (preferences == null) {
            this.preferences = new AdsPreferences(activity);
        }

        if (checkConnection(activity) && AdsConstants.isReward && AppCore.admobRewardAd != null) {

            AppCore.admobRewardAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {

                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    AppCore.loadAdmobRewarded(activity, AdsConstants.ADMOB_REWARDED_ID);
                    if (adsCallback != null) {
                        adsCallback.press();
                    }
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    AppCore.loadAdmobRewarded(activity, AdsConstants.ADMOB_REWARDED_ID);
                    if (adsCallback != null) {
                        adsCallback.press();
                    }
                }


            });

            AppCore.admobRewardAd.show(activity, rewardItem -> {
                AppCore.loadAdmobRewarded(activity, AdsConstants.ADMOB_REWARDED_ID);
                if (adsCallback != null) {
                    adsCallback.press();
                }
            });
        }

    }

    public boolean addNativeView(Context mContext, View customView) {
        boolean returnStatement = false;
        if (checkConnection(mContext) && AdsConstants.isNative) {
            returnStatement = true;
            AdLoader.Builder builder = new AdLoader.Builder(mContext, AdsConstants.ADMOB_NATIVE_ID);
            builder.forNativeAd(nativeAd -> {
                if (nativeAd00 != null) {
                    nativeAd00.destroy();
                }
                nativeAd00 = nativeAd;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                NativeAdView mAdView =
                        (NativeAdView) inflater.inflate(R.layout.small_ad_unified, null);
                populateNativeAdView(nativeAd, mAdView);
                if (customView instanceof LinearLayout) {
                    LinearLayout layout = (LinearLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                } else if (customView instanceof RelativeLayout) {
                    RelativeLayout layout = (RelativeLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                } else if (customView instanceof FrameLayout) {
                    FrameLayout layout = (FrameLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                }
            });


            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {

                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
        return returnStatement;
    }

    public boolean addBigNativeView(Context mContext, View customView) {
        boolean returnStatement = false;
        if (checkConnection(mContext) && AdsConstants.isNative) {
            returnStatement = true;
            AdLoader.Builder builder = new AdLoader.Builder(mContext, AdsConstants.ADMOB_NATIVE_ID);
            builder.forNativeAd(nativeAd -> {
                if (nativeAd00 != null) {
                    nativeAd00.destroy();
                }
                nativeAd00 = nativeAd;
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                NativeAdView mAdView =
                        (NativeAdView) inflater.inflate(R.layout.big_ad_unified, null);
                populateNativeAdView(nativeAd, mAdView);
                if (customView instanceof LinearLayout) {
                    LinearLayout layout = (LinearLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                } else if (customView instanceof RelativeLayout) {
                    RelativeLayout layout = (RelativeLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                } else if (customView instanceof FrameLayout) {
                    FrameLayout layout = (FrameLayout) customView;
                    layout.removeAllViews();
                    layout.addView(mAdView);
                }
            });


            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {

                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

        }
        return returnStatement;
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

    }

    private void callListener() {
        if (callback != null) {
            callback.press();
        }
    }

}
