package com.wifisecure.unlockeez.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdRevenueListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.wifisecure.unlockeez.R;
import com.wifisecure.unlockeez.UnLockeEzMainPageActivity;
import com.wifisecure.unlockeez.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UnLockeEzSplashActivity extends AppCompatActivity implements MaxAdListener, MaxAdRevenueListener {


    MaxInterstitialAd interstitialAd;
    int tryAdAttempt = 0;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    long APP_SPLASH_TIME = 0;
    long APP_REF_TIMER = 10;
    ScheduledExecutorService mScheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_un_locke_ez);

        retrieveAdvertiseID();
        initView();
        loadAds();
        runScheduledExecutorService();

    }


    private void retrieveAdvertiseID() {
        // Check if Google Play Services is available
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode == ConnectionResult.SUCCESS) {
            // Google Play Services is available
            new Thread(() -> {
                try {
                    // Retrieve the Advertising ID
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(UnLockeEzSplashActivity.this);
                    String gpsId = adInfo.getId();

                    Utils.setGPSADID(UnLockeEzSplashActivity.this, gpsId);
                    // ...
                } catch (Exception e) {
                    // Handle any errors
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void initView() {
        FirebaseAnalytics.getInstance(this).setUserId(Utils.getClickID(this));
        FirebaseCrashlytics.getInstance().setUserId(Utils.getClickID(this));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(21600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCanceledListener(() -> {

                })
                .addOnFailureListener(this, task -> {

                })
                .addOnCompleteListener(this, task -> {

                    if (!mFirebaseRemoteConfig.getString(Utils.PREF_KEY_REMOTE_CONFIG_CLOUD_POINT)
                            .equalsIgnoreCase("")) {
                        if (mFirebaseRemoteConfig.getString(Utils.PREF_KEY_REMOTE_CONFIG_CLOUD_POINT)
                                .startsWith("http")) {
                            Utils.setCloudPointValue(UnLockeEzSplashActivity.this,
                                    mFirebaseRemoteConfig.getString(Utils.PREF_KEY_REMOTE_CONFIG_CLOUD_POINT));
                        } else {
                            Utils.setCloudPointValue(UnLockeEzSplashActivity.this,
                                    "https://" + mFirebaseRemoteConfig.getString(Utils.PREF_KEY_REMOTE_CONFIG_CLOUD_POINT));
                        }
                    }
                });
    }

    public void runScheduledExecutorService() {
        try {
            mScheduledExecutorService = Executors.newScheduledThreadPool(5);
            mScheduledExecutorService.scheduleAtFixedRate(() -> {
                APP_SPLASH_TIME = APP_SPLASH_TIME + 1;
                if (!Utils.getCampaign(UnLockeEzSplashActivity.this).isEmpty()) {
                    try {
                        mScheduledExecutorService.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkInternet();
                } else if (APP_SPLASH_TIME >= APP_REF_TIMER) {
                    try {
                        mScheduledExecutorService.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkInternet();
                }


            }, 0, 500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            gotoHome();
        }
    }

    public void checkInternet() {
        if (!Utils.isNetworkAvailable(UnLockeEzSplashActivity.this)) {
            checkInternetConnectionDialog(UnLockeEzSplashActivity.this);
        } else {
            gotoNext();
        }
    }

    public void checkInternetConnectionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.no_internet_connection);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_try_again, (dialog, which) -> retryInternetConnection());
        builder.show();
    }

    private void retryInternetConnection() {
        new Handler(Looper.getMainLooper()).postDelayed(this::checkInternet, 1000);
    }

    public void gotoNext() {
        if (!Utils.getCloudPointValue(UnLockeEzSplashActivity.this).isEmpty() ||
                !Utils.getCloudPointValue(UnLockeEzSplashActivity.this).equalsIgnoreCase("")) {
            startActivity(new Intent(UnLockeEzSplashActivity.this, UnLockeEzPremiumActivity.class));
            finish();
        } else {
            if (interstitialAd.isReady()) {
                interstitialAd.showAd();
            } else {
                gotoHome();
            }
        }
    }


    public void loadAds() {
        interstitialAd = new MaxInterstitialAd(Utils.INTER, this);
        interstitialAd.setListener(this);
        interstitialAd.setRevenueListener(this);
        // Load the first ad.
        interstitialAd.loadAd();
    }

    public void gotoHome() {
        startActivity(new Intent(UnLockeEzSplashActivity.this, UnLockeEzMainPageActivity.class));
        finish();
    }

    @Override
    public void onAdLoaded(MaxAd maxAd) {
    }

    @Override
    public void onAdDisplayed(MaxAd maxAd) {

    }

    @Override
    public void onAdHidden(MaxAd maxAd) {
        gotoHome();
    }

    @Override
    public void onAdClicked(MaxAd maxAd) {

    }

    @Override
    public void onAdLoadFailed(String s, MaxError maxError) {
        tryAdAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, tryAdAttempt)));
        new Handler().postDelayed(() -> interstitialAd.loadAd(), delayMillis);

    }

    @Override
    public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
        interstitialAd.loadAd();
    }

    @Override
    public void onAdRevenuePaid(MaxAd maxAd) {

    }
}