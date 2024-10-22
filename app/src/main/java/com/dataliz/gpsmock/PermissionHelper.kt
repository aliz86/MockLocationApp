package com.dataliz.gpsmock

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper(private val activity: Activity) {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123 // You can use any unique code
    }

    fun requestPermissionWithRationale(
        permission: String,
        explanationMessage: String,
        onPermissionGranted: () -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, execute the granted action
            onPermissionGranted()
        } else {
            // Permission needs to be requested
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // Show an explanation dialog before requesting the permission again
                showExplanationDialog(permission, explanationMessage, onPermissionGranted)
            } else {
                // Request the permission directly
                ActivityCompat.requestPermissions(activity, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun showExplanationDialog(permission: String, message: String, onPermissionGranted: () -> Unit) {
        AlertDialog.Builder(activity)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                // Request the permission again
                ActivityCompat.requestPermissions(activity, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Call this method from your Activity's onRequestPermissionsResult()
    fun handlePermissionRequestResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onPermissionGranted: () -> Unit
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, execute the granted action
            onPermissionGranted()
        } else {
            // Permission denied, handle accordingly (e.g., show an error message)
            // You might want to check if the user selected "Don't ask again"
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                // User selected "Don't ask again" - take appropriate action
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(activity)
            .setTitle("Permission Denied")
            .setMessage("The app needs this permission to function properly. You can grant it in the app settings.")
            .setPositiveButton("OK", null)
            .show()
    }
}