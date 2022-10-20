package io.github.bradleyiw.ns.core.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.bradleyiw.ns.core.extension.isServiceRunning
import javax.inject.Inject

class ServiceController @Inject constructor(
    @ApplicationContext
    private var context: Context?
) {
    fun <T> start(service: Class<T>) {
        context?.let {
            val serviceIntent = Intent(it, service)
            ContextCompat.startForegroundService(it, serviceIntent)
        }
    }

    fun <T> stop(service: Class<T>) {
        context?.let {
            val serviceIntent = Intent(it, service)
            it.stopService(serviceIntent)
        }
    }

    fun <T> isRunning(service: Class<T>): Boolean =
        context?.isServiceRunning(service) ?: false
}
