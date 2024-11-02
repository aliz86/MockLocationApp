package com.dataliz.gpsmock.service

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
import com.dataliz.gpsmock.domain.helpers.LocationMockHelper
import com.dataliz.gpsmock.presentation.ui.MainActivity
import com.dataliz.gpsmock.utils.STOP_MOCKING
import com.dataliz.gpsmock.utils.STOP_SERVICE
import com.dataliz.gpsmock.utils.TAG
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MockLocService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private var binder : LocalBinder? = null
    private val notificationId = 1234
    private val channelId = "my_foreground_service_channel"
    private lateinit var notification : Notification
    private val locationMockHelper by lazy {
        LocationMockHelper(applicationContext)
    }

    private val stopServiceIntent by lazy {
        PendingIntent.getService(
            this,
            0,
            Intent(this, MockLocService::class.java).setAction(STOP_SERVICE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val stopMockingIntent by lazy {
        PendingIntent.getService(
            this,
            0,
            Intent(this, MockLocService::class.java).setAction(STOP_MOCKING),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    var onMockModeChanged : OnMockModeChanged? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        notification = createNotification()
        startForeground(notificationId, notification)

        Log.d(TAG, "onStartCommand, startId = $startId")
        if (intent?.action == STOP_SERVICE) {
            stopSelf() // Stop the service
            return START_NOT_STICKY
        } else if(intent?.action == STOP_MOCKING){
            stopLocationMocking(getSystemService(Context.LOCATION_SERVICE) as LocationManager)
            onMockModeChanged?.modeChanged(false)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf() // Stop the service
            return START_NOT_STICKY
        }
        //startLocationMocking()
        //NOT_STICKY ecause it is supposed to execute in foreground and when it is dead and then it is started again, crash happens saying we don't have permission for it:
        //java.lang.RuntimeException: Unable to start service com.dataliz.gpsmock.MockLocService@65a4bd4 with null: android.app.ForegroundServiceStartNotAllowedException: Service.startForeground() not allowed due to mAllowStartForeground false: service com.dataliz.gpsmock/.MockLocService
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG,"onBind")
        binder = LocalBinder()
        return binder as LocalBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop any coroutines or ongoing tasks when the service is destroyed
        serviceScope.cancel()
    }

    fun startLocationMocking(locationManager: LocationManager, targetLocation: LatLng){
        serviceScope.launch {
            //loop
            while (isActive) {
                locationMockHelper.startLocationMocking(locationManager, targetLocation)
                stopLocationMocking(locationManager)
                delay(600)
            }
        }
    }

    fun stopLocationMocking(locationManager: LocationManager) {
        serviceScope.cancel()
        locationMockHelper.stopLocationMocking(locationManager)
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
                stopMockingIntent
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

    private fun clearNotification(){
        (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)?.cancel(notificationId)
    }

    inner class LocalBinder : Binder() {
        val service: MockLocService
            get() = this@MockLocService
    }
}