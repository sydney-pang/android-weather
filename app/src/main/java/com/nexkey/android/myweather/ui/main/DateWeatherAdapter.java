package com.nexkey.android.myweather.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexkey.android.myweather.R;
import com.nexkey.android.myweather.databinding.RowDateweatherBinding;
import com.nexkey.android.myweather.domain.DateWeather;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DateWeatherAdapter extends RecyclerView.Adapter<DateWeatherAdapter.DateWeatherHolder> {

    private ArrayList<DateWeather> _dateWeatherList;

    DateWeatherAdapter(ArrayList<DateWeather> dateWeatherList) {
        _dateWeatherList = dateWeatherList;
    }

    @NonNull
    @Override
    public DateWeatherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dateweather, parent, false);
        return new DateWeatherHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull DateWeatherHolder holder, int position) {
        DateWeather dateWeather = _dateWeatherList.get(position);
        holder.bindDateWeather(dateWeather);
    }

    @Override
    public int getItemCount() {
        return _dateWeatherList.size();
    }

    public static class DateWeatherHolder extends RecyclerView.ViewHolder {

        RowDateweatherBinding _binding;

        private DateWeather _dateWeather;

        public DateWeatherHolder(@NonNull View itemView) {
            super(itemView);
            _binding = RowDateweatherBinding.bind(itemView);
        }

        public void bindDateWeather(DateWeather dateWeather) {
            _dateWeather = dateWeather;
            _binding.rowDateweatherDateTextview.setText(_dateWeather.getApplicableDate());
            _binding.rowDateweatherWeatherNameTextview.setText(_dateWeather.getWeatherStateName());
            _binding.rowDateweatherWeatherImageview.setImageResource(R.mipmap.ic_light_cloud);
            _binding.rowDateweatherHighTempTextview.setText(String.valueOf(_dateWeather.getMaxTemp()));
            _binding.rowDateweatherLowTempTextview.setText(String.valueOf(_dateWeather.getMinTemp()));
        }
    }
}
