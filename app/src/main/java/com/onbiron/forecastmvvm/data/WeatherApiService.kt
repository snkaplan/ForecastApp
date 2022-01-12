package com.onbiron.forecastmvvm.data

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.onbiron.forecastmvvm.data.network.ConnectivityInterceptor
import com.onbiron.forecastmvvm.data.network.response.current.CurrentWeatherResponse
import com.onbiron.forecastmvvm.data.network.response.forecast.ForecastResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// http://api.weatherstack.com/current?access_key=ca9c44badc775bdd5dab2f4bbf183ff5&query=London
// http://api.weatherstack.com/current?access_key=ca9c44badc775bdd5dab2f4bbf183ff5&query=New%20York
interface WeatherApiService {
    @GET("weather")
    fun getCurrentWeatherByNameAsync(
        @Query("q") location: String,
        @Query("units") unit: String,
    ): Deferred<CurrentWeatherResponse>

    @GET("weather")
    fun getCurrentWeatherByLatLonAsync(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
    ): Deferred<CurrentWeatherResponse>

    @GET("onecall")
    fun getForecastByLatLonAsync(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") unit: String,
    ): Deferred<ForecastResponse>

    companion object {
        // invoke is a special method. We can call this by WeatherApiService()
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor,
            apiKey: String,
            apiStr: String
        ): WeatherApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request().url().newBuilder().addQueryParameter("appid", apiKey)
                    .build()
                val request = chain.request().newBuilder().url(url).build()
                return@Interceptor chain.proceed(request)
            }
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor) //Interceptors check if some dependencies are satisfied and ensure the flow. This is a request interceptor to creates request
                .addInterceptor(connectivityInterceptor) //Interceptors check if some dependencies are satisfied and ensure the flow. This is a custom interceptor to check network connection
                .build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(apiStr)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}