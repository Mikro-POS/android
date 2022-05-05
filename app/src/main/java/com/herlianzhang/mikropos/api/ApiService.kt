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

    // supplier
    @GET("suppliers")
    suspend fun getSuppliers(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1,
        @Query("search") search: String = ""
    ): Response<List<Supplier>>

    @GET("suppliers/{supplier_id}")
    suspend fun getSupplier(
        @Path("supplier_id") supplierId: Int
    ): Response<SupplierDetail>

    @POST("suppliers")
    @JvmSuppressWildcards
    suspend fun createSupplier(
        @Body params: Map<String, Any>
    ): Response<Supplier>

    @PATCH("suppliers/{supplier_id}")
    @JvmSuppressWildcards
    suspend fun updateSupplier(
        @Path("supplier_id") supplierId: Int,
        @Body params: Map<String, Any>
    ): Response<SupplierDetail>

    @DELETE("suppliers/{supplier_id}")
    suspend fun deleteSupplier(
        @Path("supplier_id") supplierId: Int
    ): Response<Any?>

    // customer
    @GET("customers")
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

    @DELETE("products/{product_id}")
    suspend fun deleteProduct(
        @Path("product_id") productId: Int
    ): Response<Any?>

    // default
    @POST("/upload-image")
    suspend fun uploadImage(
        @Body body: RequestBody
    ): Response<ImageUrl>
}