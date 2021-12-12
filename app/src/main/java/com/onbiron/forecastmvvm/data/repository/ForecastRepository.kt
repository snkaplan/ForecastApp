package com.onbiron.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.db.entity.future.FutureWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry>
    suspend fun getFutureWeather(isMetric: Boolean): LiveData<FutureWeatherEntry>
    suspend fun getWeatherLocation(): LiveData<WeatherLocation>
}