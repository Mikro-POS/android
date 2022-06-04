package com.herlianzhang.mikropos.utils.extensions

import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.herlianzhang.mikropos.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun AndroidViewModel.getImageDisplayName(uri: Uri): String {
    return withContext(Dispatchers.Default) {
        val column = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        getApplication<App>().contentResolver
            .query(
                uri,
                column,
                null,
                null,
                null)?.use { cursor ->
                val idDisplayName = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                cursor.moveToFirst()
                return@withContext cursor.getString(idDisplayName)
            }
        return@withContext ""
    }
}