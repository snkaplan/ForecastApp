package com.onbiron.forecastmvvm.data.network

import android.util.Log
import com.onbiron.forecastmvvm.data.WeatherApiService
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.forecast.ForecastResponse
import com.onbiron.forecastmvvm.data.provider.CustomLocation
import com.onbiron.forecastmvvm.data.provider.LocationProvider
import com.onbiron.forecastmvvm.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val weatherApiService: WeatherApiService,
    private val locationProvider: LocationProvider
): WeatherNetworkDataSource {
    private val unit = "metric"
    override suspend fun fetchCurrentWeather(
        location: CustomLocation
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

    override suspend fun fetchForecast(location: CustomLocation): ForecastResponse? {
        try {
            if (location.name.isNullOrEmpty()) {
                 return weatherApiService
                    .getForecastByLatLonAsync(location.lat!!, location.lon!!, unit)
                    .await()
            } else {
                val addressFromName = locationProvider.getAddressFromName(location.name)
                if(addressFromName!=null){
                    return weatherApiService
                        .getForecastByLatLonAsync(addressFromName.latitude, addressFromName.longitude, unit)
                        .await()
                }
            }
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
        return null
    }
}