package com.onbiron.forecastmvvm.data.db.dao.forecast

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onbiron.forecastmvvm.data.db.entity.forecast.CURRENT_FORECAST_ID
import com.onbiron.forecastmvvm.data.db.entity.forecast.Forecast

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(forecast: Forecast)

    @Query("select * from forecast where id = $CURRENT_FORECAST_ID")
    fun getForecast(): LiveData<Forecast>

    @Query("select * from forecast where id = $CURRENT_FORECAST_ID")
    fun getForecastAsNormal(): Forecast?
}