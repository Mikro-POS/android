package com.herlianzhang.mikropos.vo

data class CreateExpense(
    val categoryId: Int,
    val nominal: Long,
    val date: Long,
    val description: String
)
