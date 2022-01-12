package com.onbiron.forecastmvvm.data.db.entity.forecast

data class ForecastCurrent(
    val timestamp: Long,
    val feelsLike: Double,
    val humidity: Double,
    val temperature: Double,
    val forecastWeather: List<ForecastWeather>,
    val windSpeed: Double,
    val uvIndex: Double,
    val visibility: Double,
    val pressure: Double,
    val sunrise: Double,
    val sunset: Double,
)