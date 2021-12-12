package com.onbiron.forecastmvvm.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

const val WEATHER_LOCATION_ID = 0

@Entity(tableName = "weather_location")
data class WeatherLocation(
    val country: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val lastTimeEpoch: Long,
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = WEATHER_LOCATION_ID
}