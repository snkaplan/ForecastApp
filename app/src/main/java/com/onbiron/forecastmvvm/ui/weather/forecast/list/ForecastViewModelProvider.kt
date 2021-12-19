package com.onbiron.forecastmvvm.ui.weather.forecast.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository

class ForecastViewModelProvider (
    private val forecastRepository: ForecastRepository,
    private val unitProvider: UnitProvider
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ForecastViewModel(forecastRepository, unitProvider) as T
    }
}