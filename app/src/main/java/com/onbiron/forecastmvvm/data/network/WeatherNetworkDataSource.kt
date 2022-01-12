package com.onbiron.forecastmvvm.data.network

import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.forecast.ForecastResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation

interface WeatherNetworkDataSource {
    suspend fun fetchCurrentWeather(location: CustomLocation): CurrentWeatherResponse?
    suspend fun fetchForecast(location: CustomLocation): ForecastResponse?
}