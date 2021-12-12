package com.onbiron.forecastmvvm.data.network.response.future


import com.google.gson.annotations.SerializedName

data class Minutely(
    val dt: Double,
    val precipitation: Double
)