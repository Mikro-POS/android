package com.herlianzhang.mikropos.vo

data class Transaction(
    val id: Int,
    val status: TransactionStatus,
    val totalPrice: Long?,
    val createdAt: Long?,
    val customer: CustomerDetail?,
    val totalOtherItems: Int?,
    val item: TransactionItem?
)
