package com.onbiron.forecastmvvm.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.onbiron.forecastmvvm.data.WeatherApiService
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.future.FutureWeatherResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation
import com.onbiron.forecastmvvm.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService,
) : WeatherNetworkDataSource {

    private val _downloadedCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    private val _downloadedFutureWeather = MutableLiveData<FutureWeatherResponse>()
    override val downloadedFutureWeather: LiveData<FutureWeatherResponse>
        get() = _downloadedFutureWeather

    override suspend fun fetchCurrentWeather(location: CustomLocation, unit: String) {
        try {
            val fetchedCurrentWeather: CurrentWeatherResponse = if (location.name.isNullOrEmpty()) {
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

    override suspend fun fetchFutureWeather(location: CustomLocation, unit: String) {
        try {
            val fetchedCurrentWeather: FutureWeatherResponse = if (location.name.isNullOrEmpty()) {
                weatherApiService
                    .getFutureWeatherByLatLonAsync(location.lat!!, location.lon!!, unit)
                    .await()
            } else {
                weatherApiService
                    .getFutureWeatherByNameAsync(location.name, unit)
                    .await()
            }
            _downloadedFutureWeather.postValue(fetchedCurrentWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }
}