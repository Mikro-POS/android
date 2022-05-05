package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller
) {
    fun getCustomers(
        page: Int,
        limit: Int,
        search: String
    ) = apiCaller {
        apiService.getCustomers(page = page, limit = limit, search = search)
    }
    fun getCustomer(
        id: Int
    ) = apiCaller {
        apiService.getCustomer(id)
    }

    fun createCustomer(
        params: Map<String, Any>
    ) = apiCaller {
        apiService.createCustomer(params)
    }

    fun updateCustomer(
        id: Int,
        params: Map<String, Any>
    ) = apiCaller {
        apiService.updateCustomer(id, params)
    }

    fun deleteCustomer(
        id: Int
    ) = apiCaller {
        apiService.deleteCustomer(id)
    }
}