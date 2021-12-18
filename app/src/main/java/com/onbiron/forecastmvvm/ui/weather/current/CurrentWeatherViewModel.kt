package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.internal.UnitSystem
import com.onbiron.forecastmvvm.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider) : ViewModel() {

    private val unitSystem: UnitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }

    val forecast by lazyDeferred {
        forecastRepository.getFutureWeather(isMetric)
    }

    val location by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}