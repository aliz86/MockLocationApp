package com.dataliz.gpsmock.presentation.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.dataliz.gpsmock.utils.AppInfo
import com.dataliz.gpsmock.service.MockLocService
import com.dataliz.gpsmock.service.OnMockModeChanged
import com.dataliz.gpsmock.utils.TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.ref.WeakReference

class MapViewModel(private val application: Application) : AndroidViewModel(application) {

    val appInfo = AppInfo()
    private var _isLocationMockingStarted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLocationMockingStarted: StateFlow<Boolean> = _isLocationMockingStarted.asStateFlow()

    private val _cameraPosition = MutableStateFlow(
        CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 12f)
    )

    var cameraPosition = _cameraPosition.asStateFlow()

    private val _targetMockLocation = MutableStateFlow<LatLng?>(null)
    val targetMockLocation = _targetMockLocation.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation = _targetMockLocation.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow<Boolean>(false)
    val hasLocationPermission = _hasLocationPermission.asStateFlow()

    //when the service stops, the mockLocationService object should be garbege colllected , too. but withput this WeakReference line, westilll will have a reference to that service object
    private var mockLocService: WeakReference<MockLocService?> = WeakReference(null)

    private var isServiceBound = false

    private lateinit var locationManager: LocationManager
    private lateinit var targetLocation: LatLng
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }


    // Define the connection to the service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            val binder = iBinder as MockLocService.LocalBinder
            mockLocService = WeakReference(binder.service)
            isServiceBound = true
            mockLocService.get()?.startLocationMocking(locationManager, targetLocation)
            mockLocService.get()?.onMockModeChanged = object : OnMockModeChanged {
                override fun modeChanged(mode: Boolean) {
                    _isLocationMockingStarted.value = mode
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    init {
        if (_hasLocationPermission.value) {
            fetchUserLocation()
        }
    }

    fun updateUserLocation(_locationManager: LocationManager, _location: LatLng) {

        _targetMockLocation.value = targetLocation
        _cameraPosition.value = CameraPosition.fromLatLngZoom(targetLocation, 16f)
        startLocationMockingRepeatedly(locationManager, targetLocation)
    }

    fun startLocationMockingRepeatedly(_locationManager: LocationManager, _location: LatLng) {

        Log.d(TAG, "here2")

        locationManager = _locationManager
        targetLocation = _location

        val serviceIntent = Intent(application.applicationContext, MockLocService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            application.applicationContext.startForegroundService(serviceIntent)
        } else {
            application.applicationContext.startService(serviceIntent)
        }
        application.applicationContext.bindService(
            serviceIntent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
        setLocationMocking(true)
    }

    fun stopLocationMocking(locationManager: LocationManager) {
        application.applicationContext.unbindService(serviceConnection)
        mockLocService.get()?.stopLocationMocking(locationManager)
        setLocationMocking(false)
    }

    fun setLocationMocking(mode: Boolean) {
        _isLocationMockingStarted.value = mode
    }

    fun isLocationPermissionGranted(isGranted: Boolean) {
        _hasLocationPermission.value = isGranted
    }

    @SuppressLint("MissingPermission")
    fun fetchUserLocation() {
        fusedLocationClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null)
                //Toast.makeText(this, "Cannot get location.", Toast.LENGTH_SHORT).show()
                else {
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mockLocService.clear()
            application.applicationContext.unbindService(serviceConnection)
        } catch (e: Exception) {

        }
    }
}