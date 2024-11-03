package com.dataliz.gpsmock.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dataliz.gpsmock.R
import com.dataliz.gpsmock.utils.TAG
import com.dataliz.gpsmock.presentation.viewmodels.MapViewModel
import com.dataliz.gpsmock.utils.hasAllMockLocationPermissions
import com.dataliz.gpsmock.utils.hasLocationPermission
import com.dataliz.gpsmock.utils.hasMockLocationPermission
import com.dataliz.gpsmock.utils.openDeveloperOptions
import com.dataliz.gpsmock.utils.showDialogForEnablingMockLocations
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                val viewModel: MapViewModel = viewModel()

                NavHost(navController = navController, startDestination = "map") {
                    composable("map") {
                        MapScreen(
                            viewModel,
                            navController,
                            this@MainActivity
                        )
                    }
                    composable("about") { AboutScreen(viewModel, navController) }
                    composable("dialog") { DialogScreen(viewModel) }
                }
            }
        }
    }
}

@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter", "MissingPermission",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    navController: NavHostController,
    activity: ComponentActivity
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = viewModel.cameraPosition.value
    }
    var isLocationMockingStarted = viewModel.isLocationMockingStarted.collectAsStateWithLifecycle()
    val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.all { it.value }) {
                viewModel.isLocationPermissionGranted (true)
                viewModel.fetchUserLocation()
                if (!hasMockLocationPermission(context)) {
                    showDialogForEnablingMockLocations(context)
                    // Option 2: Open developer options directly
                    openDeveloperOptions(context)
                }
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.check_permissions_again),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    Scaffold(
        //topBar = { TopAppBar(title = { Text("Google Map") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isLocationMockingStarted.value) {
                        Log.d(TAG, "here1")
                        // Stop location mocking
                        viewModel.stopLocationMocking(locationManager)
                    } else {
                        if (hasAllMockLocationPermissions(context)) {
                            // Permission already granted, start mocking
                            viewModel.startLocationMockingRepeatedly(
                                locationManager,
                                cameraPositionState.position.target
                            )
                        } else {
                            Log.d(TAG, "asking for permissions")
                            // Request ACCESS_MOCK_LOCATION permission
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                launcher.launch(
                                    arrayOf(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    )
                                )
                            } else {
                                Log.d(TAG, "here4")
                                launcher.launch(
                                    arrayOf(
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    )
                                )
                            }
                        }
                    }
                }, content = {

                }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box (modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {
                MapComposable(viewModel, cameraPositionState)
                Icon(
                    painter = painterResource(id = R.drawable.baseline_location_on_24),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(50.dp)
                        .height(50.dp),
                    contentDescription = "Map Marker",
                    tint = Color.Unspecified // Or apply a tint if you want a different color
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { navController.navigate("about") }) {
                    Text("About")
                }
                Button(onClick = { navController.navigate("dialog") }) {
                    Text("Show Dialog")
                }
            }
        }

    }

}

@Composable
fun AboutScreen(viewModel: MapViewModel, navController: NavHostController) {
    // ... (About screen code - same as previous example)
}

@Composable
fun DialogScreen(viewModel: MapViewModel) {
    Dialog(onDismissRequest = { /* Handle dialog dismiss */ }) {
        CustomDialogUI(viewModel)
    }
}

@Composable
fun CustomDialogUI(viewModel: MapViewModel) {
    val context = LocalContext.current
    val appInfo = viewModel.appInfo

    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Title Row (only text)
            Text(
                text = "App Information",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Icon + Text Rows
            DialogRow(
                icon = Icons.Default.AccountCircle,
                text = "Developer: ${appInfo.developerName}"
            )
            DialogRow(icon = Icons.Default.Email, text = "Email: ${appInfo.email}")
            DialogRow(icon = Icons.Default.Phone, text = "Version: ${appInfo.version}")
            DialogRow(icon = Icons.Default.Info, text = "This is a sample dialog")
        }
    }
}

@Composable
fun DialogRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp),
            tint = Color.Gray // Change the icon color as needed
        )
        Text(text = text)
    }
}

@Composable
fun MapComposable(viewModel: MapViewModel, cameraPositionState: CameraPositionState){
    val userLocation = viewModel.userLocation
    val hasLocationPermission = viewModel.hasLocationPermission.collectAsStateWithLifecycle()
    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission.value), // Enable user location
        uiSettings = MapUiSettings(myLocationButtonEnabled = true, zoomControlsEnabled = true) // Enable the default button
    ) {

        LaunchedEffect(key1 = userLocation.value) {
            if(!viewModel.isLocationMockingStarted.value){
                userLocation.value?.let { location ->
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(location.latitude, location.longitude))
                                .zoom(15f) // Set your desired zoom level
                                .build()
                        )
                    )
                }
            }
        }
        // Fixed Marker at the center of the screen
        /*Marker(
            state = MarkerState(position = cameraPositionState.position.target),
            title = "Fixed Marker",
            snippet = "This marker stays at the center"
        )

        // Marker for user's location (if available)
        val userLocation = viewModel.userLocation.collectAsState()
        userLocation.value?.let { latLng ->
            Marker(
                state = MarkerState(position = latLng),
                title = "Your Location",
                snippet = "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
            )
        }*/

    }
}