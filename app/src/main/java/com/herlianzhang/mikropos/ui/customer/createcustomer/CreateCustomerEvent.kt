package com.herlianzhang.mikropos.ui.customer.createcustomer

sealed class CreateCustomerEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateCustomerEvent()
    object BackWithResult : CreateCustomerEvent()
}