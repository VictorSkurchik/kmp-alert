package by.vsdev.blealert.ui.molecules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.core.ble.BleDevice

@Composable
fun DeviceListItem(device: BleDevice, onClick: (BleDevice) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(16.dp),
    ) {
        Text(text = device.name ?: "Unknown device", style = MaterialTheme.typography.bodyLarge)
        Text(text = device.id, style = MaterialTheme.typography.bodySmall)
    }
}
