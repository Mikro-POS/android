package com.herlianzhang.mikropos.api

import com.herlianzhang.mikropos.vo.*
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

    // customer
    @GET("customers/")
    suspend fun getCustomers(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String = ""
    ): Response<List<Customer>>

    @GET("customers/{customer_id}")
    suspend fun getCustomer(
        @Path("customer_id") customerId: Int
    ): Response<CustomerDetail>

    @POST("customers")
    @JvmSuppressWildcards
    suspend fun createCustomer(
        @Body params: Map<String, Any>
    ): Response<Customer>

    @PATCH("customers/{customer_id}")
    @JvmSuppressWildcards
    suspend fun updateCustomer(
        @Path("customer_id") customerId: Int,
        @Body params: Map<String, Any>
    ): Response<CustomerDetail>

    @DELETE("customers/{customer_id}")
    suspend fun deleteCustomer(
        @Path("customer_id") customerId: Int
    ): Response<Any?>

    // product
    @GET("products/")
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

    @DELETE("products/{product_id}")
    suspend fun deleteProduct(
        @Path("product_id") productId: Int
    ): Response<Any?>

    // stock
    @GET("stocks/{product_id}")
    suspend fun getStocks(
        @Path("product_id") productId: Int,
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String = ""
    ): Response<List<Stock>>

    @POST("stocks/{product_id}")
    @JvmSuppressWildcards
    suspend fun createStock(
        @Path("product_id") productId: Int,
        @Body params: Map<String, Any>
    ): Response<Stock>

    @DELETE("stocks/{product_id}/{stock_id}")
    suspend fun deleteStock(
        @Path("product_id") productId: Int,
        @Path("stock_id") stockId: Int
    ): Response<Any?>

    // transaction
    @POST("transactions")
    suspend fun createTransaction(
        @Body data: CreateTransaction
    ): Response<TransactionDetail>

    // default
    @POST("/upload-image")
    suspend fun uploadImage(
        @Body body: RequestBody
    ): Response<ImageUrl>
}