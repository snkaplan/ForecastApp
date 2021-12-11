package com.onbiron.forecastmvvm

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import com.onbiron.forecastmvvm.data.WeatherApiService
import com.onbiron.forecastmvvm.data.db.ForecastDatabase
import com.onbiron.forecastmvvm.data.network.ConnectivityInterceptor
import com.onbiron.forecastmvvm.data.network.ConnectivityInterceptorImpl
import com.onbiron.forecastmvvm.data.network.WeatherNetworkDataSource
import com.onbiron.forecastmvvm.data.network.WeatherNetworkDataSourceImpl
import com.onbiron.forecastmvvm.data.provider.LocationProvider
import com.onbiron.forecastmvvm.data.provider.LocationProviderImpl
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.data.provider.UnitProviderImpl
import com.onbiron.forecastmvvm.data.repository.ForecastRepository
import com.onbiron.forecastmvvm.data.repository.ForecastRepositoryImpl
import com.onbiron.forecastmvvm.ui.weather.current.CurrentWeatherViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ForecastApplication : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication)) // produces us a context
        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { WeatherApiService(instance(), getApiKey(), getApiStr()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind<ForecastRepository>() with singleton {
            ForecastRepositoryImpl(instance(),
                instance(),
                instance(),
                instance())
        }
        bind() from provider { CurrentWeatherViewModelFactory(instance(), instance()) }

    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }

    private fun getApiKey(): String {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["apiKey"]
        return value.toString()
    }

    private fun getApiStr(): String {
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["apiStr"]
        return value.toString()
    }
}