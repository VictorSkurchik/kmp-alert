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

@Composable
fun ConnectionStatusChip(state: ConnectionUiState, modifier: Modifier = Modifier) {
    val color = when (state) {
        ConnectionUiState.CONNECTED -> StatusColors.connected
        ConnectionUiState.CONNECTING, ConnectionUiState.SCANNING -> StatusColors.connecting
        ConnectionUiState.DISCONNECTED -> StatusColors.disconnected
        ConnectionUiState.IDLE -> StatusColors.idle
    }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        StatusDot(color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = state.name, style = MaterialTheme.typography.labelLarge)
    }
}
