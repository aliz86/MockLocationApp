package com.dataliz.gpsmock

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dataliz.gpsmock.ui.theme.GPSMockTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                val navController = rememberNavController()
                val viewModel: MapViewModel = viewModel()

                NavHost(navController = navController, startDestination = "map") {
                    composable("map") { MapScreen(viewModel, navController) }
                    composable("about") { AboutScreen(viewModel, navController) }
                    composable("dialog") { DialogScreen(viewModel) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(viewModel: MapViewModel, navController: NavHostController) {
    val cameraPositionState = rememberCameraPositionState {
        position = viewModel.cameraPosition.value
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Google Map") }) }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState
            ) {
                MapMarker(viewModel)
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
fun MapMarker(viewModel: MapViewModel){
    val cameraPosition by viewModel.cameraPosition.collectAsState()

    Marker(
        state = MarkerState(position = cameraPosition.target),
        title = "Marker",
        snippet = "Example Marker"
    )
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
            .fillMaxHeight(0.8f),
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
            DialogRow(icon = Icons.Default.AccountCircle, text = "Developer: ${appInfo.developerName}")
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