package com.dataliz.gpsmock.domain.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.dataliz.gpsmock.utils.openDeveloperOptions
import com.dataliz.gpsmock.utils.showDialogForSettingAppAsMockLocations
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationMockHelper(private val context: Context) {
    private val LOCATION_ACCURACY = 10f
    private val PROVIDER = LocationManager.GPS_PROVIDER
    fun startLocationMocking(locationManager: LocationManager, targetLocation: LatLng, provider : String = PROVIDER) {
        mockLocationForProvider(provider, locationManager, targetLocation)
        mockFusedLocationForProvider(PROVIDER, targetLocation)
    }

    fun mockLocationForProvider(
        provider: String,
        locationManager: LocationManager,
        targetLocation: LatLng
    ){
        try{
            mockLocationForProviderMethod1(
                provider,
                locationManager,
                targetLocation
            )
        } catch (e : Exception){
                mockLocationForProviderMethod2(
                    provider,
                    locationManager,
                    targetLocation
                )
           // } catch (e : Exception){
           //     Log.d(TAG, "no way was possible in mocking location (although mocking fused location is in progress.")


        }

    }

    fun mockLocationForProviderMethod1(
        provider: String = LocationManager.GPS_PROVIDER,
        locationManager: LocationManager,
        targetLocation: LatLng
    ) {
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

    fun mockLocationForProviderMethod2(
        provider: String,
        locationManager: LocationManager,
        targetLocation: LatLng
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                locationManager.setTestProviderEnabled(provider, true)
                locationManager.getProviders(true)
                    .forEach { locationManager.removeTestProvider(it) }
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
                locationManager.setTestProviderLocation(
                    provider,
                    createMockLocation(provider, targetLocation)
                )
            } catch (e: SecurityException) {
                // Handle the case where the app is not allowed to mock location
                // This might happen if the user hasn't enabled mock locations
                // in developer options. You can show a message to the user.
                Log.e("MapViewModel", "Error setting up mock location", e)
                // Option 1: Show an in-app dialog explaining how to enable mock locations
                showDialogForSettingAppAsMockLocations(context)
                // Option 2: Open developer options directly
                openDeveloperOptions(context)
                return
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error setting up mock location", e)
                // Option 1: Show an in-app dialog explaining how to enable mock locations
                showDialogForSettingAppAsMockLocations(context)
                // Option 2: Open developer options directly
                openDeveloperOptions(context)
            }
        }
    }

    private fun mockLocationForProviderMethod3(
        provider: String,
        locationManager: LocationManager,
        targetLocation: LatLng
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                locationManager.setTestProviderEnabled(provider, true)
                locationManager.getProviders(true)
                    .forEach { locationManager.removeTestProvider(it) }
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
                locationManager.setTestProviderLocation(
                    provider,
                    createMockLocation(provider, targetLocation)
                )
            } catch (e: SecurityException) {
                // Handle the case where the app is not allowed to mock location
                // This might happen if the user hasn't enabled mock locations
                // in developer options. You can show a message to the user.
                Log.e("MapViewModel", "Error setting up mock location", e)
                // Option 1: Show an in-app dialog explaining how to enable mock locations
                showDialogForSettingAppAsMockLocations(context)
                // Option 2: Open developer options directly
                openDeveloperOptions(context)
                return
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error setting up mock location", e)
                // Option 1: Show an in-app dialog explaining how to enable mock locations
                showDialogForSettingAppAsMockLocations(context)
                // Option 2: Open developer options directly
                openDeveloperOptions(context)
            }
        }
    }

    fun stopLocationMocking(locationManager: LocationManager) {
        locationManager.getProviders(true)
            .forEach { locationManager.removeTestProvider(it) }
        stopMockFusedLocation()
    }

    fun createMockLocation(provider: String, targetLocation: LatLng): Location {
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
    fun mockFusedLocationForProvider(provider: String, targetLocation: LatLng) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val mockLocation = createMockLocation(provider, targetLocation)
        try {
            // Add a mock location to the Fused Location Provider (requires Developer Options to be enabled)
            fusedLocationProviderClient.setMockMode(true)
            fusedLocationProviderClient.setMockLocation(mockLocation)
        } catch (e: SecurityException) {
            // Handle the case where the app is not allowed to mock location
            // This might happen if the user hasn't enabled mock locations
            // in developer options. You can show a message to the user.
            Log.e("MapViewModel", "Error setting up mock location", e)
            // Option 1: Show an in-app dialog explaining how to enable mock locations
            showDialogForSettingAppAsMockLocations(context)
            // Option 2: Open developer options directly
            openDeveloperOptions(context)
            return
        }
    }

    @SuppressLint("MissingPermission")
    fun stopMockFusedLocation(){
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.setMockMode(false)
    }
}