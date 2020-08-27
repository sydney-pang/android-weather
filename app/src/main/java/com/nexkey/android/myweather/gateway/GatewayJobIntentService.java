package com.nexkey.android.myweather.gateway;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;

import com.nexkey.android.myweather.MyWeatherApplication;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Request;
import timber.log.Timber;

public class GatewayJobIntentService extends JobIntentService {

    public static final String ACTION_LOCATION_SEARCH = GatewayJobIntentService.class.getCanonicalName() + ".ACTION_LOCATION_SEARCH";
    public static final String ACTION_LOCATION = GatewayJobIntentService.class.getCanonicalName() + ".ACTION_LOCATION";
    public static final String ACTION_LOCATION_DAY = GatewayJobIntentService.class.getCanonicalName() + ".ACTION_LOCATION_DAY";

    public static final String EXTRA_LATTLONG = GatewayJobIntentService.class.getCanonicalName() + ".EXTRA_LATTLONG";
    public static final String EXTRA_QUERY = GatewayJobIntentService.class.getCanonicalName() + ".EXTRA_QUERY";
    public static final String EXTRA_WOEID = GatewayJobIntentService.class.getCanonicalName() + ".EXTRA_WOEID";

    public static void enqueueWork(Context context, Intent work) {
        Timber.d("enqueueWork");
        enqueueWork(context, GatewayJobIntentService.class, JOB_ID, work);
    }

    static final int JOB_ID = 1000;

    private static final String BASE_URL = "https://www.metaweather.com/api/";

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Timber.i("Executing work: " + intent);
        if (intent == null) {
            Timber.w("intent is null!");
            return;
        }
        final String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            Timber.w("action is missing!");
            return;
        }

        Callback callback = null;
        try {
            if (TextUtils.equals(action, ACTION_LOCATION_SEARCH)) {
                callback = new LocationSearchCallback();
                _locationSearch(intent, callback);
            } else if (TextUtils.equals(action, ACTION_LOCATION)) {
                callback = new LocationCallback();
                _location(intent, callback);
            } else if (TextUtils.equals(action, ACTION_LOCATION_DAY)) {
                //callback = new LocationDayCallback(getApplicationContext());
                //_locationDay(intent, callback);
            } else {
                Timber.w("Unexpected action: %s", action);
            }
        } catch (Throwable t) {
            //callback.broadcastError(t.getMessage());
            t.printStackTrace();
        }
    }

    private void _locationSearch(Intent intent, Callback callback) {
        String lattlong = intent.getStringExtra(EXTRA_LATTLONG);
        if (!TextUtils.isEmpty(lattlong)) {
            _sendGet(callback, "location/search/", new Pair<String, String>("lattlong", lattlong));
        } else {
            String query = intent.getStringExtra(EXTRA_QUERY);
            if (TextUtils.isEmpty(query)) {
                query = "a";
            }
            _sendGet(callback, "location/search/", new Pair<String, String>("query", query));
        }
    }

    private void _location(Intent intent, Callback callback) {
        String woeid = intent.getStringExtra(EXTRA_WOEID);
        if (TextUtils.isEmpty(woeid)) {
            woeid = "2487956";  // San Francisco
        }
        _sendGet(callback, "location/" + woeid);
    }

    private void _sendGet(Callback callback, String path, Pair<String, String>... queryParams) {
        _sendGet(callback, BASE_URL, path, queryParams);
    }

    private void _sendGet(Callback callback, String baseUrl, String path, Pair<String, String>... queryParams) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(baseUrl + path)
                .newBuilder();
        for (Pair<String, String> qp : queryParams) {
            httpUrlBuilder.addQueryParameter(qp.first, qp.second);
        }
        MyWeatherApplication.getHttpClient()
                .newCall(new Request.Builder()
                        .url(httpUrlBuilder.build())
                        .build())
                .enqueue(callback);
    }
}