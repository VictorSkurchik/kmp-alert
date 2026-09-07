package by.vsdev.blealert.ui.organisms

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import by.vsdev.blealert.core.ble.BleDevice
import by.vsdev.blealert.ui.molecules.DeviceListItem

@Composable
fun DeviceList(devices: List<BleDevice>, onDeviceClick: (BleDevice) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(devices, key = { it.id }) { device ->
            DeviceListItem(device = device, onClick = onDeviceClick)
        }
    }
}
