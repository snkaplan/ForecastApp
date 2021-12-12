package com.onbiron.forecastmvvm.data.db.entity.future

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onbiron.forecastmvvm.data.network.response.future.Current
import com.onbiron.forecastmvvm.data.network.response.future.Daily
import com.onbiron.forecastmvvm.data.network.response.future.Hourly


const val FUTURE_WEATHER_ID = 0

@Entity(tableName = "future_weather")
data class FutureWeatherEntry(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = FUTURE_WEATHER_ID
}