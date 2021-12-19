package com.onbiron.forecastmvvm.ui.weather.forecast.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastDaily
import com.onbiron.forecastmvvm.databinding.ForecastFragmentBinding
import com.onbiron.forecastmvvm.ui.base.ScopedFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.onbiron.forecastmvvm.R
import com.onbiron.forecastmvvm.ui.weather.forecast.detail.ForecastDetailFragment


class ForecastFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val TAG = this::class.java.simpleName
    private val viewModelFactory: ForecastViewModelProvider by instance()
    private lateinit var viewModel: ForecastViewModel
    private lateinit var binding: ForecastFragmentBinding
    private val detailFragment = ForecastDetailFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ForecastFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[ForecastViewModel::class.java]
        bindUI()
    }

    private fun bindUI() = launch {
        binding.forecastGroupLoading.visibility = View.VISIBLE
        binding.forecastCl.visibility = View.GONE
        val cal: Calendar = Calendar.getInstance()
        val dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US)
        val dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.forecastInclude.dateTv.text = "$dayOfWeek, $dayOfMonth"
        val forecastJob = async { viewModel.forecast.await() }
        val forecast = forecastJob.await()
        forecast.observe(viewLifecycleOwner, Observer{
            if (it == null) {
                Log.d(TAG, "No forecast data found.")
                return@Observer
            }
            updateLocation(it.location.name)
            binding.forecastInclude.forecastRv.apply{
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                setItemViewCacheSize(10)
                adapter = ForecastRecyclerViewAdapter(it.daily) { selectedDaily: ForecastDaily -> dailyItemClicked(selectedDaily) }
            }
        })
        binding.forecastGroupLoading.visibility = View.GONE
        binding.forecastCl.visibility = View.VISIBLE
    }

    private fun dailyItemClicked(daily: ForecastDaily) {
        viewModel.setSelectedDaily(daily)
        if(!detailFragment.isResumed){
            val fm: FragmentManager = childFragmentManager
            val ft: FragmentTransaction = fm.beginTransaction()
            ft.replace(R.id.detail_fragment_container, detailFragment)
            ft.commit()
        } else {
            Log.d(TAG, "dailyItemClicked: fragment already resumed")
        }
    }

    private fun updateLocation(location: String) {
        binding.forecastInclude.locationTv.text = location
    }

}