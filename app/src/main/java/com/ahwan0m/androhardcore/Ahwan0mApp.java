package com.ahwan0m.androhardcore;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;

public class Ahwan0mApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }
}

