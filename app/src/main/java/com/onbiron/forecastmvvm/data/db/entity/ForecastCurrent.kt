package com.onbiron.forecastmvvm.data.db.entity

data class ForecastCurrent(
    val feelsLike: Double,
    val humidity: Double,
    val temperature: Double,
    val visibility: Int,
    val weatherDescriptions: List<String>,
    val weatherIcons: List<String>,
    val windDir: Int,
    val windSpeed: Double,
)