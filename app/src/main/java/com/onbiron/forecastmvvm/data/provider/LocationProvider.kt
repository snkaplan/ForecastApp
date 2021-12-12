package com.onbiron.forecastmvvm.data.provider

import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation


interface LocationProvider {
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation) : Boolean
    suspend fun getPreferredLocationString(): CustomLocation
}