package com.herlianzhang.mikropos.ui.common

import com.herlianzhang.mikropos.R

sealed class Screen(val route: String, val label: String, val iconFilled: Int, val iconOutlined: Int) {
    object Cart : Screen("cart", "Keranjang", R.drawable.ic_cart_filled, R.drawable.ic_cart_outlined)
    object TransactionList : Screen("transactionList", "Transaksi", R.drawable.ic_transaction_filled, R.drawable.ic_transaction_outlined)
    object Menu : Screen("menu", "Menu", R.drawable.ic_menu_filled, R.drawable.ic_menu_outlined)

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