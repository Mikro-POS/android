package com.herlianzhang.mikropos.repository

import android.content.Context
import android.net.Uri
import com.herlianzhang.mikropos.api.ApiCaller
import com.herlianzhang.mikropos.api.ApiService
import com.herlianzhang.mikropos.api.InputStreamRequestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val apiService: ApiService,
    private val apiCaller: ApiCaller,
    @ApplicationContext private val context: Context
) {
    fun uploadImage(uri: Uri, displayName: String) = apiCaller {
        val requestBody = InputStreamRequestBody(uri, context.contentResolver)
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "target",
                displayName,
                requestBody
            ).build()
        apiService.uploadImage(body)
    }
}