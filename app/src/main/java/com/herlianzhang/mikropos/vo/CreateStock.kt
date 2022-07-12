package com.herlianzhang.mikropos.vo

data class CreateStock(
    val supplierName: String?,
    val amount: Int,
    val purchasePrice: Long,
    val source: StockSource
)