package com.herlianzhang.mikropos.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            "Akun",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        MenuItem(label = "Ubah Profil", icon = Icons.Rounded.AccountCircle) {
            navController.navigate("profile")
        }
        MenuItem(label = "Ganti Password", icon = Icons.Rounded.Password) {
            navController.navigate("change_password")
        }
        MenuItem(label = "Printer", icon = Icons.Rounded.Print) {
            navController.navigate("printer_list")
        }

        Spacer(Modifier.height(0.dp))
        Text(
            "Inventaris",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        MenuItem(label = "Kelola Produk", icon = Icons.Rounded.Inventory2) {
            navController.navigate("product_list")
        }
        MenuItem(label = "Kelola Pelanggan", icon = Icons.Rounded.SupervisedUserCircle) {
            navController.navigate("customer_list")
        }
        MenuItem(label = "Catat Pengeluaran", icon = Icons.Rounded.Paid) {
            navController.navigate("create_expense")
        }
    }
}