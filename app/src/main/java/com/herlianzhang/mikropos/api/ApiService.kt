package com.herlianzhang.mikropos.api

import com.herlianzhang.mikropos.vo.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    // user
    @POST("users/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<User>

    @POST("users/register")
    @JvmSuppressWildcards
    suspend fun register(
        @Body params: Map<String, Any>
    ): Response<User>
}