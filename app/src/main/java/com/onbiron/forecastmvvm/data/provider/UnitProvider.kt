package com.onbiron.forecastmvvm.data.provider

import com.onbiron.forecastmvvm.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}