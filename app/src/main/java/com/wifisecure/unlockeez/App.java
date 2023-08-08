package com.wifisecure.unlockeez;


import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import java.util.Map;


public class App extends Application {

    Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            builder.detectFileUriExposure();
            StrictMode.setVmPolicy(builder.build());
        }

        try {
            mContext = this;
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
        AppLovinSdk.initializeSdk(this, appLovinSdkConfiguration -> {
        });

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(Utils.APP_ONE_SIGNAL_APP_ID);
        OneSignal.setExternalUserId(Utils.generateClickID(getApplicationContext()));

        String afDevKey = Utils.APP_DEV_KEY;
        AppsFlyerLib appsflyer = AppsFlyerLib.getInstance();
        appsflyer.setMinTimeBetweenSessions(0);
        appsflyer.setDebugLog(BuildConfig.DEBUG);

        appsflyer.init(afDevKey, new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> map) {
                String campaign = (String) map.get("campaign");
                Log.e("App=", campaign);
                Utils.setCampaign(mContext, campaign);
            }

            @Override
            public void onConversionDataFail(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        }, this);
        appsflyer.start(this);

        Utils.generateClickID(getApplicationContext());

        try {
            FirebaseApp.initializeApp(this);
            FirebaseAnalytics.getInstance(mContext)
                    .getAppInstanceId()
                    .addOnCompleteListener(task -> {
                        Utils.setFirebaseInstanceID(mContext, task.getResult());
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}