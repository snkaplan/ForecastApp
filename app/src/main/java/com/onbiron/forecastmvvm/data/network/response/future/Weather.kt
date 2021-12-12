package com.onbiron.forecastmvvm.data.network.response.future

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)