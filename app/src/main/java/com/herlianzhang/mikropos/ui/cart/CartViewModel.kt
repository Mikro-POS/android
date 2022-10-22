package com.herlianzhang.mikropos.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CardRepository
) : ViewModel() {
    val carts: Flow<List<Cart>>
        get() = cartRepository.carts

    @OptIn(ExperimentalCoroutinesApi::class)
    val isCartEmpty: Flow<Boolean>
        get() = carts.flatMapLatest { flowOf(it.isEmpty()) }

    fun increaseAmount(cart: Cart) {
        val newCart = cart.copy(amount = cart.amount + 1)
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.insertCart(newCart)
        }
    }

    fun decreaseAmount(cart: Cart) {
        val newCart = cart.copy(amount = cart.amount - 1)
        viewModelScope.launch(Dispatchers.IO) {
            if (newCart.amount <= 0) {
                cartRepository.delete(newCart)
            } else {
                cartRepository.insertCart(newCart)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.deleteAll()
        }
    }
}