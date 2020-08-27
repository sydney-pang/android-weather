package com.nexkey.android.myweather.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexkey.android.myweather.databinding.FragmentForecastBinding;
import com.nexkey.android.myweather.domain.DateWeather;
import com.nexkey.android.myweather.gateway.GatewayJobIntentService;
import com.nexkey.android.myweather.gateway.LocationCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import timber.log.Timber;

public class ForecastFragment extends Fragment {

    public static Fragment newInstance() {
        return new ForecastFragment();
    }

    private FragmentForecastBinding _binding;

    private ArrayList<DateWeather> _dateWeatherList;

    private DateWeatherAdapter _adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _dateWeatherList = new ArrayList<DateWeather>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = FragmentForecastBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _binding.forecastSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Timber.d("onRefresh");
                GatewayJobIntentService.enqueueWork(getContext(), new Intent(GatewayJobIntentService.ACTION_LOCATION)
                        .putExtra(GatewayJobIntentService.EXTRA_WOEID, "2487956"));
            }
        });
        _binding.forecastRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        _adapter = new DateWeatherAdapter(_dateWeatherList);
        _binding.forecastRecyclerview.setAdapter(_adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
        _binding.forecastSwiperefreshlayout.setRefreshing(false);
    }

    @Subscribe
    public void onLocationSucceeded(LocationCallback.Success success) {
        Timber.d("onLocationSucceeded");
        _binding.forecastSwiperefreshlayout.setRefreshing(false);
        JSONObject responseJson = success.getResponseJson();
        JSONArray consolidatedWeatherJsonArray = responseJson.optJSONArray("consolidated_weather");
        if (consolidatedWeatherJsonArray != null) {
            _dateWeatherList.clear();
            for (int i = 0; i < consolidatedWeatherJsonArray.length(); i++) {
                _dateWeatherList.add(new DateWeather(consolidatedWeatherJsonArray.optJSONObject(i)));
            }

            _updateUi();
        }
    }

    private void _updateUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _adapter.notifyDataSetChanged();
            }
        });
    }
}
