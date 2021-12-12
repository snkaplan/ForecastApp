package com.onbiron.forecastmvvm.data.network.response.current


import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Long,
    val id: Int,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind,
)

data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double,
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String,
)

data class Sys(
    val country: String,
    val id: Int,
    val sunrise: Int,
    val sunset: Int,
    val type: Int,
)

data class Main(
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val temp: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
)

data class Coord(
    val lat: Double,
    val lon: Double,
)

data class Clouds(
    val all: Int,
)