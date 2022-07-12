package com.herlianzhang.mikropos.ui.home

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.runtime.Composable
import com.herlianzhang.mikropos.ui.common.Screen

@Composable
fun HomeAction(route: String?, viewModel: HomeViewModel, onLogout: () -> Unit) {
    when (route) {
        Screen.Cart.route -> {
            IconButton(onClick = {
                viewModel.navigateToSelectProduct()
            }) {
                Icon(Icons.Rounded.Add, contentDescription = null)
            }
        }
        Screen.TransactionList.route -> {
            IconButton(onClick = { viewModel.navigateToQRScan() }) {
                Icon(Icons.Rounded.QrCodeScanner, contentDescription = null)
            }
        }
        Screen.Menu.route -> {
            IconButton(onClick = { onLogout() }) {
                Icon(Icons.Rounded.Logout, contentDescription = null)
            }
        }
    }
}