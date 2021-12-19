package com.onbiron.forecastmvvm.data.network.response.forecast


data class ForecastResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val minutely: List<Minutely>,
    val lat: Double,
    val lon: Double,
)