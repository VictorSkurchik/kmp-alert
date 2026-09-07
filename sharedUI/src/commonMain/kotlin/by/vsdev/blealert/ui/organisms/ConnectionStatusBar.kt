package by.vsdev.blealert.ui.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.ConnectionUiState
import by.vsdev.blealert.ui.atoms.PrimaryButton
import by.vsdev.blealert.ui.molecules.ConnectionStatusChip

@Composable
fun ConnectionStatusBar(
    state: ConnectionUiState,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ConnectionStatusChip(state)
        if (state == ConnectionUiState.CONNECTED || state == ConnectionUiState.CONNECTING) {
            PrimaryButton(text = "Disconnect", onClick = onDisconnect)
        }
    }
}
