package by.vsdev.blealert.ui.format

import androidx.compose.runtime.Composable
import by.vsdev.blealert.domain.AlertType
import by.vsdev.blealert.domain.currentTimeMillis
import kmp_ble_alert.core_ui.generated.resources.Res
import kmp_ble_alert.core_ui.generated.resources.alert_type_device_offline
import kmp_ble_alert.core_ui.generated.resources.alert_type_long_bathroom_time
import kmp_ble_alert.core_ui.generated.resources.alert_type_motion
import kmp_ble_alert.core_ui.generated.resources.alert_type_no_activity
import kmp_ble_alert.core_ui.generated.resources.alert_type_not_returned_home
import kmp_ble_alert.core_ui.generated.resources.alert_type_sos
import kmp_ble_alert.core_ui.generated.resources.alert_type_woke_up
import kmp_ble_alert.core_ui.generated.resources.time_days_ago
import kmp_ble_alert.core_ui.generated.resources.time_hours_ago
import kmp_ble_alert.core_ui.generated.resources.time_just_now
import kmp_ble_alert.core_ui.generated.resources.time_minutes_ago
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlertType.displayName(): String = stringResource(
    when (this) {
        AlertType.MOTION -> Res.string.alert_type_motion
        AlertType.SOS -> Res.string.alert_type_sos
        AlertType.DEVICE_OFFLINE -> Res.string.alert_type_device_offline
        AlertType.WOKE_UP -> Res.string.alert_type_woke_up
        AlertType.LONG_BATHROOM_TIME -> Res.string.alert_type_long_bathroom_time
        AlertType.NOT_RETURNED_HOME -> Res.string.alert_type_not_returned_home
        AlertType.NO_ACTIVITY -> Res.string.alert_type_no_activity
    },
)

@Composable
fun formatRelativeTime(receivedAt: Long, now: Long = currentTimeMillis()): String {
    val diffMinutes = (now - receivedAt) / 60_000
    return when {
        diffMinutes < 1 -> stringResource(Res.string.time_just_now)
        diffMinutes < 60 -> stringResource(Res.string.time_minutes_ago, diffMinutes.toInt())
        diffMinutes < 24 * 60 -> stringResource(Res.string.time_hours_ago, (diffMinutes / 60).toInt())
        else -> stringResource(Res.string.time_days_ago, (diffMinutes / (24 * 60)).toInt())
    }
}
