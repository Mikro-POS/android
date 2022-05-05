package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplierRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller
) {
    fun getSuppliers(
        page: Int,
        limit: Int,
        search: String
    ) = apiCaller {
        apiService.getSuppliers(page = page, limit = limit, search = search)
    }
    fun getSupplier(
        id: Int
    ) = apiCaller {
        apiService.getSupplier(id)
    }

    fun createSupplier(
        params: Map<String, Any>
    ) = apiCaller {
        apiService.createSupplier(params)
    }

    fun updateSupplier(
        id: Int,
        params: Map<String, Any>
    ) = apiCaller {
        apiService.updateSupplier(id, params)
    }

    fun deleteSupplier(
        id: Int
    ) = apiCaller {
        apiService.deleteSupplier(id)
    }
}