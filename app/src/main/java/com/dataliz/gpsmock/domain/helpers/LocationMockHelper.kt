package com.dataliz.gpsmock.domain.helpers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dataliz.gpsmock.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class LocationMockHelper (private val context : Context) {
    private val LOCATION_ACCURACY = 10f
    fun startLocationMocking(locationManager: LocationManager, targetLocation: LatLng) {
        mockLocationForProvider(LocationManager.GPS_PROVIDER, locationManager, targetLocation)
        mockFusedLocationForProvider(LocationManager.GPS_PROVIDER, targetLocation)
    }
    private fun mockLocationForProvider(provider: String, locationManager: LocationManager, targetLocation: LatLng) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                locationManager.setTestProviderEnabled(provider, true)
                locationManager.getProviders(true).forEach { locationManager.removeTestProvider(it)}
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
                locationManager.setTestProviderLocation(provider, createMockLocation(provider, targetLocation))
            } catch (e: SecurityException) {
                // Handle the case where the app is not allowed to mock location
                // This might happen if the user hasn't enabled mock locations
                // in developer options. You can show a message to the user.
                Log.e("MapViewModel", "Error setting up mock location", e)
                // Option 1: Show an in-app dialog explaining how to enable mock locations
                showDialogForEnablingMockLocations()
                // Option 2: Open developer options directly
                openDeveloperOptions()
                return
            } catch (e : Exception){
                Toast.makeText(context, ContextCompat.getString(context, R.string.enable_mock_locations) + e, Toast.LENGTH_SHORT).show()
            }
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

    private fun showDialogForEnablingMockLocations() {
        AlertDialog.Builder(context)
            .setTitle(ContextCompat.getString(context, R.string.enable_mock_locations))
            .setMessage(R.string.to_use_this_feature_developer)
            .setPositiveButton(R.string.open_settings) { dialog, _ ->
                openDeveloperOptions()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openDeveloperOptions() {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        context.startActivity(intent)
    }
}