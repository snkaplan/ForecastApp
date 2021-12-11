package com.onbiron.forecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.onbiron.forecastmvvm.data.WeatherApiService
import com.onbiron.forecastmvvm.data.network.response.WeatherResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation
import com.onbiron.forecastmvvm.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService,
) : WeatherNetworkDataSource {

    private val _downloadedCurrentWeather = MutableLiveData<WeatherResponse>()
    override val downloadedCurrentWeather: LiveData<WeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(location: CustomLocation, unit: String) {
        try {
            val fetchedCurrentWeather: WeatherResponse = if (location.name.isNullOrEmpty()) {
                weatherApiService
                    .getCurrentWeatherByLatLonAsync(location.lat!!, location.lon!!, unit)
                    .await()
            } else {
                weatherApiService
                    .getCurrentWeatherByNameAsync(location.name, unit)
                    .await()
            }
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}