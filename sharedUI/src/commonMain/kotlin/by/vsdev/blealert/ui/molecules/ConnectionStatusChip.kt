package by.vsdev.blealert.ui.molecules

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.ConnectionUiState
import by.vsdev.blealert.ui.atoms.StatusColors
import by.vsdev.blealert.ui.atoms.StatusDot
import kmp_ble_alert.sharedui.generated.resources.Res
import kmp_ble_alert.sharedui.generated.resources.connection_state_connected
import kmp_ble_alert.sharedui.generated.resources.connection_state_connecting
import kmp_ble_alert.sharedui.generated.resources.connection_state_disconnected
import kmp_ble_alert.sharedui.generated.resources.connection_state_idle
import kmp_ble_alert.sharedui.generated.resources.connection_state_scanning
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectionStatusChip(state: ConnectionUiState, modifier: Modifier = Modifier) {
    val color = when (state) {
        ConnectionUiState.CONNECTED -> StatusColors.connected
        ConnectionUiState.CONNECTING, ConnectionUiState.SCANNING -> StatusColors.connecting
        ConnectionUiState.DISCONNECTED -> StatusColors.disconnected
        ConnectionUiState.IDLE -> StatusColors.idle
    }
    val label = stringResource(
        when (state) {
            ConnectionUiState.CONNECTED -> Res.string.connection_state_connected
            ConnectionUiState.CONNECTING -> Res.string.connection_state_connecting
            ConnectionUiState.SCANNING -> Res.string.connection_state_scanning
            ConnectionUiState.DISCONNECTED -> Res.string.connection_state_disconnected
            ConnectionUiState.IDLE -> Res.string.connection_state_idle
        },
    )
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        StatusDot(color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}
