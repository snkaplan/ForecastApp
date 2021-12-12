package com.onbiron.forecastmvvm.data.db.dao.future

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onbiron.forecastmvvm.data.db.entity.future.FUTURE_WEATHER_ID
import com.onbiron.forecastmvvm.data.db.entity.future.FutureWeatherEntry

@Dao
interface FutureWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(futureWeather: FutureWeatherEntry)

    @Query("select * from FUTURE_WEATHER where id = $FUTURE_WEATHER_ID")
    fun getFutureWeather(): LiveData<FutureWeatherEntry>
}