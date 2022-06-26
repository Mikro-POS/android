package com.herlianzhang.mikropos.ui.checkout

sealed class CheckoutEvent {
    data class ShowErrorSnackbar(val message: String?) : CheckoutEvent()
    data class NavigateToDetail(val id: Int) : CheckoutEvent()
}