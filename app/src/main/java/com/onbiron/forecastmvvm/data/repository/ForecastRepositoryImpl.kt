package com.onbiron.forecastmvvm.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.onbiron.forecastmvvm.R
import com.onbiron.forecastmvvm.data.db.dao.current.CurrentWeatherDao
import com.onbiron.forecastmvvm.data.db.dao.forecast.ForecastDao
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.db.entity.forecast.*
import com.onbiron.forecastmvvm.data.network.WeatherNetworkDataSource
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.current.Sys
import com.onbiron.forecastmvvm.data.network.response.forecast.ForecastResponse
import com.onbiron.forecastmvvm.data.network.response.forecast.Weather
import com.onbiron.forecastmvvm.data.provider.LocationProvider
import com.onbiron.forecastmvvm.data.provider.PreferenceProvider
import kotlinx.coroutines.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val forecastDao: ForecastDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider,
    private val context: Context,
) : ForecastRepository {
    private val TAG = this::class.java.simpleName
    override suspend fun getCurrentWeather(): LiveData<CurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            val weatherData = initCurrentWeatherData()
            if (weatherData != null) {
                persistFetchedCurrentWeather(weatherData)
            }
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getForecast(): LiveData<Forecast> {
        return withContext(Dispatchers.IO) {
            val weatherData = initForecast()
            if (weatherData != null) {
                persistForecastData(weatherData)
            }
            return@withContext forecastDao.getForecast()
        }
    }

    override suspend fun refreshForecast() {
        withContext(Dispatchers.IO) {
            val nextFetchTime = forecastDao.getForecastAsNormal()?.location?.lastTimeEpoch!! + 3600000
            val now = System.currentTimeMillis()
            if (nextFetchTime <= now) {
                val fetchedForecast = fetchForecast()
                if (fetchedForecast != null) {
                    persistForecastData(fetchedForecast)
                } else {}
            } else {
                val leftTime = (nextFetchTime - now) / 60000
                withContext(Dispatchers.Main){
                    Toast.makeText(context,
                        "${context.getString(R.string.refresh_error_message)} $leftTime minute",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
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
                fetchedWeather.wind.speed,
                WeatherLocation(
                    fetchedWeather.sys.country,
                    fetchedWeather.name,
                    fetchedWeather.coord.lat,
                    fetchedWeather.coord.lon,
                    fetchedWeather.dt)

            ))
    }

    private fun persistForecastData(fetchedWeather: ForecastResponse) {
        val dailyForecasts = mutableListOf<ForecastDaily>()
        val hourlyForecasts = mutableListOf<ForecastHourly>()
        val minutelyForecasts = mutableListOf<ForecastMinutely>()
        val address = locationProvider.getAddressFromLatLon(fetchedWeather.lat, fetchedWeather.lon)
        val currentForecast = ForecastCurrent(fetchedWeather.current.dt,
            fetchedWeather.current.feelsLike,
            fetchedWeather.current.humidity,
            fetchedWeather.current.temp,
            extractForecastWeatherData(fetchedWeather.current.weather),
            fetchedWeather.current.windSpeed,
            fetchedWeather.current.uvi,
            fetchedWeather.current.visibility,
            fetchedWeather.current.pressure,
            fetchedWeather.current.sunrise,
            fetchedWeather.current.sunset)
        for (item in fetchedWeather.daily) {
            dailyForecasts.add(ForecastDaily(
                item.dt,
                item.humidity,
                item.pop,
                item.pressure,
                item.rain,
                item.sunrise,
                item.sunset,
                item.temp,
                item.uvi,
                extractForecastWeatherData(item.weather),
                item.windSpeed,
            ))
        }
        for (item in fetchedWeather.hourly) {
            hourlyForecasts.add(ForecastHourly(
                item.dt,
                item.feelsLike,
                item.humidity,
                item.pop,
                item.pressure,
                item.temp,
                item.uvi,
                item.visibility,
                extractForecastWeatherData(item.weather),
                item.windSpeed
            ))
        }

        for (item in fetchedWeather.minutely) {
            minutelyForecasts.add(ForecastMinutely(
                item.dt,
                item.precipitation
            ))
        }

        forecastDao.upsert(
            Forecast(currentForecast,
                dailyForecasts,
                hourlyForecasts,
                minutelyForecasts,
                WeatherLocation(
                    address?.countryName ?: "",
                    address?.subAdminArea ?: "",
                    fetchedWeather.lat,
                    fetchedWeather.lon,
                    System.currentTimeMillis())))
    }

    private suspend fun initCurrentWeatherData(): CurrentWeatherResponse? {
        var currentWeatherResponse: CurrentWeatherResponse? = null
        if (isCurrentFetchNeeded()) {
            currentWeatherResponse =
                weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString())
        }
        return currentWeatherResponse
    }

    private suspend fun initForecast(): ForecastResponse? {
        var forecastResponse: ForecastResponse? = null
        if (isForecastFetchNeeded()) {
            forecastResponse = fetchForecast()
        }
        return forecastResponse
    }

    private suspend fun fetchForecast(): ForecastResponse? {
        return weatherNetworkDataSource.fetchForecast(locationProvider.getPreferredLocationString())
    }


    private suspend fun isCurrentFetchNeeded(): Boolean {
        val currentWeatherEntry = currentWeatherDao.getCurrentWeatherAsNormal()
        if (currentWeatherEntry == null) {
            Log.d(TAG, "No weather info found. Needs fetch current weather")
            return true
        }
        if (locationProvider.hasLocationChanged(currentWeatherEntry.location)) {
            Log.d(TAG, "Location changed. Needs fetch current weather")
            return true
        }
        val instant: Instant = Instant.ofEpochSecond(currentWeatherEntry.location.lastTimeEpoch)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        if (zonedDateTime.isBefore(thirtyMinutesAgo)) {
            Log.d(TAG, "Last fetch time is before now. Needs fetch current weather.")
            return true
        }
        Log.d(TAG, "No need to fetch current weather.")
        return false
    }

    private suspend fun isForecastFetchNeeded(): Boolean {
        val forecastData = forecastDao.getForecastAsNormal()
        if (forecastData == null) {
            Log.d(TAG, "No weather info found. Needs fetch forecast.")
            return true
        }
        if (locationProvider.hasLocationChanged(forecastData.location)) {
            Log.d(TAG, "Location changed. Needs fetch forecast.")
            return true
        }
        val lastInstants: Instant =
            Instant.ofEpochSecond(forecastData.daily[0].timestamp).truncatedTo(
                ChronoUnit.DAYS)
        val nowInstant: Instant = ZonedDateTime.now().toInstant().truncatedTo(
            ChronoUnit.DAYS)
        if (lastInstants != nowInstant) {
            Log.d(TAG, "Last fetch time is before now. Needs fetch forecast.")
            return true
        }
        Log.d(TAG, "No need to fetch forecast.")
        return false
    }

    private fun extractForecastWeatherData(weather: List<Weather>): List<ForecastWeather> {
        val forecastWeathers = mutableListOf<ForecastWeather>()
        for (item in weather) {
            forecastWeathers.add(ForecastWeather(item.description, item.icon, item.id, item.main))
        }
        return forecastWeathers
    }
}