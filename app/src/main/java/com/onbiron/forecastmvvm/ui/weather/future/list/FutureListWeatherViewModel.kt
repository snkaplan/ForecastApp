package com.onbiron.forecastmvvm.ui.weather.future.list

import androidx.lifecycle.ViewModel
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.internal.UnitSystem
import com.onbiron.forecastmvvm.internal.lazyDeferred

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
) : ViewModel() {
    private val unitSystem: UnitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val futureWeather by lazyDeferred {
        forecastRepository.getFutureWeather(isMetric)
    }

    val location by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}