package com.herlianzhang.mikropos.ui.printer

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection

@Composable
fun PrinterItem(
    item: BluetoothConnection,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (item.isConnected) MaterialTheme.colors.primarySurface else Color.Gray,
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                item.device.name ?: "-",
                style = MaterialTheme.typography
                    .h6
                    .copy(fontWeight = FontWeight.Bold)
            )
            Text(
                item.device.address ?: "-",
                style = MaterialTheme
                    .typography
                    .body2
                    .copy(color = Color.Gray)
            )
        }
        if (!item.isConnected) {
            TextButton(onClick = onClick) {
                Text("Hubungkan")
            }
        } else {
            Text(
                "Terhubung",
                color = Color.Green
            )
        }
    }
}