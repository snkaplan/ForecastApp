package com.onbiron.forecastmvvm.data.repository

import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.data.db.dao.current.CurrentWeatherDao
import com.onbiron.forecastmvvm.data.db.dao.WeatherLocationDao
import com.onbiron.forecastmvvm.data.db.dao.future.FutureWeatherDao
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.db.entity.future.FutureWeatherEntry
import com.onbiron.forecastmvvm.data.network.WeatherNetworkDataSource
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.future.FutureWeatherResponse
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
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider,
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            run {
                persistFetchedCurrentWeather(newCurrentWeather)
            }
        }

        weatherNetworkDataSource.downloadedFutureWeather.observeForever { newFutureWeather ->
            run {
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }

    override suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry> {
        // withcontext same as GlobalScope but only different is withContext returns some data
        return withContext(Dispatchers.IO) {
            initCurrentWeatherData(isMetric)
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getFutureWeather(isMetric: Boolean): LiveData<FutureWeatherEntry> {
        // withcontext same as GlobalScope but only different is withContext returns some data
        return withContext(Dispatchers.IO) {
            initFutureWeatherData(isMetric)
            return@withContext futureWeatherDao.getFutureWeather()
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
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
                fetchedWeather.dt
            ))
        }
    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {
        // Couroutines are lightweight threads in kotlin
        GlobalScope.launch(Dispatchers.IO) {
            futureWeatherDao.upsert(
                FutureWeatherEntry(fetchedWeather.current,
                    fetchedWeather.daily,
                    fetchedWeather.hourly))
        }
    }

    private suspend fun initCurrentWeatherData(isMetric: Boolean) {
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
        if (isFetchNeeded(zonedDateTime)) {
            fetchCurrentWeather(isMetric)
            return
        }
    }

    private suspend fun initFutureWeatherData(isMetric: Boolean) {
        val lastWeatherLocation = weatherLocationDao.getLocationAsNormal()
        if (lastWeatherLocation == null) {
            fetchFutureWeather(isMetric)
            return
        }
        if (locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchFutureWeather(isMetric)
            return
        }
        val instant: Instant = Instant.ofEpochSecond(lastWeatherLocation.lastTimeEpoch)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
        if (isFetchNeeded(zonedDateTime)) {
            fetchFutureWeather(isMetric)
            return
        }
    }

    private suspend fun fetchCurrentWeather(isMetric: Boolean) {
        val unit = if (isMetric) "metric" else "imperial"
        weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString(),
            unit)
    }

    private suspend fun fetchFutureWeather(isMetric: Boolean) {
        val unit = if (isMetric) "metric" else "imperial"
        weatherNetworkDataSource.fetchFutureWeather(locationProvider.getPreferredLocationString(),
            unit)
    }

    private fun isFetchNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}