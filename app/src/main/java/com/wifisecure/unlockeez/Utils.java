package com.wifisecure.unlockeez;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appsflyer.AppsFlyerLib;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static MaxInterstitialAd sMaxInterstitialAd;
    @SuppressLint("StaticFieldLeak")
    public static MaxAdView sMaxBannerAd;
    public static String INTER = "ea73034de715a1ac";
    public static String BANNER = "8851d35dea24574c";

    private static int tryAdAttempt;

    public static int ADS_COUNTER = 0;


    public static final String APP_PREF_SETTINGS_NAME = "Unlockeez";
    public static final String APP_DEV_KEY = "2czkX4PzkQ6nw2iaf3y8zJ";
    public static final String APP_ONE_SIGNAL_APP_ID = "4a3fecdf-5f0f-4916-a7ff-5e2e31a0f490";
    public static final String PREF_KEY_CAMPAIGN = "campaign";
    public static final String PREF_KEY_USER_UUID = "user_uuid";
    public static final String PREF_KEY_GPS_ADID = "gps_adid";
    public static final String PREF_VALUE_END_POINT = "end_point";
    public static final String PREF_KEY_REMOTE_CONFIG_CLOUD_POINT = "sub_endu";

    public static final String PREF_KEY_FIREBASE_INSTANCE_ID = "firebase_instance_id";


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm != null && cm.getActiveNetworkInfo() != null) && cm
                .getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void loadMaxInterstitialAd(Activity activity) {
        try {
            if (sMaxInterstitialAd == null) {
                sMaxInterstitialAd = new MaxInterstitialAd(INTER, activity);
                sMaxInterstitialAd.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(MaxAd maxAd) {
                        tryAdAttempt =0;
                    }

                    @Override
                    public void onAdDisplayed(MaxAd maxAd) {

                    }

                    @Override
                    public void onAdHidden(MaxAd maxAd) {

                    }

                    @Override
                    public void onAdClicked(MaxAd maxAd) {

                    }

                    @Override
                    public void onAdLoadFailed(String s, MaxError maxError) {
                        tryAdAttempt++;
                        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, tryAdAttempt)));
                        new Handler().postDelayed(() -> sMaxInterstitialAd.loadAd(), delayMillis);
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                        sMaxInterstitialAd.loadAd();
                    }
                });
                sMaxInterstitialAd.loadAd();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showInterstitialAd() {
        try {
            ADS_COUNTER++;
            if (ADS_COUNTER == 3) {
                if (sMaxInterstitialAd != null && sMaxInterstitialAd.isReady()) {
                    sMaxInterstitialAd.showAd();
                }
                ADS_COUNTER = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showBannerAds(RelativeLayout adContainer, Activity activity) {

        sMaxBannerAd = new MaxAdView(BANNER, activity);

        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        final boolean isTablet = AppLovinSdkUtils.isTablet(activity);
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(activity).getHeight();
        int heightTabletDp = MaxAdFormat.LEADER.getAdaptiveSize(activity).getHeight();
        //int heightPx = AppLovinSdkUtils.dpToPx(activity, heightDp);
        final int heightPx = AppLovinSdkUtils.dpToPx(activity, isTablet ? heightTabletDp : heightDp);

        sMaxBannerAd.setLayoutParams(new RelativeLayout.LayoutParams(width, heightPx));
        sMaxBannerAd.setExtraParameter("adaptive_banner", "true");
        adContainer.addView(sMaxBannerAd);
        sMaxBannerAd.startAutoRefresh();
        sMaxBannerAd.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                // adContainer.removeAllViews();

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            }
        });

        sMaxBannerAd.loadAd();
    }


    public static void setClickID(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(APP_PREF_SETTINGS_NAME,
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_KEY_USER_UUID, value);
            editor.apply();
        }
    }

    public static String getClickID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREF_SETTINGS_NAME,
                MODE_PRIVATE);
        return preferences.getString(PREF_KEY_USER_UUID, "");
    }

    public static void setCampaign(Context context, String InternalFlow_value) {
        if (context != null) {
            SharedPreferences preferences_InternalFlow = context.getSharedPreferences(APP_PREF_SETTINGS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor_InternalFlow = preferences_InternalFlow.edit();
            editor_InternalFlow.putString(PREF_KEY_CAMPAIGN, InternalFlow_value);
            editor_InternalFlow.apply();
        }
    }

    public static String getCampaign(Context context) {
        SharedPreferences preferences_InternalFlow = context.getSharedPreferences(APP_PREF_SETTINGS_NAME, MODE_PRIVATE);
        return preferences_InternalFlow.getString(PREF_KEY_CAMPAIGN, "");
    }

    public static void setFirebaseInstanceID(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(APP_PREF_SETTINGS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_KEY_FIREBASE_INSTANCE_ID, value);
            editor.apply();
        }
    }

    public static String getFirebaseInstanceID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREF_SETTINGS_NAME, MODE_PRIVATE);
        return preferences.getString(PREF_KEY_FIREBASE_INSTANCE_ID, "");
    }

    public static void setCloudPointValue(Context context, String value) {
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(APP_PREF_SETTINGS_NAME,
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_VALUE_END_POINT, value);
            editor.apply();
        }
    }

    public static String getCloudPointValue(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(APP_PREF_SETTINGS_NAME,
                MODE_PRIVATE);
        return preferences.getString(PREF_VALUE_END_POINT, "");
    }

    public static void setGPSADID(Context context, String InternalFlow_value) {
        if (context != null) {
            SharedPreferences preferences_InternalFlow = context.getSharedPreferences(APP_PREF_SETTINGS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor_InternalFlow = preferences_InternalFlow.edit();
            editor_InternalFlow.putString(PREF_KEY_GPS_ADID, InternalFlow_value);
            editor_InternalFlow.apply();
        }
    }

    public static String getGPSADID(Context context) {
        SharedPreferences preferences_InternalFlow = context.getSharedPreferences(APP_PREF_SETTINGS_NAME, MODE_PRIVATE);
        return preferences_InternalFlow.getString(PREF_KEY_GPS_ADID, "");
    }

    public static String generateClickID(Context context) {
        String md5uuid = getClickID(context);
        if (md5uuid == null || md5uuid.isEmpty()) {
            String guid = "";
            final String uniqueID = UUID.randomUUID().toString();
            Date date = new Date();
            long timeMilli = date.getTime();
            guid = uniqueID + timeMilli;
            md5uuid = md5(guid);
            setClickID(context, md5uuid);
        }
        return md5uuid;
    }

    private static String md5(String str_value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str_value.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuffer strBuffer = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                strBuffer.append(Integer.toHexString(0xFF & messageDigest[i]));
            return strBuffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMcc(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String mCCMccCode = telephonyManager.getNetworkOperator();
            String mccCode = "";
            if (TextUtils.isEmpty(mCCMccCode)) {
                return "";
            }

            final int MCC_CODE_LENGTH = 3;
            if (mCCMccCode.length() >= MCC_CODE_LENGTH) {
                mccCode = mCCMccCode.substring(0, MCC_CODE_LENGTH);
            }

            return mccCode;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getMnc(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return "";
            }
            String mCCMncCode = telephonyManager.getNetworkOperator();
            String mncCode = "";
            if (TextUtils.isEmpty(mCCMncCode)) {
                return "";
            }

            final int MNC_CODE_LENGTH = 3;

            if (mCCMncCode.length() > MNC_CODE_LENGTH) {
                mncCode = mCCMncCode.substring(MNC_CODE_LENGTH);
            }
            return mncCode;
        } catch (Exception e) {
            return "";
        }
    }

    public static String generatePremiumLink(Context context) {
        String strPremiumLink = "";
        try {
            strPremiumLink = getCloudPointValue(context)
                    + "?naming=" + URLEncoder.encode(getCampaign(context), "utf-8")
                    + "&adid=" + getGPSADID(context)
                    + "&firebase_instance_id=" + getFirebaseInstanceID(context)
                    + "&afid=" + AppsFlyerLib.getInstance().getAppsFlyerUID(context)
                    + "&package=" + context.getPackageName()
                    + "&mnc=" + getMnc(context)
                    + "&mcc=" + getMcc(context)
                    + "&click_id=" + getClickID(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strPremiumLink;
    }
}
