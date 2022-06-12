package com.herlianzhang.mikropos.ui.transaction.cart

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CartScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nama Pelanggan")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = MaterialTheme.colors.onBackground, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable {

                }
                .padding(12.dp)
        ) {
            Text("Hotman paris", modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ArrowDropDown, contentDescription = null)
        }
        Spacer(Modifier.height(0.dp))
        Text("Keranjang")
        Spacer(Modifier.weight(1f))
        Text("Total: Rp322.000")
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { }
        ) {
            Text("Bayar")
        }
    }
}