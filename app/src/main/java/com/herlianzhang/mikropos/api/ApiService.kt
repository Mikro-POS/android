package com.herlianzhang.mikropos.api

import com.herlianzhang.mikropos.vo.ImageUrl
import com.herlianzhang.mikropos.vo.Product
import com.herlianzhang.mikropos.vo.ProductDetail
import com.herlianzhang.mikropos.vo.User
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

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

    // product
    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String = ""
    ): Response<List<Product>>

    @GET("products/{product_id}")
    suspend fun getProduct(
        @Path("product_id") productId: Int
    ): Response<ProductDetail>

    @POST("products")
    @JvmSuppressWildcards
    suspend fun createProduct(
        @Body params: Map<String, Any>
    ): Response<Product>

    @PATCH("products/{product_id}")
    @JvmSuppressWildcards
    suspend fun updateProduct(
        @Path("product_id") productId: Int,
        @Body params: Map<String, Any>
    ): Response<ProductDetail>

    // default
    @POST("/upload-image")
    suspend fun uploadImage(
        @Body body: RequestBody
    ): Response<ImageUrl>
}