package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.CreateOrUpdateCustomer
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
        data: CreateOrUpdateCustomer
    ) = apiCaller {
        apiService.createCustomer(data)
    }

    fun updateCustomer(
        id: Int,
        data: CreateOrUpdateCustomer
    ) = apiCaller {
        apiService.updateCustomer(id, data)
    }

    fun deleteCustomer(
        id: Int
    ) = apiCaller {
        apiService.deleteCustomer(id)
    }
}