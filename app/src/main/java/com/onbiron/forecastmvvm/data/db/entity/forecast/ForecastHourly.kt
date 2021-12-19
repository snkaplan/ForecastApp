package com.onbiron.forecastmvvm.data.db.entity.forecast

data class ForecastHourly (
    val timestamp: Long,
    val feelsLike: Double,
    val humidity: Double,
    val pop: Double, //probability of precipitation
    val pressure: Double,
    val temperature: Double,
    val uvIndex: Double,
    val visibility: Double,
    val weather: List<ForecastWeather>,
    val windSpeed: Double
)