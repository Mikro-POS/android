package com.herlianzhang.mikropos.ui.product.product_list

sealed class ProductListEvent {
    data class NavigateToProductDetail(val id: Int) : ProductListEvent()
}