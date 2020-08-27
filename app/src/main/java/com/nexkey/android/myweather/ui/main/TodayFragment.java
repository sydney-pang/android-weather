package com.nexkey.android.myweather.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexkey.android.myweather.R;
import com.nexkey.android.myweather.databinding.FragmentTodayBinding;
import com.nexkey.android.myweather.domain.DateWeather;
import com.nexkey.android.myweather.gateway.GatewayJobIntentService;
import com.nexkey.android.myweather.gateway.LocationCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import timber.log.Timber;

public class TodayFragment extends Fragment {
    public static Fragment newInstance() {
        return new TodayFragment();
    }

    private FragmentTodayBinding _binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = FragmentTodayBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _binding.todaySwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Timber.d("onRefresh");
                GatewayJobIntentService.enqueueWork(getContext(), new Intent(GatewayJobIntentService.ACTION_LOCATION_SEARCH)
                        .putExtra(GatewayJobIntentService.EXTRA_QUERY, "san"));
                GatewayJobIntentService.enqueueWork(getContext(), new Intent(GatewayJobIntentService.ACTION_LOCATION)
                        .putExtra(GatewayJobIntentService.EXTRA_WOEID, "2487956"));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        GatewayJobIntentService.enqueueWork(getContext(), new Intent(GatewayJobIntentService.ACTION_LOCATION_SEARCH)
                .putExtra(GatewayJobIntentService.EXTRA_QUERY, "san"));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    @Subscribe
    public void onLocationFailed(LocationCallback.Failure failure) {
        Timber.d("onLocationFailed");
        _binding.todaySwiperefreshlayout.setRefreshing(false);
    }

    @Subscribe
    public void onLocationSuccess(LocationCallback.Success success) {
        Timber.d("onLocationSuccess");
        _binding.todaySwiperefreshlayout.setRefreshing(false);
        JSONObject responseJson = success.getResponseJson();
        JSONArray consolidatedWeatherJsonArray = responseJson.optJSONArray("consolidated_weather");
        if (consolidatedWeatherJsonArray != null && consolidatedWeatherJsonArray.length() >= 1) {
            try {
                final DateWeather todayWeather = new DateWeather(consolidatedWeatherJsonArray.getJSONObject(0)); // TODO match Date vs _applicableDate
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _binding.todayDatetimeTextview.setText(_buildPrettyDateTime(todayWeather.getCreated()));
                        _binding.todayHiloTempTextview.setText(getString(R.string.temp_hi_lo, (int) _toFahrenheit(todayWeather.getMaxTemp()), (int) _toFahrenheit(todayWeather.getMinTemp())));
                        _binding.todayTemperatureTextview.setText(String.valueOf((int) _toFahrenheit(todayWeather.getTheTemp())));
                        _binding.todayWeatherTextview.setText(todayWeather.getWeatherStateName());
                    }
                });

            } catch (JSONException e) {
                Timber.w(e, "Unable to parse today's weather");
            }
        }
    }

    private static String _buildPrettyDateTime(String dateIso) {
        String ret = null;
        try {                                                      // "2020-08-27T06:20:20.154442Z"
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("LMT"));

            Date date = isoFormat.parse(dateIso);

            SimpleDateFormat prettyFormat = new SimpleDateFormat("MMMM d, h:mm a");
            prettyFormat.setTimeZone(TimeZone.getDefault());

            ret = prettyFormat.format(date);
        } catch (ParseException e) {
            Timber.e(e);
        }
        return ret;
    }

    private static double _toFahrenheit(double centigrade) {
        return (centigrade * 1.8f) + 32;
    }
}
