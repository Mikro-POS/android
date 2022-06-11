package com.herlianzhang.mikropos.ui.stock.stocklist

sealed class StockListEvent {
    data class ShowErrorSnackbar(val message: String?) : StockListEvent()
}