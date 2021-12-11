package com.onbiron.forecastmvvm.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.CurrentWeatherDao
import com.onbiron.forecastmvvm.data.db.WeatherLocationDao
import com.onbiron.forecastmvvm.data.db.entity.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.network.WeatherNetworkDataSource
import com.onbiron.forecastmvvm.data.network.response.WeatherResponse
import com.onbiron.forecastmvvm.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider,
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever {

                newCurrentWeather ->
            run {
                persistFetchedCurrentWeather(newCurrentWeather)
            }
        }
    }

    override suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry> {
        // withcontext same as GlobalScope but only different is withContext returns some data
        return withContext(Dispatchers.IO) {
            initWeatherData(isMetric)
            return@withContext currentWeatherDao.getWeatherMetric()
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: WeatherResponse) {
        // Couroutines are lightweight threads in kotlin
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(
                CurrentWeatherEntry(fetchedWeather.main.feelsLike,
                    null,
                    fetchedWeather.main.temp,
                    fetchedWeather.visibility,
                    fetchedWeather.weather.map { it.description },
                    fetchedWeather.weather.map { it.icon },
                    fetchedWeather.wind.deg,
                    fetchedWeather.wind.speed))
            weatherLocationDao.upsert(WeatherLocation(
                fetchedWeather.sys.country,
                fetchedWeather.name,
                fetchedWeather.coord.lat,
                fetchedWeather.coord.lon,
                System.currentTimeMillis()
            ))
        }
    }

    private suspend fun initWeatherData(isMetric: Boolean) {
        val lastWeatherLocation = weatherLocationDao.getLocationAsNormal()
        if (lastWeatherLocation == null) {
            fetchCurrentWeather(isMetric)
            return
        }
        if (locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather(isMetric)
            return
        }
        val instant: Instant = Instant.ofEpochSecond(lastWeatherLocation.lastTimeEpoch)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
        if(isFetchCurrentNeeded(zonedDateTime)){
            fetchCurrentWeather(isMetric)
            return
        }
    }

    private suspend fun fetchCurrentWeather(isMetric: Boolean) {
        val unit = if (isMetric) "metric" else "imperial"
        weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString(),
            unit)
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}