package com.herlianzhang.mikropos.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Cart : Screen("cart", "Keranjang", Icons.Rounded.AddShoppingCart)
    object TransactionList : Screen("transactionList", "Transaksi", Icons.Rounded.ReceiptLong)
    object Menu : Screen("menu", "Menu", Icons.Rounded.Menu)

    companion object {
        fun getLabel(route: String?): String {
            return when (route) {
                Cart.route -> Cart.label
                TransactionList.route -> TransactionList.label
                Menu.route -> Menu.label
                else -> ""
            }
        }
    }
}