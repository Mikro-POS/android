package com.herlianzhang.mikropos.repository

import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.vo.ChangePassword
import com.herlianzhang.mikropos.vo.Register
import com.herlianzhang.mikropos.vo.UpdateUser
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
        data: Register
    ) = apiCaller {
        apiService.register(data)
    }

    fun getUserInfo() = apiCaller { apiService.getUserInfo() }

    fun updateUser(
        data: UpdateUser
    ) = apiCaller { apiService.updateUser(data) }

    fun changePassword(
        data: ChangePassword
    ) = apiCaller { apiService.changePassword(data) }
}