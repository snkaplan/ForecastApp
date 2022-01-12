package com.onbiron.forecastmvvm.data.db.entity

data class WeatherLocation(
    val country: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val lastTimeEpoch: Long,
)