package by.vsdev.blealert.ui.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.ConnectionUiState
import by.vsdev.blealert.ui.atoms.PrimaryButton
import by.vsdev.blealert.ui.molecules.ConnectionStatusChip
import kmp_ble_alert.sharedui.generated.resources.Res
import kmp_ble_alert.sharedui.generated.resources.action_disconnect
import kmp_ble_alert.sharedui.generated.resources.action_reconnect
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectionStatusBar(
    state: ConnectionUiState,
    onDisconnect: () -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ConnectionStatusChip(state)
            when (state) {
                ConnectionUiState.CONNECTED, ConnectionUiState.CONNECTING ->
                    PrimaryButton(text = stringResource(Res.string.action_disconnect), onClick = onDisconnect)
                ConnectionUiState.DISCONNECTED ->
                    PrimaryButton(text = stringResource(Res.string.action_reconnect), onClick = onReconnect)
                ConnectionUiState.IDLE, ConnectionUiState.SCANNING -> Unit
            }
        }
    }
}
