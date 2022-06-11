package com.herlianzhang.mikropos.ui.customer.createcustomer

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.App
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.CustomerRepository
import com.herlianzhang.mikropos.repository.ImageRepository
import com.herlianzhang.mikropos.utils.extensions.getImageDisplayName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateCustomerViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    private val customerRepository: CustomerRepository,
    app: Application
): AndroidViewModel(app) {
    private var currUrl: String? = null
    private var currBitmap: Bitmap? = null
    private var uploadJob: Job? = null

    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?>
        get() = _bitmap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean>
        get() = _isUploadingImage

    private val _event = Channel<CreateCustomerEvent>()
    val event: Flow<CreateCustomerEvent>
        get() = _event.receiveAsFlow()

    fun uploadImage(uri: Uri) {
        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            setBitmap(uri)
            imageRepository.uploadImage(uri, getImageDisplayName(uri)).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isUploadingImage.emit(result.state.value)
                    is ApiResult.Failed -> {
                        _bitmap.emit(currBitmap)
                        _event.send(CreateCustomerEvent.ShowErrorSnackbar(result.message))
                    }
                    is ApiResult.Success -> {
                        currBitmap = _bitmap.value
                        currUrl = result.data?.url
                    }
                }
            }
        }
    }

    fun createCustomer(
        name: String,
        phoneNumber: String,
        address: String
    ) {
        val params = mutableMapOf<String, Any>()
        params["name"] = name
        params["phone_number"] = phoneNumber
        if (address.isNotBlank())
            params["address"] = address
        currUrl?.let { url ->
            params["photo"] = url
        }
        viewModelScope.launch {
            customerRepository.createCustomer(params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(CreateCustomerEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> _event.send(CreateCustomerEvent.BackWithResult)
                }
            }
        }
    }

    private suspend fun setBitmap(uri: Uri) {
        withContext(Dispatchers.Default) {
            if (Build.VERSION.SDK_INT < 28) {
                _bitmap.emit(
                    MediaStore.Images
                        .Media.getBitmap(getApplication<App>().contentResolver, uri))
            } else {
                val source = ImageDecoder
                    .createSource(getApplication<App>().contentResolver, uri)
                _bitmap.emit(ImageDecoder.decodeBitmap(source))
            }
        }
    }
}