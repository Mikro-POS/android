package com.herlianzhang.mikropos.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.api.ApiResult
import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.repository.CartRepository
import com.herlianzhang.mikropos.repository.TransactionRepository
import com.herlianzhang.mikropos.vo.CreateTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _event = Channel<CheckoutEvent>()
    val event: Flow<CheckoutEvent>
        get() = _event.receiveAsFlow()

    val carts: Flow<List<Cart>>
        get() = cartRepository.carts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalPrice: Flow<Long>
        get() = carts.flatMapLatest {
            var result: Long = 0
            for (cart in it)
                result += cart.price?.times(cart.amount) ?: 0
            flowOf(result)
        }

    fun createTransaction(data: CreateTransaction) {
        viewModelScope.launch {
            transactionRepository.createTransaction(data).collect { result ->
                when(result) {
                    is ApiResult.Loading -> _isLoading.emit(result.state.value)
                    is ApiResult.Success -> {
                        val detailId = result.data?.id
                        if (detailId != null) {
                            cartRepository.deleteAll()
                            _event.send(CheckoutEvent.NavigateToDetail(detailId))
                        }
                    }
                    is ApiResult.Failed -> _event.send(CheckoutEvent.ShowErrorSnackbar(result.message))
                }
            }
        }
    }
}