package com.herlianzhang.mikropos.vo

data class TransactionItem(
    val id: Int,
    val productId: Int,
    val amount: Int?,
    val profit: Long?,
    val price: Long?,
    val stocks: List<TransactionStock>
)
