package com.herlianzhang.mikropos.ui.product.productdetail

sealed class ProductDetailEvent {
    data class ShowErrorSnackbar(val message: String?) : ProductDetailEvent()
    object HideDialog : ProductDetailEvent()
    object SetHasChanges : ProductDetailEvent()
    object Back : ProductDetailEvent()
}