package com.onbiron.forecastmvvm.data.network.response.future


data class FeelsLike(
    val day: Double,
    val eve: Double,
    val morn: Double,
    val night: Double
)