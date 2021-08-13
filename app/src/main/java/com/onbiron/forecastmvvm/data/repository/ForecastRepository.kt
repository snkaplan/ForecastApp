package com.onbiron.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation

interface ForecastRepository {
    suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry>
    suspend fun getWeatherLocation(): LiveData<WeatherLocation>
}