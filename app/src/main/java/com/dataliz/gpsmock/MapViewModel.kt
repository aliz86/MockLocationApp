package com.dataliz.gpsmock

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel(private val application: Application) : AndroidViewModel(application) {

    val appInfo = AppInfo()
    private val _cameraPosition = MutableStateFlow(
        CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 12f)
    )
    var cameraPosition = _cameraPosition.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation = _userLocation.asStateFlow()

    private var mockLocService : MockLocService? = null

    private var isServiceBound = false

    private lateinit var locationManager: LocationManager
    private lateinit var targetLocation: LatLng

    // Define the connection to the service
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MockLocService.LocalBinder
            mockLocService = binder.service
            isServiceBound = true

            Log.d(TAG, "mockLocService == null? ${mockLocService == null}")
            mockLocService?.startLocationMocking(locationManager, targetLocation)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    fun updateUserLocation(_locationManager: LocationManager, _location: LatLng) {

        _userLocation.value = targetLocation
        _cameraPosition.value = CameraPosition.fromLatLngZoom(targetLocation, 16f)
        startLocationMockingRepeatedly(locationManager, targetLocation)
    }

    fun startLocationMockingRepeatedly(_locationManager: LocationManager, _location: LatLng) {

        locationManager = _locationManager
        targetLocation = _location

        val serviceIntent = Intent(application.applicationContext, MockLocService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.applicationContext.startForegroundService(serviceIntent)
        } else {
            application.applicationContext.startService(serviceIntent)
        }
        application.applicationContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    fun stopLocationMocking(locationManager: LocationManager){
        mockLocService?.stopLocationMocking(locationManager)
    }

    override fun onCleared() {
        super.onCleared()
        mockLocService = null
        application.applicationContext.unbindService(connection)
    }
}