package com.onbiron.forecastmvvm.data.network.response.forecast

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)