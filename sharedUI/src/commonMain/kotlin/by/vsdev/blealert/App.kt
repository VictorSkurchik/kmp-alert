package by.vsdev.blealert

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import by.vsdev.blealert.core.ble.BleDevice
import by.vsdev.blealert.ui.screens.MonitoringScreen
import by.vsdev.blealert.ui.screens.ScanScreen

private sealed class AppScreen {
    data object Scan : AppScreen()
    data object Monitoring : AppScreen()
}

@Composable
fun App(viewModel: MonitoringViewModel) {
    MaterialTheme {
        var screen by remember { mutableStateOf<AppScreen>(AppScreen.Scan) }

        when (screen) {
            AppScreen.Scan -> ScanScreen(
                viewModel = viewModel,
                onDeviceSelected = { device: BleDevice ->
                    viewModel.connect(device)
                    screen = AppScreen.Monitoring
                },
            )
            AppScreen.Monitoring -> MonitoringScreen(viewModel = viewModel)
        }
    }
}
