package com.onbiron.forecastmvvm.ui.weather.future.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository

class FutureListWeatherViewModelFactory (
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  FutureListWeatherViewModel(forecastRepository, unitProvider) as T
    }
}