package com.dataliz.gpsmock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class MockLocService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val notificationId = 1234
    private val channelId = "my_foreground_service_channel"
    private lateinit var notification : Notification
    private val locationMockHandler by lazy {
        LocationMockHandler()
    }

    private val stopServiceIntent by lazy {
        PendingIntent.getService(
            this,
            0,
            Intent(this, MockLocService::class.java).setAction("STOP_SERVICE"),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        notification = createNotification()
        startForeground(notificationId, notification)

        Log.d(TAG, "onStartCommand, startId = $startId")
        if (intent?.action == "STOP_SERVICE") {
            stopSelf() // Stop the service
            return START_NOT_STICKY
        }
        //startLocationMocking()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG,"onBind")
        return LocalBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop any coroutines or ongoing tasks when the service is destroyed
        serviceScope.cancel()
    }

    fun startLocationMocking(locationManager: LocationManager, targetLocation: LatLng){
        serviceScope.launch {
            while (isActive) {
                locationMockHandler.startLocationMocking(locationManager, targetLocation)
                delay(300)
            }
        }
    }

    fun stopLocationMocking(locationManager: LocationManager) {
        locationMockHandler.stopLocationMocking(locationManager)
    }

    // Create and show the notification for the foreground service
    private fun createNotification(): Notification {
        // Intent to open your app when the notification is tapped
        val notificationIntent = Intent(this, MainActivity::class.java) // Replace with your activity
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service")
            .setContentText("Service is running in the foreground")
            .setSmallIcon(androidx.core.R.drawable.notification_bg) // Replace with your icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)// Make it an ongoing notification
            .setContentText("asdasda")
            .setContentTitle("dfgdfgdf")
            .addAction(
                androidx.core.R.drawable.notification_tile_bg, // Replace with your stop icon drawable
                "Stop",
                stopServiceIntent
            )
            .build()
    }

    // Create the notification channel (required for Android Oreo and above)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Foreground Service Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class LocalBinder : Binder() {
        val service: MockLocService
            get() = this@MockLocService
    }

}