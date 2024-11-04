package com.dataliz.gpsmock.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.dataliz.gpsmock.R
import com.dataliz.gpsmock.domain.helpers.LocationMockHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

fun showDialogForEnablingDeveloperOptions(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(ContextCompat.getString(context, R.string.enable_mock_locations))
        .setMessage(R.string.to_use_this_feature_enable_developer)
        .setPositiveButton(R.string.open_settings) { dialog, _ ->
            openDeveloperOptions(context)
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}
fun showDialogForSettingAppAsMockLocations(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(ContextCompat.getString(context, R.string.enable_mock_locations))
        .setMessage(R.string.to_use_this_feature_developer)
        .setPositiveButton(R.string.open_settings) { dialog, _ ->
            openDeveloperOptions(context)
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun openDeveloperOptions(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
    context.startActivity(intent)
}

fun hasAllMockLocationPermissions(context: Context): Boolean {
    return (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                hasLocationPermission(context)
                        &&
                        hasPostNotificationPermission(context)
                        &&
                        isDeveloperOptionsEnabled(context)
                        &&
                        hasMockLocationPermission(context)
            } else {
                hasLocationPermission(context)
                        &&
                        isDeveloperOptionsEnabled(context)
                        &&
                        hasMockLocationPermission(context)
            }
            )
}

fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun hasPostNotificationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

fun isDeveloperOptionsEnabled(context: Context) : Boolean{
    return android.provider.Settings.Secure.getInt(context.contentResolver,
        android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0
}

fun hasMockLocationPermission(context: Context): Boolean {
    return try {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        // Add a mock location to the Fused Location Provider (requires Developer Options to be enabled)
        fusedLocationProviderClient.setMockMode(true)

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        LocationMockHelper(context).apply {
            mockLocationForProviderMethod1(
                LocationManager.GPS_PROVIDER,
                locationManager,
                LatLng(37.7749, -122.4194)
            )
            stopLocationMocking(locationManager)
        }
        true
    } catch (e: SecurityException) {
        false
    }
}