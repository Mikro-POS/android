package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller
) {
    fun login(
        username: String,
        password: String
    ) = apiCaller {
        apiService.login(username, password)
    }

    fun register(
        params: Map<String, Any>
    ) = apiCaller {
        apiService.register(params)
    }
}