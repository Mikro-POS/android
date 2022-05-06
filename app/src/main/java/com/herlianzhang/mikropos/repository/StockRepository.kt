package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
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
        params: Map<String, Any>
    ) = apiCaller {
        apiService.createStock(productId, params)
    }

    fun deleteStock(
        productId: Int,
        stockId: Int
    ) = apiCaller {
        apiService.deleteStock(productId, stockId)
    }
}