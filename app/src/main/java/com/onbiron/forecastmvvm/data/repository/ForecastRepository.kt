package com.onbiron.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.forecast.Forecast

interface ForecastRepository {
    suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry>
    suspend fun getForecast(isMetric: Boolean): LiveData<Forecast>
    suspend fun refreshForecast(isMetric: Boolean)
}