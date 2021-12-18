package com.onbiron.forecastmvvm.data.repository

import android.util.Log
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
import com.onbiron.forecastmvvm.data.provider.UnitProvider
import com.onbiron.forecastmvvm.internal.UnitSystem
import kotlinx.coroutines.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider,
) : ForecastRepository {
    private val TAG = this::class.java.simpleName

    override suspend fun getCurrentWeather(isMetric: Boolean): LiveData<CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            val weatherData = initCurrentWeatherData(isMetric)
            if(weatherData!= null){
                persistFetchedCurrentWeather(weatherData)
            }
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getFutureWeather(isMetric: Boolean): LiveData<FutureWeatherEntry> {
        return withContext(Dispatchers.IO) {
            val weatherData = initFutureWeatherData(isMetric)
            if(weatherData!= null){
                persistFetchedFutureWeather(weatherData)
            }
            return@withContext futureWeatherDao.getFutureWeather()
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        weatherLocationDao.upsert(WeatherLocation(
            fetchedWeather.sys.country,
            fetchedWeather.name,
            fetchedWeather.coord.lat,
            fetchedWeather.coord.lon,
            fetchedWeather.dt
        ))
        currentWeatherDao.upsert(
            CurrentWeatherEntry(fetchedWeather.main.feelsLike,
                fetchedWeather.main.humidity,
                fetchedWeather.main.temp,
                fetchedWeather.main.tempMin,
                fetchedWeather.main.tempMax,
                fetchedWeather.visibility,
                fetchedWeather.weather.map { it.description },
                fetchedWeather.weather.map { it.icon },
                fetchedWeather.wind.deg,
                fetchedWeather.wind.speed))

    }

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {
        futureWeatherDao.upsert(
            FutureWeatherEntry(fetchedWeather.current,
                fetchedWeather.daily,
                fetchedWeather.hourly))
    }

    private suspend fun initCurrentWeatherData(isMetric: Boolean): CurrentWeatherResponse? {
        var currentWeatherResponse: CurrentWeatherResponse? = null
        if (isCurrentFetchNeeded()) {
            val unit = if (isMetric) "metric" else "imperial"
            currentWeatherResponse =
                weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString(),
                    unit)
        }
        return currentWeatherResponse
    }

    private suspend fun initFutureWeatherData(isMetric: Boolean): FutureWeatherResponse? {
        var futureWeatherResponse: FutureWeatherResponse? = null
        if (isFutureFetchNeeded()) {
            val unit = if (isMetric) "metric" else "imperial"
            futureWeatherResponse =
                weatherNetworkDataSource.fetchFutureWeather(locationProvider.getPreferredLocationString(),
                    unit)
        }
        return futureWeatherResponse
    }


    private suspend fun isCurrentFetchNeeded(): Boolean {
        val lastWeatherLocation = weatherLocationDao.getLocationAsNormal()
        if (lastWeatherLocation == null) {
            Log.d(TAG, "No weather info found. Needs fetch current weather")
            return true
        }
        if (locationProvider.hasLocationChanged(lastWeatherLocation)) {
            Log.d(TAG, "Location changed. Needs fetch current weather")
            return true
        }
        val instant: Instant = Instant.ofEpochSecond(lastWeatherLocation.lastTimeEpoch)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        if (zonedDateTime.isBefore(thirtyMinutesAgo)) {
            Log.d(TAG, "Last fetch time is before now. Needs fetch current weather.")
            return true
        }
        Log.d(TAG, "No need to fetch current weather.")
        return false
    }

    private suspend fun isFutureFetchNeeded(): Boolean {
        val lastWeatherLocation = weatherLocationDao.getLocationAsNormal()
        val futureWeatherEntry = futureWeatherDao.getFutureWeatherAsNormal()
        if (futureWeatherEntry == null || lastWeatherLocation == null) {
            Log.d(TAG, "No weather info found. Needs fetch future weather.")
            return true
        }
        if (locationProvider.hasLocationChanged(lastWeatherLocation)) {
            Log.d(TAG, "Location changed. Needs fetch future weather.")
            return true
        }
        val lastInstanst: Instant =
            Instant.ofEpochSecond(futureWeatherEntry.daily[0].dt).truncatedTo(
                ChronoUnit.DAYS)
        val nowInstant: Instant = ZonedDateTime.now().toInstant().truncatedTo(
            ChronoUnit.DAYS)
        if (lastInstanst != nowInstant) {
            Log.d(TAG, "Last fetch time is before now. Needs fetch future weather.")
            return true
        }
        Log.d(TAG, "No need to fetch future weather.")
        return false
    }
}