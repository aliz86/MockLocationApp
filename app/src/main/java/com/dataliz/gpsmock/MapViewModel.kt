package com.dataliz.gpsmock

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    // Initial camera position (you can change this to your desired location)
    private val _cameraPosition = MutableStateFlow(
        CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 12f)
    )
    val cameraPosition = _cameraPosition.asStateFlow()

    val appInfo = AppInfo()
}