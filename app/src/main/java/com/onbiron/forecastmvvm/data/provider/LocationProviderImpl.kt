package com.onbiron.forecastmvvm.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.onbiron.forecastmvvm.data.db.entity.WeatherLocation
import com.onbiron.forecastmvvm.internal.LocationPermissionNotGrantedException
import com.onbiron.forecastmvvm.internal.asDeferred
import kotlinx.coroutines.Deferred

import android.location.Geocoder
import java.io.IOException
import java.util.*
import kotlin.math.abs


const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"
const val DEFAULT_LOCATION = "London"

class LocationProviderImpl(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : PreferenceProvider(context), LocationProvider {
    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            return false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    override suspend fun getPreferredLocationString(): CustomLocation {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation =
                    getLastDeviceLocation().await()
                        ?: return CustomLocation(getCustomLocationName() ?: DEFAULT_LOCATION,
                            null,
                            null)
                return CustomLocation(null, deviceLocation.latitude, deviceLocation.longitude)
            } catch (e: LocationPermissionNotGrantedException) {
                return CustomLocation(getCustomLocationName() ?: DEFAULT_LOCATION, null, null)
            }
        } else {
            return CustomLocation(getCustomLocationName() ?: DEFAULT_LOCATION, null, null)
        }
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        // Comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation()) {
            val customLocationName = getCustomLocationName()
            return customLocationName != lastWeatherLocation.name
        }
        return false
    }

    private fun isUsingDeviceLocation(): Boolean {
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    override fun getAddressFromLatLon(lat: Double, lng: Double) : Address?{
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
            return addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun getAddressFromName(name: String): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocationName(name, 1)
            return addresses[0]
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


}