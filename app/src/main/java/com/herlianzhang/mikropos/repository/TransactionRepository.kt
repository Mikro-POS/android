package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.CreateTransaction
import com.herlianzhang.mikropos.vo.PayInstallments
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller,
) {
    fun getTransactions(
        page: Int,
        limit: Int,
        startDate: Long?,
        endDate: Long?,
        isBillingPeriod: Boolean,
        isNotYetPaidOff: Boolean
    ) = apiCaller.invoke {
        apiService.getTransactions(
            page,
            limit,
            startDate,
            endDate,
            isBillingPeriod,
            isNotYetPaidOff
        )
    }

    fun getTransaction(
        id: Int
    ) = apiCaller.invoke { apiService.getTransaction(id) }

    fun createTransaction(
        data: CreateTransaction
    ) = apiCaller.invoke { apiService.createTransaction(data) }

    fun payInstallments(
        id: Int,
        data: PayInstallments
    ) = apiCaller.invoke { apiService.payInstallments(id, data) }

    fun changeTransactionStatusToLost(
        id: Int
    ) = apiCaller.invoke { apiService.changeTransactionStatusToLost(id) }
}