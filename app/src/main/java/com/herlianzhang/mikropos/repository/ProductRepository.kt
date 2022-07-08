package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.CreateOrUpdateProduct
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
        data: CreateOrUpdateProduct
    ) = apiCaller {
        apiService.createProduct(data)
    }

    fun updateProduct(
        id: Int,
        data: CreateOrUpdateProduct
    ) = apiCaller {
        apiService.updateProduct(id, data)
    }

    fun deleteProduct(
        id: Int
    ) = apiCaller {
        apiService.deleteProduct(id)
    }
}