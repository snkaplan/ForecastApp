package com.onbiron.forecastmvvm.ui.weather.forecast.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastDaily
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.internal.UnitSystem
import com.onbiron.forecastmvvm.internal.lazyDeferred

class ForecastViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider,
) : ViewModel() {
    private val unitSystem: UnitSystem = unitProvider.getUnitSystem()
    private val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val forecast by lazyDeferred {
        forecastRepository.getForecast(isMetric)
    }

    private val _selectedDaily = MutableLiveData<ForecastDaily>()
    val selectedDaily: LiveData<ForecastDaily>
        get() = _selectedDaily

    fun setSelectedDaily(selectedDaily: ForecastDaily){
        _selectedDaily.postValue(selectedDaily)
    }


}