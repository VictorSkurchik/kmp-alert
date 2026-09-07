package by.vsdev.blealert.core.alert

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * The actual runtime permission request dialog can only be shown from an Activity
 * (`ActivityResultContracts.RequestPermission`), so [requestPermission] here only reports the
 * current OS-level status; androidApp's MainActivity drives the actual request flow.
 */
class AndroidNotificationPermissionManager(private val context: Context) : NotificationPermissionManager {

    override fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun requestPermission(): Boolean = hasPermission()
}
