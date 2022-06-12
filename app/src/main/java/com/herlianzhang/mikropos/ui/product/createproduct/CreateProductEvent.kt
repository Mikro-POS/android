package com.herlianzhang.mikropos.ui.product.createproduct

sealed class CreateProductEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateProductEvent()
    object BackWithResult : CreateProductEvent()
}