package com.herlianzhang.mikropos.ui.product.productlist

sealed class ProductListEvent {
    data class NavigateToProductDetail(val id: Int) : ProductListEvent()
}