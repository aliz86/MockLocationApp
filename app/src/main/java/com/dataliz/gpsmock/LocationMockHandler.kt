package com.dataliz.gpsmock

import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.maps.model.LatLng

class LocationMockHandler {
    fun startLocationMocking(locationManager: LocationManager, targetLocation: LatLng) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                locationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false, // requiresNetwork
                    false, // requiresSatellite
                    false, // requiresCell
                    false, // hasMonetaryCost
                    false, // supportsAltitude
                    true, // supportsSpeed
                    false, // supportsBearing
                    ProviderProperties.POWER_USAGE_LOW, // powerRequirement
                    ProviderProperties.ACCURACY_FINE // accuracy
                )

                locationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false, // requiresNetwork
                    false, // requiresSatellite
                    false, // requiresCell
                    false, // hasMonetaryCost
                    false, // supportsAltitude
                    true, // supportsSpeed
                    false, // supportsBearing
                    ProviderProperties.POWER_USAGE_MEDIUM, // powerRequirement
                    ProviderProperties.ACCURACY_FINE // accuracy
                )
                locationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false, // requiresNetwork
                    false, // requiresSatellite
                    false, // requiresCell
                    false, // hasMonetaryCost
                    true, // supportsAltitude
                    true, // supportsSpeed
                    false, // supportsBearing
                    ProviderProperties.POWER_USAGE_HIGH, // powerRequirement
                    ProviderProperties.ACCURACY_FINE // accuracy
                )

                locationManager.addTestProvider(
                    LocationManager.NETWORK_PROVIDER,
                    false, // requiresNetwork
                    false, // requiresSatellite
                    false, // requiresCell
                    false, // hasMonetaryCost
                    true, // supportsAltitude
                    true, // supportsSpeed
                    false, // supportsBearing
                    ProviderProperties.POWER_USAGE_MEDIUM, // powerRequirement
                    ProviderProperties.ACCURACY_FINE // accuracy
                )
            } catch (e: SecurityException) {
                // Handle the case where the app is not allowed to mock location
                // This might happen if the user hasn't enabled mock locations
                // in developer options. You can show a message to the user.
                Log.e("MapViewModel", "Error setting up mock location", e)
                return
            }
            locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
            locationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true)

            val mockLocation = Location(LocationManager.GPS_PROVIDER)
            mockLocation.latitude = targetLocation.latitude
            mockLocation.longitude = targetLocation.longitude
            mockLocation.accuracy = 10f // Set desired accuracy
            mockLocation.time = System.currentTimeMillis()
            mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

            locationManager.setTestProviderLocation(LocationManager.NETWORK_PROVIDER, mockLocation)
            locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)
        }
    }

    fun stopLocationMocking(locationManager: LocationManager) {
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
    }
}