package com.onbiron.forecastmvvm.ui.weather.forecast.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository

class ForecastViewModelProvider (
    private val forecastRepository: ForecastRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ForecastViewModel(forecastRepository) as T
    }
}