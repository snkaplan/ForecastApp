package com.onbiron.forecastmvvm.data.db.entity

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onbiron.forecastmvvm.data.network.response.future.Current
import com.onbiron.forecastmvvm.data.network.response.future.Daily
import com.onbiron.forecastmvvm.data.network.response.future.Hourly
import java.lang.reflect.Type


object Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        val listType: Type = object : TypeToken<List<String?>?>() {}.getType()
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStrToCurrentObject(value: String?): Current {
        val listType: Type = object : TypeToken<Current?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromCurrentObject(current: Current): String {
        val gson = Gson()
        return gson.toJson(current)
    }

    @TypeConverter
    fun fromStringToDaily(value: String?): List<Daily> {
        val listType: Type = object : TypeToken<List<Daily?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDailyListToString(list: List<Daily?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }


    @TypeConverter
    fun fromStringToHourly(value: String?): List<Hourly> {
        val listType: Type = object : TypeToken<List<Hourly?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromHourlyListToString(list: List<Hourly?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}