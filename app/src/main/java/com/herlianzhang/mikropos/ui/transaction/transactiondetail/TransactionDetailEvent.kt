package com.herlianzhang.mikropos.ui.transaction.transactiondetail

sealed class TransactionDetailEvent {
    data class ShowErrorSnackbar(val message: String?) : TransactionDetailEvent()
    object HideDialog : TransactionDetailEvent()
}