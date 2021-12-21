package com.onbiron.forecastmvvm.ui.weather.forecast.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastDaily
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.internal.lazyDeferred

class ForecastViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {

    val forecast by lazyDeferred {
        forecastRepository.getForecast()
    }

    private val _selectedDaily = MutableLiveData<ForecastDaily>()
    val selectedDaily: LiveData<ForecastDaily>
        get() = _selectedDaily

    fun setSelectedDaily(selectedDaily: ForecastDaily){
        _selectedDaily.postValue(selectedDaily)
    }


}