package com.herlianzhang.mikropos.ui.transaction.transaction_detail

sealed class TransactionDetailEvent {
    data class ShowErrorSnackbar(val message: String?) : TransactionDetailEvent()
    object HideDialog : TransactionDetailEvent()
}