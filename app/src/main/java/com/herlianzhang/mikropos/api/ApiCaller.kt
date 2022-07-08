package com.herlianzhang.mikropos.api

import com.google.gson.Gson
import com.herlianzhang.mikropos.vo.ApiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import javax.inject.Inject

class ApiCaller @Inject constructor(
    private val gson: Gson
) {
    operator fun <T> invoke(call: suspend () -> Response<T>) = flow {
        try {
            val response = call()
            if (response.isSuccessful) {
                val data = response.body()
                emit(ApiResult.Success(data))
            } else {
                val message = getErrorMessage(response.errorBody()?.string())
                emit(ApiResult.Failed(message, response.code()))
            }
        } catch(e: ConnectException) {
            emit(ApiResult.Failed(message = "Jaringan internet anda bermasalah, coba lagi nanti"))
        } catch (e: IOException) {
            emit(ApiResult.Failed(message = "Anda tidak memiliki akses Internet"))
        }
    }.onStart {
        emit(ApiResult.Loading(LoadingState.Start()))
    }.onCompletion {
        emit(ApiResult.Loading(LoadingState.End()))
    }.flowOn(Dispatchers.IO)

    private fun getErrorMessage(json: String?): String? {
        if (json == null) return null
        return try {
            val adapter = gson.getAdapter(ApiError::class.java)
            adapter.fromJson(json).detail
        } catch(e: Exception) {
            null
        }
    }
}