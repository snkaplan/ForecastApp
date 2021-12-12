package com.onbiron.forecastmvvm.data.network

import android.util.Log
import com.onbiron.forecastmvvm.data.WeatherApiService
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.future.FutureWeatherResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation
import com.onbiron.forecastmvvm.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService,
): WeatherNetworkDataSource {

    override suspend fun fetchCurrentWeather(
        location: CustomLocation,
        unit: String,
    ): CurrentWeatherResponse? {
        try {
            return if (location.name.isNullOrEmpty()) {
                weatherApiService
                    .getCurrentWeatherByLatLonAsync(location.lat!!, location.lon!!, unit)
                    .await()
            } else {
                weatherApiService
                    .getCurrentWeatherByNameAsync(location.name, unit)
                    .await()
            }
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
        return null
    }

    override suspend fun fetchFutureWeather(location: CustomLocation, unit: String): FutureWeatherResponse? {
        try {
            return if (location.name.isNullOrEmpty()) {
                weatherApiService
                    .getFutureWeatherByLatLonAsync(location.lat!!, location.lon!!, unit)
                    .await()
            } else {
                weatherApiService
                    .getFutureWeatherByNameAsync(location.name, unit)
                    .await()
            }
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
        return null
    }
}