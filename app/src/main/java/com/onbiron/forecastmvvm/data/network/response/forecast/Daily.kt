package com.onbiron.forecastmvvm.data.network.response.forecast


import com.google.gson.annotations.SerializedName

data class Daily(
    val clouds: Double,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val dt: Long,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    val humidity: Double,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val moonrise: Long,
    val moonset: Long,
    val pop: Double,
    val pressure: Double,
    val rain: Double,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val uvi: Double,
    val weather: List<Weather>,
    @SerializedName("wind_deg")
    val windDeg: Double,
    @SerializedName("wind_gust")
    val windGust: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double
)