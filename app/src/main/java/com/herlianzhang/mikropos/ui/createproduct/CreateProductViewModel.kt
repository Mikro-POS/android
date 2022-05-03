package com.herlianzhang.mikropos.ui.createproduct

import android.app.Application
import android.net.Uri
import android.provider.MediaStore.Images.Media.DISPLAY_NAME
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.App
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.ImageRepository
import com.herlianzhang.mikropos.repository.ProductRepository
import com.herlianzhang.mikropos.vo.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateProductEvent {
    data class ShowErrorSnackbar(val message: String?, val uri: Uri? = null) : CreateProductEvent()
    data class BackWithResult(val product: Product?) : CreateProductEvent()
}

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val productRepository: ProductRepository,
    app: Application
): AndroidViewModel(app) {
    private var currUri: Uri? = null
    private var currUrl: String? = null
    private var uploadJob: Job? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean>
        get() = _isUploadingImage

    private val _event = Channel<CreateProductEvent>()
    val event: Flow<CreateProductEvent>
        get() = _event.receiveAsFlow()

    fun uploadImage(uri: Uri) {
        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            imageRepository.uploadImage(uri, getImageDisplayName(uri)).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isUploadingImage.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(CreateProductEvent.ShowErrorSnackbar(result.message, currUri))
                    is ApiResult.Success -> {
                        currUri = uri
                        currUrl = result.data?.url
                    }
                }
            }
        }
    }

    fun createProduct(
        name: String,
        price: Long,
        sku: String
    ) {
        val params = mutableMapOf<String, Any>()
        params["name"] = name
        params["price"] = price
        if (sku.isNotBlank())
            params["sku"] = sku
        currUrl?.let { url ->
            params["photo"] = url
        }
        viewModelScope.launch {
            productRepository.crateProduct(params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(CreateProductEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> _event.send(CreateProductEvent.BackWithResult(result.data))
                }
            }
        }
    }

    private fun getImageDisplayName(uri: Uri): String {
        val column = arrayOf(DISPLAY_NAME)
        getApplication<App>().contentResolver
            .query(
                uri,
                column,
                null,
                null,
                null)?.use { cursor ->
                val idDisplayName = cursor.getColumnIndex(DISPLAY_NAME)
                cursor.moveToFirst()
                return cursor.getString(idDisplayName)
            }
        return ""
    }
}