package com.onbiron.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.forecast.Forecast

interface ForecastRepository {
    suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry>
    suspend fun getForecast(): LiveData<Forecast>
    suspend fun refreshForecast()
}