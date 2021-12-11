package com.onbiron.forecastmvvm.data.network

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.network.response.WeatherResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<WeatherResponse>
    suspend fun fetchCurrentWeather(location: CustomLocation, unit: String)
}