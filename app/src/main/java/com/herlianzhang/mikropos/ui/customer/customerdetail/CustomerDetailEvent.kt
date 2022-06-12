package com.herlianzhang.mikropos.ui.customer.customerdetail

sealed class CustomerDetailEvent {
    data class ShowErrorSnackbar(val message: String?) : CustomerDetailEvent()
    object HideDialog : CustomerDetailEvent()
    object SetHasChanges : CustomerDetailEvent()
    object Back : CustomerDetailEvent()
}