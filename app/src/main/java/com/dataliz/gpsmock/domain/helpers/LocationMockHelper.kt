package com.dataliz.gpsmock.domain.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationMockHelper (private val context : Context) {
    private val LOCATION_ACCURACY = 10f
    fun startLocationMocking(locationManager: LocationManager, targetLocation: LatLng) {
        mockLocationForProvider(LocationManager.NETWORK_PROVIDER, locationManager, targetLocation)
        mockLocationForProvider(LocationManager.GPS_PROVIDER, locationManager, targetLocation)
        mockFusedLocationForProvider(LocationManager.NETWORK_PROVIDER, targetLocation)
        mockFusedLocationForProvider(LocationManager.GPS_PROVIDER, targetLocation)
    }
    private fun mockLocationForProvider(provider: String, locationManager: LocationManager, targetLocation: LatLng) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                locationManager.setTestProviderEnabled(provider, true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    locationManager.addTestProvider(
                        provider,
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
                }
            } catch (e: SecurityException) {
                // Handle the case where the app is not allowed to mock location
                // This might happen if the user hasn't enabled mock locations
                // in developer options. You can show a message to the user.
                Log.e("MapViewModel", "Error setting up mock location", e)
                return
            }

            locationManager.setTestProviderLocation(provider, createMockLocation(provider, targetLocation))
        }
    }

    fun stopLocationMocking(locationManager: LocationManager) {
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
    }

    private fun createMockLocation(provider: String, targetLocation: LatLng): Location {
        val mockLocation = Location(provider)
        return mockLocation.apply {
            latitude = targetLocation.latitude
            longitude = targetLocation.longitude
            accuracy = LOCATION_ACCURACY // Set desired accuracy
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }

    @SuppressLint("MissingPermission")
    private fun mockFusedLocationForProvider(provider: String, targetLocation: LatLng){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val mockLocation = createMockLocation(provider, targetLocation)
        // Add a mock location to the Fused Location Provider (requires Developer Options to be enabled)
        fusedLocationProviderClient.setMockMode(true)
        fusedLocationProviderClient.setMockLocation(mockLocation)
    }
}