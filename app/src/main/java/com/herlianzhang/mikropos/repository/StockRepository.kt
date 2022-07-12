package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.CreateStock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller
) {
    fun getStocks(
        productId: Int,
        page: Int,
        limit: Int
    ) = apiCaller {
        apiService.getStocks(productId = productId, page = page, limit = limit)
    }

    fun createStock(
        productId: Int,
        data: CreateStock
    ) = apiCaller {
        apiService.createStock(productId, data)
    }

    fun deleteStock(
        productId: Int,
        stockId: Int
    ) = apiCaller {
        apiService.deleteStock(productId, stockId)
    }
}