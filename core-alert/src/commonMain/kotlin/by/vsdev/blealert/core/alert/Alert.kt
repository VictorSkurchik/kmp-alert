package by.vsdev.blealert.core.alert

data class Alert(
    val id: String,
    val type: AlertType,
    val severity: AlertSeverity,
    val receivedAt: Long,
    val sourceDeviceId: String,
)

enum class AlertType { MOTION, SOS, DEVICE_OFFLINE }

enum class AlertSeverity { INFO, WARNING, CRITICAL }

fun deviceOfflineAlert(sourceDeviceId: String): Alert = Alert(
    id = "$sourceDeviceId-offline-${currentTimeMillis()}",
    type = AlertType.DEVICE_OFFLINE,
    severity = AlertSeverity.CRITICAL,
    receivedAt = currentTimeMillis(),
    sourceDeviceId = sourceDeviceId,
)
