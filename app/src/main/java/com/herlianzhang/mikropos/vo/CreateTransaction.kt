package com.herlianzhang.mikropos.vo

data class CreateTransactionItem(
    val productId: Int,
    val amount: Int
)

data class CreateTransaction(
    val customerId: Int?,
    val status: TransactionStatus,
    val totalInstallment: Int?,
    val debtDue: Long?,
    val items: List<CreateTransactionItem>
)