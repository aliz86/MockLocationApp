package com.dataliz.gpsmock.utils

import android.content.ServiceConnection

data class AppInfo(
    val developerName: String = "Ali Hasanzadeh",
    val email: String = "ali.mm.hasanzadeh@gmail.com",
    val version: String = "1.0.0"
)

public interface MapServiceConnection : ServiceConnection {

}