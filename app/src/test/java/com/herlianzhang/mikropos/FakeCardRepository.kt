package com.herlianzhang.mikropos

import com.herlianzhang.mikropos.db.cart.Cart
import com.herlianzhang.mikropos.repository.CardRepository
import com.herlianzhang.mikropos.ui.home.HomeEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeCardRepository : CardRepository {
    val testaja = Channel<List<Cart>>()
    override val carts: Flow<List<Cart>>
        get() = testaja.receiveAsFlow()

    override suspend fun getCartById(id: Int): Cart? {
        return null
    }

    override suspend fun insertCart(cart: Cart) {
    }

    override suspend fun delete(cart: Cart) {
    }

    override suspend fun deleteAll() {
    }
}