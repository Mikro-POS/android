package com.herlianzhang.mikropos.ui.supplierlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.api.LoadingState
import com.herlianzhang.mikropos.repository.SupplierRepository
import com.herlianzhang.mikropos.vo.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierListViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {
    private val limit: Int = 20
    private var page: Int = 1
    private var search: String = ""
    private var isLastPage: Boolean = false
    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    private val _suppliers = MutableStateFlow(listOf<Supplier>())
    val suppliers: StateFlow<List<Supplier>>
        get() = _suppliers

    private val _isSupplierEmpty = MutableStateFlow(false)
    val isSupplierEmpty: StateFlow<Boolean>
        get() = _isSupplierEmpty

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    private val _isLoadMore = MutableStateFlow(false)
    val isLoadMore: StateFlow<Boolean>
        get() = _isLoadMore

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean>
        get() = _isError

    init {
        getSuppliers()
    }

    fun search(value: String, check: Boolean = true) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (check) {
                delay(1000)
                if (value == search.trim())
                    return@launch
            }
            search = value.trim()
            page = 1
            isLastPage = false
            _suppliers.emit(emptyList())
            getSuppliers()
        }
    }

    fun loadMore() {
        if (isLastPage || searchJob?.isActive == true || fetchJob?.isActive == true)
            return
        page += 1
        getSuppliers()
    }

    fun tryAgain() {
        viewModelScope.launch {
            _isError.emit(false)
            getSuppliers()
        }
    }

    private fun getSuppliers() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            supplierRepository.getSuppliers(page, limit, search).collect { result ->
                when(result) {
                    is ApiResult.Success -> handleSuccess(result.data ?: emptyList())
                    is ApiResult.Failed -> handleError()
                    is ApiResult.Loading -> handleLoading(result.state)
                }
            }
        }
    }

    private suspend fun handleSuccess(data: List<Supplier>) {
        val curr = _suppliers.value.toMutableList()
        isLastPage = data.size < limit
        curr.addAll(data)
        _isSupplierEmpty.emit(curr.isEmpty())
        _suppliers.emit(curr)
    }

    private suspend fun handleError() {
        _isError.emit(true)
    }

    private suspend fun handleLoading(state: LoadingState) {
        when (state) {
            is LoadingState.Start -> {
                if (page == 1)
                    _isLoading.emit(page == 1)
            }
            is LoadingState.End -> {
                _isLoadMore.emit(!isLastPage)
                _isLoading.emit(false)
            }
        }
    }
}