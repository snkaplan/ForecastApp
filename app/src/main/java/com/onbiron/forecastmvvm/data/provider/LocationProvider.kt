package com.onbiron.forecastmvvm.data.provider

import android.location.Address
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation


interface LocationProvider {
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation) : Boolean
    suspend fun getPreferredLocationString(): CustomLocation
    fun getAddressFromLatLon(lat: Double, lng: Double) : Address?
    fun getAddressFromName(name: String) : Address?
}