package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.CreateTransaction
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller,
) {
    fun createTransaction(data: CreateTransaction) =
        apiCaller.invoke { apiService.createTransaction(data) }
}