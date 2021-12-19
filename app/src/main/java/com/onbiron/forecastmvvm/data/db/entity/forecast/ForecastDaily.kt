package com.onbiron.forecastmvvm.data.db.entity.forecast

import com.onbiron.forecastmvvm.data.network.response.forecast.Temp

data class ForecastDaily(
    val timestamp: Long,
    val humidity: Double,
    val pop: Double, //probability of precipitation
    val pressure: Double,
    val rain: Double, // Precipitation volume, mm
    val sunrise: Double,
    val sunset: Double,
    val temperature: Temp,
    val uvIndex: Double,
    val weather: List<ForecastWeather>,
    val windSpeed: Double
)