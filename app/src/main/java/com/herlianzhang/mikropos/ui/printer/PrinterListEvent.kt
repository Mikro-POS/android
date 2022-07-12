package com.herlianzhang.mikropos.ui.printer

sealed class PrinterListEvent {
    data class ShowErrorSnackbar(val message: String?) : PrinterListEvent()
}