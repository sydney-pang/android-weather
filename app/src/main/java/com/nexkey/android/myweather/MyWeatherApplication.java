package com.nexkey.android.myweather;

import android.app.Application;

import okhttp3.OkHttpClient;
import timber.log.Timber;

public class MyWeatherApplication extends Application {

    public static OkHttpClient getHttpClient() {
        return _httpClient;
    }

    private static OkHttpClient _httpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Timber: logging framework
         */
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } /*else {
            Timber.plant(new BugseeTree());
        }*/

        /**
         * OkHttp: networking
         */
        _httpClient = new OkHttpClient();


    }
}
