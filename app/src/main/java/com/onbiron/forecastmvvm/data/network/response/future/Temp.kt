package com.onbiron.forecastmvvm.data.network.response.future


data class Temp(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
)