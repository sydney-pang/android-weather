package com.nexkey.android.myweather.gateway;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

public class LocationCallback implements Callback {

    public class Failure {

        private final String _message;

        Failure(String message) {
            _message = message;
        }

        public String getMessage() {
            return _message;
        }
    }

    public class Success {

        private final JSONObject _responseJson;

        Success(JSONObject responseJson) {
            _responseJson = responseJson;
        }

        public JSONObject getResponseJson() {
            return _responseJson;
        }
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Timber.d("onFailure");
        EventBus.getDefault().post(new Failure(e.getMessage()));
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            String responseStr = responseBody.string();
            Timber.d("RESPONSE=%s", responseStr);
            JSONObject responseJson = new JSONObject(responseStr);
            EventBus.getDefault().postSticky(new Success(responseJson));
        } catch (JSONException e) {
            EventBus.getDefault().post(new Failure(e.getMessage()));
        }
    }
}
