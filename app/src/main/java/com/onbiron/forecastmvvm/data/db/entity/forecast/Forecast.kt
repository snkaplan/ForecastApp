package com.onbiron.forecastmvvm.data.db.entity.forecast

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation

const val CURRENT_FORECAST_ID = 0

@Entity(tableName = "forecast")
data class Forecast(
    val current: ForecastCurrent,
    val daily: List<ForecastDaily>,
    val hourly: List<ForecastHourly>,
    val minutely: List<ForecastMinutely>,
    val location: WeatherLocation
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_FORECAST_ID
}