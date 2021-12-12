package com.onbiron.forecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.future.FutureWeatherResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedFutureWeather: LiveData<FutureWeatherResponse>
    suspend fun fetchCurrentWeather(location: CustomLocation, unit: String)
    suspend fun fetchFutureWeather(location: CustomLocation, unit: String)
}