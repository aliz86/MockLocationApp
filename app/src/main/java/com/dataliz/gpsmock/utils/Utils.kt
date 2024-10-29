package com.dataliz.gpsmock.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.dataliz.gpsmock.R
import com.google.android.gms.location.LocationServices

fun showDialogForEnablingMockLocations(context: Context) {
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
                        hasMockLocationPermission(context)
            } else {
                hasLocationPermission(context)
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

fun hasMockLocationPermission(context: Context): Boolean {
    return try {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        // Add a mock location to the Fused Location Provider (requires Developer Options to be enabled)
        fusedLocationProviderClient.setMockMode(true)
        true
    } catch (e: SecurityException) {
        false
    }
}