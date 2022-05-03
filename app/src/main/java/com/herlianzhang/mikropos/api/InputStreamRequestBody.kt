package com.herlianzhang.mikropos.api

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayOutputStream

class InputStreamRequestBody(
    private val uri: Uri,
    private val contentResolver: ContentResolver
): RequestBody() {
    override fun contentType(): MediaType? = contentResolver.getType(uri)?.toMediaTypeOrNull()

    override fun contentLength(): Long = -1

    override fun writeTo(sink: BufferedSink) {
        val input = contentResolver.openInputStream(uri)
        val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeStream(input)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        sink.write(baos.toByteArray())
    }
}