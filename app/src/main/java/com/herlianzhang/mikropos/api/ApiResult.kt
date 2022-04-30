package com.herlianzhang.mikropos.api

sealed class LoadingState(val value: Boolean) {
    class Start : LoadingState(true)
    class End : LoadingState(false)
}

sealed class ApiResult<T> {
    data class Success<T>(val data: T?) : ApiResult<T>()
    data class Failed<T>(val message: String? = null, val code: Int = 400) : ApiResult<T>()
    data class Loading<T>(val state: LoadingState) : ApiResult<T>()
}