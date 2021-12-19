package com.onbiron.forecastmvvm.ui.weather.current

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastHourly
import com.onbiron.forecastmvvm.data.network.response.future.Hourly
import com.onbiron.forecastmvvm.databinding.TimelineTemperatureItemBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalField
import java.util.*

class CurrentRecyclerViewAdapter(
    private val hourlyList: List<ForecastHourly>,
    private val clickListener: (ForecastHourly) -> Unit,
) : RecyclerView.Adapter<CurrentRecyclerViewAdapter.MViewHolder>() {
    private lateinit var itemBinding: TimelineTemperatureItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        itemBinding =
            TimelineTemperatureItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return MViewHolder(itemBinding.root)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bind(hourlyList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    inner class MViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(hourly: ForecastHourly, clickListener: (ForecastHourly) -> Unit) {
            Glide.with(view)
                .load("http://openweathermap.org/img/wn/${hourly.weather[0].icon}@2x.png")
                .into(itemBinding.tempImg)
            itemBinding.tempTv.text = hourly.temperature.toString()
            itemBinding.timeTv.text = SimpleDateFormat("MM-dd/HH:mm").format(Date(hourly.timestamp * 1000))
            // itemBinding.myTextView.text = hourly.name
            view.setOnClickListener {
                clickListener(hourly)
            }
        }
    }
}