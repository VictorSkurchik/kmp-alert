package by.vsdev.blealert.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.core.ble.BleDevice
import by.vsdev.blealert.ui.organisms.DeviceList
import by.vsdev.blealert.ui.templates.MonitoringScaffold

@Composable
fun ScanScreen(viewModel: MonitoringViewModel, onDeviceSelected: (BleDevice) -> Unit) {
    LaunchedEffect(Unit) { viewModel.startScanning() }
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    MonitoringScaffold(title = "Scan for devices") { modifier ->
        DeviceList(devices = devices, onDeviceClick = onDeviceSelected, modifier = modifier)
    }
}
