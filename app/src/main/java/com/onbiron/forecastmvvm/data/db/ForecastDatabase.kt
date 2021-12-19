package com.onbiron.forecastmvvm.data.db

import android.content.Context
import androidx.room.*
import com.onbiron.forecastmvvm.data.db.dao.current.CurrentWeatherDao
import com.onbiron.forecastmvvm.data.db.dao.forecast.ForecastDao
import com.onbiron.forecastmvvm.data.db.entity.Converters
import com.onbiron.forecastmvvm.data.db.entity.current.CurrentWeatherEntry
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.data.db.entity.forecast.Forecast


@Database(
        entities = [CurrentWeatherEntry::class, Forecast::class],
        version = 21,
        exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class ForecastDatabase: RoomDatabase(){

    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun forecastDao (): ForecastDao

    companion object{
        @Volatile private var instance: ForecastDatabase? = null // Volatile stands for all threads have immediate access to instance
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        ForecastDatabase::class.java, "forecast.db").fallbackToDestructiveMigration().build()


    }
}