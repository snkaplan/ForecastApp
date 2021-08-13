package com.onbiron.forecastmvvm.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.CurrentWeatherDao
import com.onbiron.forecastmvvm.data.db.WeatherLocationDao
import com.onbiron.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.network.WeatherNetworkDataSource
import com.onbiron.forecastmvvm.data.network.response.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class ForecastRepositoryImpl(
        private val currentWeatherDao: CurrentWeatherDao,
        private val weatherLocationDao: WeatherLocationDao,
        private val weatherNetworkDataSource: WeatherNetworkDataSource,
        private val locationProvider: LocationProvider) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever{

            newCurrentWeather ->
            run {
                persistFetchedCurrentWeather(newCurrentWeather)
            }
        }
    }
    override suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry> {
        // withcontext same as GlobalScope but only different is withContext returns some data
        return withContext(Dispatchers.IO){
            initWeatherData(isMetric)
            return@withContext currentWeatherDao.getWeatherMetric()
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO){
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse){
        // Couroutines are lightweight threads in kotlin
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData(isMetric: Boolean){
        val lastWeatherLocation = weatherLocationDao.getLocationAsNormal()
        if(locationProvider.hasLocationChanged(lastWeatherLocation)){
            Log.d("TAG", "initWeatherData: here")
            fetchCurrentWeather(isMetric)
            return
        }
        if(isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)){
            fetchCurrentWeather(isMetric)
        }
    }

    private suspend fun fetchCurrentWeather(isMetric: Boolean){
        val unit = if(isMetric) "m" else "f"
        weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString(), unit)
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean{
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}