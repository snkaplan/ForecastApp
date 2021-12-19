package com.onbiron.forecastmvvm.data.network.response.future


import com.google.gson.annotations.SerializedName

data class Current(
    val clouds: Double,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val dt: Long,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Double,
    val pressure: Double,
    val sunrise: Double,
    val sunset: Double,
    val temp: Double,
    val uvi: Double,
    val visibility: Double,
    val weather: List<Weather>,
    @SerializedName("wind_deg")
    val windDeg: Double,
    @SerializedName("wind_gust")
    val windGust: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double
)