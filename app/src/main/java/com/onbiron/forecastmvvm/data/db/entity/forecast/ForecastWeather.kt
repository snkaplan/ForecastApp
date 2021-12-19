package com.onbiron.forecastmvvm.data.db.entity.forecast

data class ForecastWeather (
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)