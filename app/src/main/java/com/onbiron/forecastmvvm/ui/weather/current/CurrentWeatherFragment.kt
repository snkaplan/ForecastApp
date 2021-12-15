package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.onbiron.forecastmvvm.R
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.databinding.CurrentWeatherFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
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
        val currentWeather = currentWeatherJob.await()
        val weatherLocation = weatherLocationJob.await()

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
            updateTemperatures(it.temperature, it.feelsLike, it.minTemp, it.maxTemp, it.weatherDescriptions[0])
            it.precip?.let { it1 -> updatePrecipitation(it1) }
            updateWind(it.windDir.toString(), it.windSpeed)
            updateVisibility(it.visibility)
            updateHumidity(it.humidity)
            Glide.with(this@CurrentWeatherFragment)
                .load("http://openweathermap.org/img/wn/${it.weatherIcons[0]}@2x.png")
                .into(binding.currentWeatherInclude.temperatureIdentifierImage)

        })
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial

    }

    private fun updateLocation(location: String) {
        binding.currentWeatherInclude.cityTv.text = location
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double, min: Double, max: Double, condition: String) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation(getString(R.string.celcius),
            getString(R.string.fahrenheit))
        binding.currentWeatherInclude.let {
            it.temperatureTv.text = "$temperature°$unitAbbreviation"
            it.minMaxTempTv.text = "$min° / $max°"
            it.feelsLikeIncl.infoLbl.text = getString(R.string.feels_like)
            it.feelsLikeIncl.infoTv.text = "$feelsLike°"
            it.conditionTv.text = condition  //e.g Possible Rain

        }

    }
    private fun updatePrecipitation(precipitationVolume: Double) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.milimeter), getString(R.string.inch))
        binding.currentWeatherInclude.precipitationInclude.infoLbl.text =
            getString(R.string.precipation)
        binding.currentWeatherInclude.precipitationInclude.infoTv.text =
            "$precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.kph), getString(R.string.mph))
        binding.currentWeatherInclude.windInclude.infoLbl.text = getString(R.string.wind)
        binding.currentWeatherInclude.windInclude.infoTv.text =
            "$windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Int) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.meter), getString(R.string.mile))
        binding.currentWeatherInclude.visibilityInclude.infoLbl.text =
            getString(R.string.visibility)
        binding.currentWeatherInclude.visibilityInclude.infoTv.text =
            "$visibilityDistance $unitAbbreviation"
    }

    private fun updateHumidity(humidity: Double) {
        binding.currentWeatherInclude.humidityInclude.infoLbl.text = getString(R.string.humidity)
        binding.currentWeatherInclude.humidityInclude.infoTv.text =
            "$humidity%"
    }
}