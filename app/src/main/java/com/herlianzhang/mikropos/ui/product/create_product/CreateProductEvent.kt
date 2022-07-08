package com.herlianzhang.mikropos.ui.product.create_product

sealed class CreateProductEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateProductEvent()
    object BackWithResult : CreateProductEvent()
}