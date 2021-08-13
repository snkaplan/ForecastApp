package com.onbiron.forecastmvvm.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onbiron.forecastmvvm.data.db.entity.CURRENT_WEATHER_ID
import com.onbiron.forecastmvvm.data.db.entity.CurrentWeatherEntry

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weather: CurrentWeatherEntry)

    @Query("select * from CURRENT_WEATHER where id = $CURRENT_WEATHER_ID")
    fun getWeatherMetric(): LiveData<CurrentWeatherEntry>
}