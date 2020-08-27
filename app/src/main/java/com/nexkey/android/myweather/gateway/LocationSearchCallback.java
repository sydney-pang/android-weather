package com.nexkey.android.myweather.gateway;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class LocationSearchCallback implements Callback {

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Timber.d("Failed");
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            String responseStr = responseBody.string();
            Timber.d("RESPONSE=%s", responseStr);
        }
    }
}
