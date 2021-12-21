package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModel
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.internal.lazyDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository) : ViewModel() {

    val forecast by lazyDeferred {
        forecastRepository.getForecast()
    }

    fun refreshForecast(){
        CoroutineScope(Dispatchers.Main).launch{
            forecastRepository.refreshForecast()
        }
    }
}