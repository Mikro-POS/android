package com.herlianzhang.mikropos.ui.supplier.supplierdetail

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.repository.ImageRepository
import com.herlianzhang.mikropos.repository.SupplierRepository
import com.herlianzhang.mikropos.utils.getImageDisplayName
import com.herlianzhang.mikropos.vo.SupplierDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SupplierDetailEvent {
    data class ShowErrorSnackbar(val message: String?) : SupplierDetailEvent()
    object HideDialog : SupplierDetailEvent()
    object SetHasChanges : SupplierDetailEvent()
    object Back : SupplierDetailEvent()
}

@HiltViewModel
class SupplierDetailViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository,
    private val imageRepository: ImageRepository,
    app: Application
): AndroidViewModel(app) {
    private var id: Int = 0
    private var updateJob: Job? = null
    private var uploadJob: Job? = null

    private val _data = MutableStateFlow<SupplierDetail?>(null)
    val data: StateFlow<SupplierDetail?>
        get() = _data

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean>
        get() = _isUploadingImage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    private val _isNotFound = MutableStateFlow(false)
    val isNotFound: StateFlow<Boolean>
        get() = _isNotFound

    private val _isDialogLoading = MutableStateFlow(false)
    val isDialogLoading: StateFlow<Boolean>
        get() = _isDialogLoading

    private val _event = Channel<SupplierDetailEvent>()
    val event: Flow<SupplierDetailEvent>
        get() = _event.receiveAsFlow()

    fun setSupplierId(id: Int) {
        this.id = id
        getSupplier()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getSupplier()
        }
    }

    private fun getSupplier() {
        viewModelScope.launch {
            supplierRepository.getSupplier(id).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Success -> _data.emit(result.data)
                    is ApiResult.Failed -> {
                        when(result.code) {
                            404 -> _isNotFound.emit(true)
                            else -> _isError.emit(true)
                        }
                    }
                }
            }
        }
    }

    fun updateSupplier(params: Map<String, Any>, fromDialog: Boolean = true) {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            supplierRepository.updateSupplier(id, params).collect { result ->
                when(result) {
                    is ApiResult.Loading -> {
                        if (fromDialog)
                            _isDialogLoading.emit(result.state.value)
                        else
                            _isUploadingImage.emit(result.state.value)
                    }
                    is ApiResult.Success -> {
                        _event.send(SupplierDetailEvent.SetHasChanges)
                        _event.send(SupplierDetailEvent.HideDialog)
                        _data.emit(result.data)
                    }
                    is ApiResult.Failed -> _event.send(SupplierDetailEvent.ShowErrorSnackbar(result.message))
                }
            }
        }
    }

    fun cancelUpdateSupplier() {
        viewModelScope.launch {
            _isDialogLoading.emit(false)
            updateJob?.cancel()
        }
    }

    fun deleteSupplier() {
        viewModelScope.launch {
            supplierRepository.deleteSupplier(id).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Success -> {
                        _event.send(SupplierDetailEvent.SetHasChanges)
                        _event.send(SupplierDetailEvent.Back)
                    }
                    is ApiResult.Failed -> _event.send(SupplierDetailEvent.ShowErrorSnackbar(result.message))
                }
            }
        }
    }

    fun uploadImage(uri: Uri) {
        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            imageRepository.uploadImage(uri, getImageDisplayName(uri)).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isUploadingImage.emit(result.state.value)
                    is ApiResult.Failed -> _event.send(SupplierDetailEvent.ShowErrorSnackbar(result.message))
                    is ApiResult.Success -> {
                        result.data?.url?.let { url ->
                            val params = mapOf("photo" to url)
                            updateSupplier(params, false)
                        }
                    }
                }
            }
        }
    }
}