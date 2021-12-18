package com.onbiron.forecastmvvm.ui.weather.current

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.onbiron.forecastmvvm.R
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.network.response.future.Hourly
import com.onbiron.forecastmvvm.databinding.CurrentWeatherFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.time.Instant
import java.time.ZoneId
import java.util.*

// TODO Fetch weather when metric changed.
class CurrentWeatherFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val TAG = this::class.java.simpleName
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel
    private lateinit var binding: CurrentWeatherFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CurrentWeatherFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CurrentWeatherViewModel::class.java]
        bindUI()
    }

    // Global scope is not safe for classes which has lifecycle.
    // Operations in launch could be finish after activity destroyed then the app will be crashed.
    // So that we have to use our custom fragments which has custom coroutine lifecycle
    private fun bindUI() = launch {
        val cal: Calendar = Calendar.getInstance()
        val dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US)
        val dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.currentWeatherInclude.dateTv.text = "$dayOfWeek, $dayOfMonth"
        val currentWeatherJob = async { viewModel.weather.await() }
        val weatherLocationJob = async { viewModel.location.await() }
        val forecastJob = async { viewModel.forecast.await() }
        val currentWeather = currentWeatherJob.await()
        val weatherLocation = weatherLocationJob.await()
        val forecast = forecastJob.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })
        currentWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.d(TAG, "No data found for current weather.")
                return@Observer
            }
            binding.groupLoading.visibility = View.GONE
            updateTemperatures(it.temperature,
                it.feelsLike,
                it.minTemp,
                it.maxTemp,
                it.weatherDescriptions[0])
            updateWind(it.windDir.toString(), it.windSpeed)
            updateVisibility(it.visibility)
            updateHumidity(it.humidity)
            Glide.with(this@CurrentWeatherFragment)
                .load("http://openweathermap.org/img/wn/${it.weatherIcons[0]}@2x.png")
                .into(binding.currentWeatherInclude.temperatureIdentifierImage)
        })

        forecast.observe(viewLifecycleOwner, Observer {
            var initialPos = 0
            for(idx in it.hourly.indices){
                val dt = Instant.ofEpochSecond(it.hourly[idx].dt)
                if(Instant.now().isBefore(dt)){
                    initialPos = idx + 1
                    break
                }
            }
            binding.currentWeatherInclude.timelineRv.apply{
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                setItemViewCacheSize(48)
                adapter = CurrentRecyclerViewAdapter(it.hourly
                ) { selectedHourly: Hourly -> hourlyItemClicked(selectedHourly) }
                scrollToPosition(initialPos)
                updatePrecipitation(it.daily[1].rain)
            }
        })

    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial

    }

    private fun updateTemperatures(
        temperature: Double,
        feelsLike: Double,
        min: Double,
        max: Double,
        condition: String,
    ) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(getString(R.string.celcius),
            getString(R.string.fahrenheit))
        binding.currentWeatherInclude.let {
            it.temperatureTv.text = "$temperature°$unitAbbreviation"
            it.minMaxTempTv.text = "$min° / $max°"
            it.conditionTv.text = "$condition today".uppercase()   //e.g Possible Rain

        }
    }

    private fun updateLocation(location: String) {
        binding.currentWeatherInclude.cityTv.text = location
    }

    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.milimeter), getString(R.string.inch))
        binding.currentWeatherInclude.precipitationInclude.infoImage.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ic_precipitationandsun_icon, null))
        binding.currentWeatherInclude.precipitationInclude.infoTv.text =
            "$precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.kph), getString(R.string.mph))
        binding.currentWeatherInclude.windInclude.infoImage.setImageDrawable(ResourcesCompat.getDrawable(
            resources, R.drawable.wind_icon, null))
        binding.currentWeatherInclude.windInclude.infoTv.text =
            "$windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Int) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.meter), getString(R.string.mile))
        binding.currentWeatherInclude.visibilityInclude.infoImage.setImageDrawable(ResourcesCompat.getDrawable(
            resources, R.drawable.ic_visibility_icon, null))
        binding.currentWeatherInclude.visibilityInclude.infoTv.text =
            "$visibilityDistance $unitAbbreviation"
    }

    private fun updateHumidity(humidity: Double) {
        binding.currentWeatherInclude.humidityInclude.infoImage.setImageDrawable(ResourcesCompat.getDrawable(
            resources, R.drawable.ic_weather_humidity_icon, null))
        binding.currentWeatherInclude.humidityInclude.infoTv.text =
            "$humidity%"
    }

    private fun hourlyItemClicked(hourly: Hourly) {
        Toast.makeText(activity, "Hourly Info: $hourly", Toast.LENGTH_LONG).show()
    }
}