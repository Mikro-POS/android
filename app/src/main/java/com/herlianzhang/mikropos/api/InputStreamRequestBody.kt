package com.herlianzhang.mikropos.api

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

class InputStreamRequestBody(
    private val uri: Uri,
    private val contentResolver: ContentResolver
): RequestBody() {
    override fun contentType(): MediaType? = contentResolver.getType(uri)?.toMediaTypeOrNull()

    override fun contentLength(): Long = -1

    override fun writeTo(sink: BufferedSink) {
        val input = contentResolver.openInputStream(uri)
        input?.use {
            sink.writeAll(input.source())
        }
    }
}