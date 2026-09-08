package by.vsdev.blealert.core.alert

import kotlinx.serialization.Serializable

@Serializable
data class Alert(
    val id: String,
    val type: AlertType,
    val severity: AlertSeverity,
    val receivedAt: Long,
    val sourceDeviceId: String,
)

@Serializable
enum class AlertType {
    MOTION,
    SOS,
    DEVICE_OFFLINE,
    WOKE_UP,
    LONG_BATHROOM_TIME,
    NOT_RETURNED_HOME,
    NO_ACTIVITY,
}

@Serializable
enum class AlertSeverity { INFO, WARNING, CRITICAL }

fun deviceOfflineAlert(sourceDeviceId: String): Alert = Alert(
    id = "$sourceDeviceId-offline-${currentTimeMillis()}",
    type = AlertType.DEVICE_OFFLINE,
    severity = AlertSeverity.CRITICAL,
    receivedAt = currentTimeMillis(),
    sourceDeviceId = sourceDeviceId,
)
