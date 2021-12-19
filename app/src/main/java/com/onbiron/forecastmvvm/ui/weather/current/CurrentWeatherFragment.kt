package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.onbiron.forecastmvvm.R
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastDaily
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastHourly
import com.onbiron.forecastmvvm.databinding.CurrentWeatherFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.time.Instant
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
        val forecastJob = async { viewModel.forecast.await() }
        val forecast = forecastJob.await()
        forecast.observe(viewLifecycleOwner, {
            var initialPos = 0
            updateLocation(it.location.name)
            binding.groupLoading.visibility = View.GONE
            var currentDay: ForecastDaily? = null
            for(item in it.daily){
                if(DateUtils.isToday(item.timestamp * 1000)){
                    currentDay = item
                    break
                }
            }
            updateTemperatures(it.current.temperature,
                it.current.feelsLike,
                currentDay?.temperature?.min.toString(),
                currentDay?.temperature?.max.toString(),
                it.current.forecastWeather[0].description)
            updateWind(it.current.windSpeed)
            updateVisibility(it.current.visibility)
            updateHumidity(it.current.humidity)
            Glide.with(this@CurrentWeatherFragment)
                .load("http://openweathermap.org/img/wn/${it.current.forecastWeather[0].icon}@2x.png")
                .into(binding.currentWeatherInclude.temperatureIdentifierImage)
            for(idx in it.hourly.indices){
                val dt = Instant.ofEpochSecond(it.hourly[idx].timestamp)
                if(Instant.now().isBefore(dt)){
                    initialPos = idx + 1
                    break
                }
            }
            binding.currentWeatherInclude.timelineRv.apply{
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                setItemViewCacheSize(48)
                adapter = CurrentRecyclerViewAdapter(it.hourly
                ) { selectedHourly: ForecastHourly -> hourlyItemClicked(selectedHourly) }
                scrollToPosition(initialPos)
                updatePrecipitation(currentDay?.pop.toString())
            }
        })

    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetric) metric else imperial

    }

    private fun updateTemperatures(
        temperature: Double,
        feelsLike: Double,
        min: String,
        max: String,
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

    private fun updatePrecipitation(precipitationVolume: String  = "-") {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.milimeter), getString(R.string.inch))
        binding.currentWeatherInclude.precipitationInclude.infoImage.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ic_precipitationandsun_icon, null))
        binding.currentWeatherInclude.precipitationInclude.infoTv.text =
            "$precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windSpeed: Double) {
        val unitAbbreviation =
            chooseLocalizedUnitAbbreviation(getString(R.string.kph), getString(R.string.mph))
        binding.currentWeatherInclude.windInclude.infoImage.setImageDrawable(ResourcesCompat.getDrawable(
            resources, R.drawable.wind_icon, null))
        binding.currentWeatherInclude.windInclude.infoTv.text =
            "$windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
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

    private fun hourlyItemClicked(hourly: ForecastHourly) {
        Toast.makeText(activity, "Hourly Info: $hourly", Toast.LENGTH_LONG).show()
    }
}