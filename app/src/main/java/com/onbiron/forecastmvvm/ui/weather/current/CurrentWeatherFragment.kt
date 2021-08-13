package com.onbiron.forecastmvvm.ui.weather.current

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.onbiron.forecastmvvm.databinding.CurrentWeatherFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
// TODO Fetch weather when metric changed.
class CurrentWeatherFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel
    private lateinit var binding: CurrentWeatherFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = CurrentWeatherFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)
        bindUI()
    }

    // Global scope is not safe for classes which has lifecycle.
    // Operations in launch could be finish after activity destroyed then the app will be crashed.
    // So that we have to use our custom fragments which has custom coroutine lifecycle
    private fun bindUI() = launch{
        val currentWeather = viewModel.weather.await()
        val weatherLocation = viewModel.location.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if(location == null) return@Observer
            updateLocation(location.name)

        })

        currentWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.d("TAG", "bindUI: no data found ")
                return@Observer
            }
            binding.groupLoading.visibility = View.GONE
            updateDateToToday()
            updateTemperatures(it.temperature, it.feelslike)
            updateCondition(it.weatherDescriptions[0])
            updatePrecipitation(it.precip)
            updateWind(it.windDir, it.windSpeed)
            updateVisibility(it.visibility)

            Glide.with(this@CurrentWeatherFragment)
                    .load(it.weatherIcons[0])
                    .into(binding.imageViewConditionIcon)

        })
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial:String): String {
        return if(viewModel.isMetric) metric else imperial

    }

    private fun updateLocation(location: String){
        (activity as AppCompatActivity).supportActionBar?.title = location
    }

    private fun updateDateToToday(){
        (activity as AppCompatActivity).supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double){
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("C", "F")
        binding.textViewTemperature.text = "$temperature$unitAbbreviation"
        binding.textViewFeelsLikeTemperature.text = "Feels Like $feelsLike$unitAbbreviation"
    }

    private fun updateCondition(condition: String){
        binding.textViewCondition.text = condition
    }

    private fun updatePrecipitation(precipitationVolume : Double){
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("mm", "in")
        binding.textViewPrecipitation.text = "Precipitation: $precipitationVolume $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("kph", "mph")
        binding.textViewWind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("km", "mi.")
        binding.textViewVisibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }
}