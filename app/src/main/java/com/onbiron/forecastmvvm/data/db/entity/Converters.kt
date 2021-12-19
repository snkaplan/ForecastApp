package com.onbiron.forecastmvvm.data.db.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastCurrent
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastDaily
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastHourly
import com.onbiron.forecastmvvm.data.db.entity.forecast.ForecastMinutely
import java.lang.reflect.Type


object Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStrToCurrentObject(value: String?): ForecastCurrent {
        val listType: Type = object : TypeToken<ForecastCurrent?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCurrentObject(current: ForecastCurrent): String {
        val gson = Gson()
        return gson.toJson(current)
    }

    @TypeConverter
    fun fromStrToWeatherLocation(value: String?): WeatherLocation {
        val listType: Type = object : TypeToken<WeatherLocation?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromWeatherLocation(weatherLocation: WeatherLocation): String {
        val gson = Gson()
        return gson.toJson(weatherLocation)
    }

    @TypeConverter
    fun fromStringToDaily(value: String?): List<ForecastDaily> {
        val listType: Type = object : TypeToken<List<ForecastDaily?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDailyListToString(list: List<ForecastDaily?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun fromStringToHourly(value: String?): List<ForecastHourly> {
        val listType: Type = object : TypeToken<List<ForecastHourly?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromHourlyListToString(list: List<ForecastHourly?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToMinutely(value: String?): List<ForecastMinutely> {
        val listType: Type = object : TypeToken<List<ForecastMinutely?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromMinutelyListToString(list: List<ForecastMinutely?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}