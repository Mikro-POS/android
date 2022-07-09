package com.herlianzhang.mikropos.ui.product.create_product

sealed class CreateProductEvent {
    data class ShowErrorSnackbar(val message: String?) : CreateProductEvent()
    data class NavigateToDetail(val id: Int?) : CreateProductEvent()
}