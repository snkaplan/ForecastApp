package com.onbiron.forecastmvvm.ui.weather.forecast.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onbiron.forecastmvvm.R
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastDaily
import com.onbiron.forecastmvvm.databinding.ForecastRecyclerViewItemBinding
import java.util.*

class ForecastRecyclerViewAdapter(
    private val dailyList: List<ForecastDaily>,
    private val clickListener: (ForecastDaily, Int) -> Unit,
) : RecyclerView.Adapter<ForecastRecyclerViewAdapter.MViewHolder>() {
    private lateinit var itemBinding: ForecastRecyclerViewItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        itemBinding =
            ForecastRecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return MViewHolder(itemBinding.root)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.bind(dailyList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return dailyList.size
    }

    inner class MViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(daily: ForecastDaily, clickListener: (ForecastDaily, Int) -> Unit) {
            val cal: Calendar =
                Calendar.getInstance().apply { timeInMillis = daily.timestamp * 1000 }
            val dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US)
            val dayOfMonth = cal[Calendar.DAY_OF_MONTH]
            Glide.with(view)
                .load("http://openweathermap.org/img/wn/${daily.weather[0].icon}@2x.png")
                .into(itemBinding.tempImg)
            itemBinding.dayTv.text = "$dayOfWeek, $dayOfMonth"
            itemBinding.tempCurrent.text = "${daily.temperature.day}°"
            itemBinding.tempMin.text = "${daily.temperature.min}°"
            itemBinding.tempMax.text = "${daily.temperature.max}°"
            itemBinding.tempIdentifierTv.text = daily.weather[0].description.uppercase()
            itemBinding.forecastRvItemLayout.setOnClickListener {
                clickListener(daily, adapterPosition)
            }
            // itemBinding.myTextView.text = hourly.name

//            SimpleDateFormat("MM-dd/HH:mm").format(Date(hourly.timestamp * 1000))
        }
    }
}