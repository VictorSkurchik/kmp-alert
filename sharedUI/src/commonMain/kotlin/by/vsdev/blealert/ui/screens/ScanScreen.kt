package by.vsdev.blealert.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.core.ble.BleDevice
import by.vsdev.blealert.ui.organisms.DeviceList
import by.vsdev.blealert.ui.templates.MonitoringScaffold

@Composable
fun ScanScreen(viewModel: MonitoringViewModel, onDeviceSelected: (BleDevice) -> Unit) {
    // Scanning is started by BleMonitoringForegroundService once runtime permissions are
    // actually granted - starting it again here would race ahead of that permission check
    // (Kable throws immediately if scanning starts before permissions are granted).
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    MonitoringScaffold(title = "Scan for devices") { modifier ->
        DeviceList(devices = devices, onDeviceClick = onDeviceSelected, modifier = modifier)
    }
}
