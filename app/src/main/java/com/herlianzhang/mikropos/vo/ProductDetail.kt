package com.herlianzhang.mikropos.vo

data class ProductDetail(
    val id: Int,
    val name: String?,
    val price: Int?,
    val sku: String?,
    val photo: String?,
    val totalStock: Int?
)
