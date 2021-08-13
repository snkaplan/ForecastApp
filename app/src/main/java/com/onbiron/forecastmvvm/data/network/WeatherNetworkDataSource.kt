package com.onbiron.forecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.CurrentWeatherDao
import com.onbiron.forecastmvvm.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    suspend fun fetchCurrentWeather(location: String, unit: String)
}