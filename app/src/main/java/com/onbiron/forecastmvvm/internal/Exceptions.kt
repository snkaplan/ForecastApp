package com.onbiron.forecastmvvm.internal

import java.io.IOException
import java.lang.Exception

class NoConnectivityException : IOException()
class LocationPermissionNotGrantedException : Exception()