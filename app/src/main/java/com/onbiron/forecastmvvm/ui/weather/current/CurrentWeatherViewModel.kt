package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.internal.UnitSystem
import com.onbiron.forecastmvvm.internal.lazyDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider) : ViewModel() {

    private val unitSystem: UnitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val forecast by lazyDeferred {
        forecastRepository.getForecast(isMetric)
    }

    fun refreshForecast(){
        CoroutineScope(Dispatchers.Main).launch{
            forecastRepository.refreshForecast(isMetric)
        }
    }
}