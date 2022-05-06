package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller
) {
    fun getProducts(
        page: Int,
        limit: Int,
        search: String
    ) = apiCaller {
        apiService.getProducts(page = page, limit = limit, search = search)
    }

    fun getProduct(
        id: Int
    ) = apiCaller {
        apiService.getProduct(id)
    }

    fun createProduct(
        params: Map<String, Any>
    ) = apiCaller {
        apiService.createProduct(params)
    }

    fun updateProduct(
        id: Int,
        params: Map<String, Any>
    ) = apiCaller {
        apiService.updateProduct(id, params)
    }

    fun deleteProduct(
        id: Int
    ) = apiCaller {
        apiService.deleteProduct(id)
    }
}