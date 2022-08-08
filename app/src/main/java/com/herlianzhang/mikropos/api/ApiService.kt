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
    ): Response<Token>

    @POST("users/register")
    @JvmSuppressWildcards
    suspend fun register(
        @Body data: Register
    ): Response<Token>

    @GET("users/me")
    suspend fun getUserInfo(): Response<User>

    @PATCH("users/update")
    suspend fun updateUser(
        @Body data: UpdateUser
    ): Response<User>

    @POST("users/change-password")
    @JvmSuppressWildcards
    suspend fun changePassword(
        @Body data: ChangePassword
    ): Response<Any?>

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
        @Body data: CreateOrUpdateCustomer
    ): Response<Customer>

    @PATCH("customers/{customer_id}")
    @JvmSuppressWildcards
    suspend fun updateCustomer(
        @Path("customer_id") customerId: Int,
        @Body data: CreateOrUpdateCustomer
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
        @Body data: CreateOrUpdateProduct
    ): Response<Product>

    @PATCH("products/{product_id}")
    @JvmSuppressWildcards
    suspend fun updateProduct(
        @Path("product_id") productId: Int,
        @Body data: CreateOrUpdateProduct
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
        @Query("is_expired") isExpired: Boolean
    ): Response<List<Stock>>

    @POST("stocks/{product_id}")
    @JvmSuppressWildcards
    suspend fun createStock(
        @Path("product_id") productId: Int,
        @Body data: CreateStock
    ): Response<Stock>

    @DELETE("stocks/{product_id}/{stock_id}")
    suspend fun deleteStock(
        @Path("product_id") productId: Int,
        @Path("stock_id") stockId: Int
    ): Response<Any?>

    @POST("stocks/{product_id}/{stock_id}/refund")
    suspend fun refundStock(
        @Path("product_id") productId: Int,
        @Path("stock_id") stockId: Int,
        @Body data: RefundStock
    ): Response<Stock>

    // transaction
    @GET("transactions")
    suspend fun getTransactions(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1,
        @Query("start_date") startDate: Long? = null,
        @Query("end_date") endDate: Long? = null,
        @Query("filter_billing_period") isBillingPeriod: Boolean = false,
        @Query("filter_not_yet_paid_off") isNotYetPaidOff: Boolean = false
    ): Response<List<Transaction>>

    @GET("transactions/{transaction_id}")
    suspend fun getTransaction(
        @Path("transaction_id") transactionId: Int
    ): Response<TransactionDetail>

    @POST("transactions")
    suspend fun createTransaction(
        @Body data: CreateTransaction
    ): Response<TransactionDetail>

    @POST("transactions/{transaction_id}/pay-installments")
    suspend fun payInstallments(
        @Path("transaction_id") transactionId: Int,
        @Body data: PayInstallments
    ): Response<TransactionDetail>

    @POST("transactions/{transaction_id}/change-to-lost")
    suspend fun changeTransactionStatusToLost(
        @Path("transaction_id") transactionId: Int
    ): Response<TransactionDetail>

    // external expenses
    @POST("external_expenses")
    suspend fun createExpense(
        @Body data: CreateExpense
    ): Response<Any?>

    @GET("external_expenses/categories")
    suspend fun getExpenseCategories(
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1
    ): Response<List<ExpenseCategory>>

    @POST("external_expenses/categories")
    suspend fun createExpenseCategory(
        @Body data: CreateExpenseCategory
    ): Response<ExpenseCategory>

    // default
    @POST("/upload-image")
    suspend fun uploadImage(
        @Body body: RequestBody
    ): Response<ImageUrl>
}