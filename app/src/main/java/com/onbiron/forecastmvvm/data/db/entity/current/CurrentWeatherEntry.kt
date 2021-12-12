package com.onbiron.forecastmvvm.data.db.entity.current


import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeatherEntry(
    val feelsLike: Double,
    val precip: Double?,
    val temperature: Double,
    val visibility: Int,
    val weatherDescriptions: List<String>,
    val weatherIcons: List<String>,
    val windDir: Int,
    val windSpeed: Double
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID
}