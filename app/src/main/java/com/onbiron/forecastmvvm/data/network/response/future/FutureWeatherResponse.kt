package com.onbiron.forecastmvvm.data.network.response.future


data class FutureWeatherResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val minutely: List<Minutely>,
    val lat: Double,
    val lon: Double,
)