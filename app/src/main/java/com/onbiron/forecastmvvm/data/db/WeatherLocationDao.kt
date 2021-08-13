package com.onbiron.forecastmvvm.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onbiron.forecastmvvm.data.db.entity.CURRENT_WEATHER_ID
import com.onbiron.forecastmvvm.data.db.entity.WEATHER_LOCATION_ID
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation

@Dao
interface WeatherLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherLocation: WeatherLocation)

    @Query("SELECT * FROM WEATHER_LOCATION where id = $WEATHER_LOCATION_ID")
    fun getLocation(): LiveData<WeatherLocation>

    @Query("SELECT * FROM WEATHER_LOCATION where id = $WEATHER_LOCATION_ID")
    fun getLocationAsNormal(): WeatherLocation

    @Query("select * from WEATHER_LOCATION where id = $WEATHER_LOCATION_ID")
    fun getLocationNonLive(): WeatherLocation?
}