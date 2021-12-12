package com.onbiron.forecastmvvm.data.network.response.future


import com.google.gson.annotations.SerializedName

data class FutureWeatherResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
)