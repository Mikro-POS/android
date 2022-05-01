package com.herlianzhang.mikropos.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(icon, contentDescription = null)
        Text(
            label,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Rounded.NavigateNext, contentDescription = null)
    }
}

@Composable
fun SettingScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Akun")
        SettingItem(label = "Ubah Profil", icon = Icons.Rounded.AccountCircle) {}
        SettingItem(label = "Ganti Password", icon = Icons.Rounded.Password) {}
        SettingItem(label = "Laporan", icon = Icons.Rounded.Equalizer) {}
        SettingItem(label = "Printer", icon = Icons.Rounded.Print) {}

        Spacer(Modifier.height(0.dp))
        Text("Inventaris")
        SettingItem(label = "Kelola Produk", icon = Icons.Rounded.Inventory2) {
            navController.navigate("product_list")
        }
        SettingItem(label = "Kelola Pelanggan", icon = Icons.Rounded.SupervisedUserCircle) {}
        SettingItem(label = "Kelola Pemasok", icon = Icons.Rounded.LocalShipping) {}
    }
}