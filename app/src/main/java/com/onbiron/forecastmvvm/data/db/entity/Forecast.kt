package com.onbiron.forecastmvvm.data.db.entity


data class Forecast(
    val current: ForecastCurrent,
    val daily: List<ForecastDaily>,
    val hourly: List<ForecastHourly>,
    val minutely: List<ForecastMinutely>,
    val location: WeatherLocation
)