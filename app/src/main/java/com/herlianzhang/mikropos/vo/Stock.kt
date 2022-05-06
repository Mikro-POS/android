package com.herlianzhang.mikropos.vo

import java.text.SimpleDateFormat
import java.util.*

enum class StockSource {
    SUPPLIER,
    CUSTOMER
}

data class Stock(
    val id: Int,
    val amount: Int?,
    val soldAmount: Int?,
    val purchasePrice: Int?,
    val source: StockSource?,
    val supplier: Supplier?,
    val createdAt: String?,
)