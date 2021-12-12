package com.onbiron.forecastmvvm.ui.weather.future.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onbiron.forecastmvvm.databinding.FutureListWeatherFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance()
    private lateinit var viewModel: FutureListWeatherViewModel
    private lateinit var binding: FutureListWeatherFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FutureListWeatherFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[FutureListWeatherViewModel::class.java]
        bindUI()
    }

    private fun bindUI() = launch {
        val currentWeather = viewModel.futureWeather.await()
        val weatherLocation = viewModel.location.await()
    }

}