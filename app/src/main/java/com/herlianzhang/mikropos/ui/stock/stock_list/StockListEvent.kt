package com.herlianzhang.mikropos.ui.stock.stock_list

sealed class StockListEvent {
    data class ShowErrorSnackbar(val message: String?) : StockListEvent()
    object SendResult : StockListEvent()
}