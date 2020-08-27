package com.nexkey.android.myweather.domain;

import org.json.JSONObject;

public class DateWeather {

    private String _id;
    private String _weatherStateName;
    private String _weatherStateAbbr;
    private String _windDirectionCompass;
    private String _created;
    private String _applicableDate;
    private double _minTemp;
    private double _maxTemp;
    private double _theTemp;
    private double _windSpeed;
    private double _windDirection;
    private double _airPressure;
    private int _humidity;
    private double _visibility;
    private int _predictability;

    public DateWeather(JSONObject json) {
        _id = json.optString("id");
        _weatherStateName = json.optString("weather_state_name");
        _weatherStateAbbr = json.optString("weather_state_abbr");
        _windDirectionCompass = json.optString("wind_direction_compass");
        _created = json.optString("created");
        _applicableDate = json.optString("applicable_date");
        _minTemp = json.optDouble("min_temp");
        _maxTemp = json.optDouble("max_temp");
        _theTemp = json.optDouble("the_temp");
        _windSpeed = json.optDouble("wind_speed");
        _windDirection = json.optDouble("wind_direction");
        _airPressure = json.optDouble("air_pressure");
        _humidity = json.optInt("humidity");
        _visibility = json.optDouble("visibility");
        _predictability = json.optInt("predictability");
    }

    public String getCreated() {
        return _created;
    }

    public String getApplicableDate() {
        return _applicableDate;
    }

    public String getWeatherStateName() {
        return _weatherStateName;
    }

    public double getMaxTemp() {
        return _maxTemp;
    }

    public double getMinTemp() {
        return _minTemp;
    }

    public double getTheTemp() {
        return _theTemp;
    }

}
