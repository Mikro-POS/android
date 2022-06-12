package com.herlianzhang.mikropos.ui.stock.createstock

sealed class CreateStockEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateStockEvent()
    object BackWithResult : CreateStockEvent()
}