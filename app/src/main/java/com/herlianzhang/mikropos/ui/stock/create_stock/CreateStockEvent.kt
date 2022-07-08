package com.herlianzhang.mikropos.ui.stock.create_stock

sealed class CreateStockEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateStockEvent()
    object BackWithResult : CreateStockEvent()
}