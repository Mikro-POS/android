package com.herlianzhang.mikropos.vo

data class TransactionDetail(
    val id: Int,
    val status: TransactionStatus,
    val currentDebt: Int?,
    val totalInstallment: Int?,
    val currentInstallment: Int?,
    val debtDue: Long?,
    val totalProfit: Long?,
    val totalPrice: Long?,
    val items: List<TransactionItem>
)
