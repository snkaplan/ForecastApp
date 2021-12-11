package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository

// When a view model has dependencies we have to create it by custom view model providers.
// Because default view model providers can not pass arguments to VM. It always calls default constructor.
class CurrentWeatherViewModelFactory(
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  CurrentWeatherViewModel(forecastRepository, unitProvider) as T
    }
}