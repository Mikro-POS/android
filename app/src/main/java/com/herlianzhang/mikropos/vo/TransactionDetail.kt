package com.herlianzhang.mikropos.vo

data class TransactionDetail(
    val id: Int,
    val status: TransactionStatus,
    val currentDebt: Long?,
    val totalInstallment: Int?,
    val currentInstallment: Int?,
    val debtDue: Long?,
    val totalProfit: Long?,
    val totalPrice: Long?,
    val createdAt: Long?,
    val customer: CustomerDetail?,
    val items: List<TransactionItem>
)
