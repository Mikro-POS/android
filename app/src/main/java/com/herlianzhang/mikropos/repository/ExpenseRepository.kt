package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.CreateExpense
import com.herlianzhang.mikropos.vo.CreateExpenseCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller
) {
    fun createExpense(
        expense: CreateExpense
    ) = apiCaller {
        apiService.createExpense(expense)
    }

    fun getExpenseCategories(
        page: Int,
        limit: Int
    ) = apiCaller {
        apiService.getExpenseCategories(limit, page)
    }

    fun createExpenseCategory(
        expenseCategory: CreateExpenseCategory
    ) = apiCaller {
        apiService.createExpenseCategory(expenseCategory)
    }
}