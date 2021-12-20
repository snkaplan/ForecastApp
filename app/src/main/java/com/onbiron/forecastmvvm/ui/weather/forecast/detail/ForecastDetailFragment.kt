package com.onbiron.forecastmvvm.ui.weather.forecast.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.onbiron.forecastmvvm.databinding.ForecastDetailFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import com.onbiron.forecastmvvm.ui.weather.forecast.list.ForecastViewModel
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import java.text.SimpleDateFormat
import java.util.*

class ForecastDetailFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val TAG = this::class.java.simpleName
    private lateinit var binding: ForecastDetailFragmentBinding
    private val viewModel: ForecastViewModel by viewModels({requireParentFragment()})
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ForecastDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedDaily.observe(viewLifecycleOwner, Observer{
            if (it == null) {
                Log.d(TAG, "No forecast detail found.")
                return@Observer
            }
            val sdf = SimpleDateFormat("HH:mm")
            binding.sunriseInfoTv.text = sdf.format(Date(it.sunrise * 1000))
            binding.sunsetInfoTv.text = sdf.format(Date(it.sunset * 1000))
            binding.precipitationInfoTv.text = it.pop.toString()
            binding.humidityInfoTv.text = it.humidity.toString()
            binding.windInfoTv.text = it.windSpeed.toString()
            binding.pressureInfoTv.text = it.pressure.toString()
        })
    }
}